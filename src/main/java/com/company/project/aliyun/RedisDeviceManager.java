package com.company.project.aliyun;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.util.DateUtil;

/**
 * Redis 设备在线状态管理
 * 负责管理设备的在线状态、在线列表以及设备信息的缓存。
 */
@Component
public class RedisDeviceManager {

    private static final Logger log = LoggerFactory.getLogger(RedisDeviceManager.class);

    // Redis Key 前缀
    private static final String KEY_PREFIX_ONLINE_LIST = "bzj:device:online:";
    private static final String KEY_PREFIX_DEVICE_INFO = "bzj:device:info:";
    private static final String KEY_PREFIX_SEEDER_ACCUM = "bzj:seeder:accum:";

    // 默认过期时间（单位：天）
    private static final long DEFAULT_EXPIRE_DAYS = 365;
    // 在线列表的过期时间（单位：天）
    private static final long ONLINE_LIST_EXPIRE_DAYS = 365;

    // 日期格式化
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 获取今日在线过的设备列表
     * @return 设备实体列表
     */
    public List<DataBzjDeviceEntity> getTodayOnlineDeviceList() {
        String today = getCurrentDateString();
        log.info("获取今日[{}]在线设备列表...", today);
        return getOnlineDeviceListByDate(today);
    }

    /**
     * 获取昨日在线过的设备列表
     * @return 设备实体列表
     */
    public List<DataBzjDeviceEntity> getYesterdayOnlineDeviceList() {
        String yesterday = getYesterdayDateString();
        log.info("获取昨日[{}]在线设备列表...", yesterday);
        return getOnlineDeviceListByDate(yesterday);
    }

    /**
     * 获取指定日期在线过的设备列表
     * @param dayString 日期字符串，格式 "yyyy-MM-dd"
     * @return 设备实体列表
     */
    public List<DataBzjDeviceEntity> getOnlineDeviceList(String dayString) {
        log.info("获取日期[{}]在线设备列表...", dayString);
        return getOnlineDeviceListByDate(dayString);
    }

    /**
     * 获取指定日期（相对于今天的偏移量）在线过的设备列表
     * @param offsetDays 偏移天数（0=今天，-1=昨天，1=明天，-7=一周前等）
     * @return 设备实体列表
     */
    public List<DataBzjDeviceEntity> getOnlineDeviceListByOffset(int offsetDays) {
        String targetDate = getDateByOffset(offsetDays);
        log.info("获取偏移[{}]天的日期[{}]在线设备列表...", offsetDays, targetDate);
        return getOnlineDeviceListByDate(targetDate);
    }

    /**
     * 发现一个上线的设备，并将其加入今日在线列表
     * @param device 上线的设备实体
     * @return 如果是今日新增的在线设备返回 true，如果已经存在则返回 false
     */
    public boolean addOnlineDevice(DataBzjDeviceEntity device) {
        if (device == null || device.getLotId() == null) {
            log.warn("尝试添加一个null或lotId为null的设备到在线列表。");
            return false;
        }

        String lotId = device.getLotId();
        String today = getCurrentDateString();
        String onlineListKey = generateOnlineListKey(today);

        // 使用 RSet 来存储当天的在线设备ID，利用其唯一性
        RSet<String> onlineSet = redissonClient.getSet(onlineListKey);

        // sadd 命令返回 boolean，如果元素已存在则返回 false，新添加则返回 true
        boolean isNewlyAdded = onlineSet.add(lotId);

        if (isNewlyAdded) {
            // 如果是新添加的设备，设置列表的过期时间
            onlineSet.expire(ONLINE_LIST_EXPIRE_DAYS, TimeUnit.DAYS);
            log.info("设备 [{}] 是今日 [{}] 新上线的设备。", lotId, today);

            // 同时，将设备的完整信息缓存到 Hash 中，方便快速查询
            cacheDeviceInfo(device);
        } else {
            log.info("设备 [{}] 在今日 [{}] 已存在于在线列表中。", lotId, today);
            // 即使设备已在列表中，也更新其信息缓存，确保信息最新
            cacheDeviceInfo(device);
        }

        return isNewlyAdded;
    }

    /**
     * 获取所有已缓存的设备信息（扫描 Redis 中所有设备信息 key）
     * @return 设备实体列表
     */
    public List<DataBzjDeviceEntity> getAllCachedDevices() {
        // 使用 Redisson 的 Keys 扫描所有设备信息 key
        Iterable<String> keys = redissonClient.getKeys().getKeysByPattern(KEY_PREFIX_DEVICE_INFO + "*");
        List<DataBzjDeviceEntity> devices = new ArrayList<>();
        for (String key : keys) {
            RBucket<DataBzjDeviceEntity> bucket = redissonClient.getBucket(key);
            DataBzjDeviceEntity device = bucket.get();
            if (device != null) {
                devices.add(device);
            }
        }
        log.info("获取到 {} 个缓存的设备信息", devices.size());
        return devices;
    }

    // ================== 修改 syncAndGetChangedDevices 方法 ==================

    /**
     * 同步设备列表并返回有变化的设备（deviceStatus 变化或新增）
     * 同时保证缓存与入参 latestDevices 完全一致（删除不再存在的设备缓存，并覆盖所有设备信息）
     * @param latestDevices 最新的设备列表
     * @return 发生变化的设备列表（包含新增和 deviceStatus 改变的设备）
     */
    public List<DataBzjDeviceEntity> syncAndGetChangedDevices(List<DataBzjDeviceEntity> latestDevices) {
        if (latestDevices == null) {
            latestDevices = Collections.emptyList();
        }

        // 1. 获取当前所有缓存的设备ID
        Set<String> cachedIds = new HashSet<>();
        Iterable<String> keys = redissonClient.getKeys().getKeysByPattern(KEY_PREFIX_DEVICE_INFO + "*");
        for (String key : keys) {
            String lotId = key.substring(KEY_PREFIX_DEVICE_INFO.length());
            cachedIds.add(lotId);
        }

        // 2. 构建最新设备ID集合和映射
        Set<String> latestIds = new HashSet<>();
        Map<String, DataBzjDeviceEntity> latestDeviceMap = new HashMap<>();
        for (DataBzjDeviceEntity device : latestDevices) {
            if (device.getLotId() != null) {
                latestIds.add(device.getLotId());
                latestDeviceMap.put(device.getLotId(), device);
            }
        }

        // 3. 找出需要删除的设备ID（在缓存中但不在最新列表中）
        Set<String> toDeleteIds = new HashSet<>(cachedIds);
        toDeleteIds.removeAll(latestIds);

        // 4. 删除不再存在的设备缓存
        for (String lotId : toDeleteIds) {
            String deviceInfoKey = generateDeviceInfoKey(lotId);
            RBucket<DataBzjDeviceEntity> bucket = redissonClient.getBucket(deviceInfoKey);
            bucket.delete();
            log.info("删除已不存在的设备缓存，lotId: {}", lotId);
        }

        // 5. 处理最新设备列表：检查变化并更新缓存
        List<DataBzjDeviceEntity> changedDevices = new ArrayList<>();
        for (DataBzjDeviceEntity newDevice : latestDevices) {
            if (newDevice.getLotId() == null) {
                log.warn("设备缺少 lotId，跳过处理");
                continue;
            }

            // 从缓存中获取旧设备信息（注意：可能已被上面删除，所以可能为null）
            DataBzjDeviceEntity oldDevice = getDeviceInfoFromCache(newDevice.getLotId());
            boolean statusChanged = false;

            if (oldDevice == null) {
                statusChanged = true;
                log.debug("发现新设备，lotId: {}", newDevice.getLotId());
            } else {
                // 对比 deviceStatus 是否变化
                String oldStatus = oldDevice.getDeviceStatus();
                String newStatus = newDevice.getDeviceStatus();
                if (!Objects.equals(oldStatus, newStatus)) {
                    statusChanged = true;
                    log.debug("设备 {} 状态发生变化: {} -> {}", newDevice.getLotId(), oldStatus, newStatus);
                }
            }

            // 如果状态发生变化，加入变化列表
            if (statusChanged) {
                changedDevices.add(newDevice);
            }

            // 无论状态是否变化，都用最新设备信息覆盖缓存（保证缓存与入参完全一致）
            cacheDeviceInfo(newDevice);

            // 如果设备在线，自动加入今日在线列表（内部处理重复和过期时间）
            if ("ONLINE".equalsIgnoreCase(newDevice.getDeviceStatus())) {
                addOnlineDevice(newDevice);
            }
        }

        log.info("同步完成：共处理 {} 个设备，其中 {} 个设备状态有变化（新增或状态变更），删除 {} 个不再存在的设备缓存",
                latestDevices.size(), changedDevices.size(), toDeleteIds.size());
        return changedDevices;
    }
    
    /**
     * 根据日期获取在线设备列表的核心逻辑
     * @param dateString 日期 "yyyy-MM-dd"
     * @return 设备实体列表
     */
    private List<DataBzjDeviceEntity> getOnlineDeviceListByDate(String dateString) {
        String onlineListKey = generateOnlineListKey(dateString);
        RSet<String> onlineSet = redissonClient.getSet(onlineListKey);

        // 获取当天所有在线设备的ID集合
        Set<String> deviceIds = onlineSet.readAll();
        if (deviceIds == null || deviceIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据ID列表，从缓存中批量获取设备详细信息
        List<DataBzjDeviceEntity> deviceList = new ArrayList<>();
        for (String lotId : deviceIds) {
            DataBzjDeviceEntity device = getDeviceInfoFromCache(lotId);
            if (device != null) {
                deviceList.add(device);
            } else {
                // 如果缓存中没有，可以在这里添加从数据库查询的逻辑作为降级方案
                log.warn("在缓存中未找到设备信息, lotId: {}", lotId);
                // device = deviceMapper.selectById(lotId);
                // if (device != null) {
                //     cacheDeviceInfo(device); // 查到后放入缓存
                //     deviceList.add(device);
                // }
            }
        }
        return deviceList;
    }

    /**
     * 将设备信息缓存到 Redis Hash 中
     * @param device 设备实体
     */
    private void cacheDeviceInfo(DataBzjDeviceEntity device) {
        if (device == null || device.getLotId() == null) {
            return;
        }
        String deviceInfoKey = generateDeviceInfoKey(device.getLotId());
        RBucket<DataBzjDeviceEntity> bucket = redissonClient.getBucket(deviceInfoKey);
        // 设置设备信息，并赋予一个较长的过期时间
        bucket.set(device, DEFAULT_EXPIRE_DAYS, TimeUnit.DAYS);
    }

    /**
     * 从 Redis Hash 中获取设备信息
     * @param lotId 设备ID
     * @return 设备实体，如果不存在则返回 null
     */
    private DataBzjDeviceEntity getDeviceInfoFromCache(String lotId) {
        if (lotId == null) {
            return null;
        }
        String deviceInfoKey = generateDeviceInfoKey(lotId);
        RBucket<DataBzjDeviceEntity> bucket = redissonClient.getBucket(deviceInfoKey);
        return bucket.get();
    }

    /**
     * 生成在线列表的 Redis Key
     * @param dateString 日期 "yyyy-MM-dd"
     * @return 完整的 Redis Key
     */
    private String generateOnlineListKey(String dateString) {
        return KEY_PREFIX_ONLINE_LIST + dateString;
    }

    /**
     * 生成设备信息的 Redis Key
     * @param lotId 设备ID
     * @return 完整的 Redis Key
     */
    private String generateDeviceInfoKey(String lotId) {
        return KEY_PREFIX_DEVICE_INFO + lotId;
    }

    /**
     * 获取当前日期的字符串 "yyyy-MM-dd"
     * @return 日期字符串
     */
    private String getCurrentDateString() {
        return DATE_FORMAT.format(new Date());
    }

    /**
     * 获取昨天的日期字符串 "yyyy-MM-dd"
     * @return 昨天的日期字符串
     */
    private String getYesterdayDateString() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return DATE_FORMAT.format(calendar.getTime());
    }

    /**
     * 获取相对于今天偏移指定天数的日期字符串
     * @param offsetDays 偏移天数（正数为未来，负数为过去）
     * @return 日期字符串 "yyyy-MM-dd"
     */
    private String getDateByOffset(int offsetDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, offsetDays);
        return DATE_FORMAT.format(calendar.getTime());
    }

    /**
     * 获取指定日期范围内的在线设备列表（去重）
     * @param startDate 开始日期 "yyyy-MM-dd"
     * @param endDate 结束日期 "yyyy-MM-dd"
     * @return 设备实体列表（去重后）
     */
    public List<DataBzjDeviceEntity> getOnlineDeviceListByDateRange(String startDate, String endDate) {
        log.info("获取日期范围[{}]到[{}]的在线设备列表...", startDate, endDate);
        
        Set<String> allDeviceIds = new HashSet<>();
        
        // 遍历日期范围
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        
        try {
            start.setTime(DATE_FORMAT.parse(startDate));
            end.setTime(DATE_FORMAT.parse(endDate));
        } catch (Exception e) {
            log.error("日期解析失败: startDate={}, endDate={}", startDate, endDate, e);
            return new ArrayList<>();
        }
        
        // 遍历每一天
        Calendar current = (Calendar) start.clone();
        while (!current.after(end)) {
            String dateString = DATE_FORMAT.format(current.getTime());
            String onlineListKey = generateOnlineListKey(dateString);
            RSet<String> onlineSet = redissonClient.getSet(onlineListKey);
            
            Set<String> deviceIds = onlineSet.readAll();
            if (deviceIds != null && !deviceIds.isEmpty()) {
                allDeviceIds.addAll(deviceIds);
            }
            
            current.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        // 根据ID集合获取设备详细信息
        List<DataBzjDeviceEntity> deviceList = new ArrayList<>();
        for (String lotId : allDeviceIds) {
            DataBzjDeviceEntity device = getDeviceInfoFromCache(lotId);
            if (device != null) {
                deviceList.add(device);
            }
        }
        
        return deviceList;
    }

    /**
     * 获取指定日期的在线设备数量
     * @param dayString 日期字符串，格式 "yyyy-MM-dd"
     * @return 在线设备数量
     */
    public long getOnlineDeviceCount(String dayString) {
        String onlineListKey = generateOnlineListKey(dayString);
        RSet<String> onlineSet = redissonClient.getSet(onlineListKey);
        return onlineSet.size();
    }

    /**
     * 获取今日在线设备数量
     * @return 在线设备数量
     */
    public long getTodayOnlineDeviceCount() {
        return getOnlineDeviceCount(getCurrentDateString());
    }

    /**
     * 获取昨日在线设备数量
     * @return 在线设备数量
     */
    public long getYesterdayOnlineDeviceCount() {
        return getOnlineDeviceCount(getYesterdayDateString());
    }
    

    /**
     * 累加播种机数据（种子数、面积）并重新计算平均密度
     * @param lotId 设备ID
     * @param seedCount 本次种子数
     * @param workedAreaMu 本次作业面积（亩），字符串，如 "1.2345"
     * @return 累加后的累计数据（Map包含 totalSeedCount, totalAreaMu, avgSeedPerHectare）
     */
    public Map<String, Object> accumulateSeederData(String lotId, int seedCount, String workedAreaMu) {
        String today = getCurrentDateString();
        return accumulateSeederDataForDate(lotId, today, seedCount, workedAreaMu);
    }

    /**
     * 累加指定日期的播种机数据（用于历史补录或测试）
     */
    public Map<String, Object> accumulateSeederDataForDate(String lotId, String date, int seedCount, String workedAreaMu) {
        String key = KEY_PREFIX_SEEDER_ACCUM + lotId + ":" + date;
        RMap<String, Object> accumMap = redissonClient.getMap(key);
        RLock lock = accumMap.getLock("lock");
        try {
            lock.lock(10, TimeUnit.SECONDS);
            // 读取现有值
            Object oldTotalSeed = accumMap.get("totalSeedCount");
            Object oldTotalArea = accumMap.get("totalAreaMu");
            long totalSeed = (oldTotalSeed instanceof Long) ? (Long) oldTotalSeed : 
                             (oldTotalSeed instanceof Integer) ? (Integer) oldTotalSeed : 0L;
            BigDecimal totalArea = BigDecimal.ZERO;
            if (oldTotalArea instanceof String) {
                totalArea = new BigDecimal((String) oldTotalArea);
            } else if (oldTotalArea instanceof Number) {
                totalArea = new BigDecimal(((Number) oldTotalArea).doubleValue());
            }
            // 累加
            totalSeed += seedCount;
            BigDecimal addArea = new BigDecimal(workedAreaMu);
            totalArea = totalArea.add(addArea);
            // 计算平均每公顷种子数 = 总种子数 / (总面积亩 / 15) = 总种子数 * 15 / 总面积亩
            int avgSeedPerHectare = 0;
            if (totalArea.compareTo(BigDecimal.ZERO) > 0) {
                // 总面积公顷 = totalArea / 15
                // 种子数 / (总面积亩/15) = 种子数 * 15 / 总面积亩
                BigDecimal numerator = new BigDecimal(totalSeed).multiply(new BigDecimal(15));
                BigDecimal avg = numerator.divide(totalArea, 0, RoundingMode.HALF_UP);
                avgSeedPerHectare = avg.intValue();
            }
            // 保存
            accumMap.put("totalSeedCount", totalSeed);
            accumMap.put("totalAreaMu", totalArea.toString());
            accumMap.put("avgSeedPerHectare", avgSeedPerHectare);
            // 设置过期时间，比如保留30天
            accumMap.expire(30, TimeUnit.DAYS);
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalSeedCount", totalSeed);
            result.put("totalAreaMu", totalArea.toString());
            result.put("avgSeedPerHectare", avgSeedPerHectare);
            return result;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取今天累计数据
     */
    public Map<String, Object> getTodaySeederAccum(String lotId) {
        return getSeederAccumByDate(lotId, getCurrentDateString());
    }

    /**
     * 获取指定日期的累计数据
     */
    public Map<String, Object> getSeederAccumByDate(String lotId, String date) {
        String key = KEY_PREFIX_SEEDER_ACCUM + lotId + ":" + date;
        RMap<String, Object> accumMap = redissonClient.getMap(key);
        if (accumMap.isEmpty()) {
            return null;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("totalSeedCount", accumMap.get("totalSeedCount"));
        result.put("totalAreaMu", accumMap.get("totalAreaMu"));
        result.put("avgSeedPerHectare", accumMap.get("avgSeedPerHectare"));
        return result;
    }

    /**
     * 获取设备某段时间内每天的累计数据（历史记录）
     * @param lotId 设备ID
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate 结束日期 yyyy-MM-dd
     * @return List<Map> 每个Map包含 date, totalSeedCount, totalAreaMu, avgSeedPerHectare
     */
    public List<Map<String, Object>> getSeederAccumHistory(String lotId, String startDate, String endDate) {
        List<Map<String, Object>> history = new ArrayList<>();
        LocalDate start = LocalDate.parse(startDate, DateUtil.DATE_FORMATTER);
        LocalDate end = LocalDate.parse(endDate, DateUtil.DATE_FORMATTER);
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String dateStr = date.format(DateUtil.DATE_FORMATTER);
            Map<String, Object> accum = getSeederAccumByDate(lotId, dateStr);
            if (accum != null && !accum.isEmpty()) {
                Map<String, Object> item = new HashMap<>();
                item.put("date", dateStr);
                item.putAll(accum);
                history.add(item);
            }
        }
        return history;
    }
}
