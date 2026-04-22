package com.company.project.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.project.aliyun.AliYunService;
import com.company.project.aliyun.RedisDeviceManager;
import com.company.project.entity.DataAlarmEntity;
import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.entity.DataElectricSeederMessageEntity;
import com.company.project.entity.DataElectricSeederMessageLineEntity;
import com.company.project.mapper.DataElectricSeederMessageMapper;
import com.company.project.service.DataAlarmService;
import com.company.project.service.DataBzjDeviceService;
import com.company.project.service.DataElectricSeederMessageLineService;
import com.company.project.service.DataElectricSeederMessageService;
import com.company.project.strategy.CodeReadStrategy;
import com.company.project.util.DateUtil;


@Service("dataElectricSeederMessageService")
public class DataElectricSeederMessageServiceImpl extends ServiceImpl<DataElectricSeederMessageMapper, DataElectricSeederMessageEntity> implements DataElectricSeederMessageService {

    private static final Logger logger = LoggerFactory.getLogger(DataElectricSeederMessageServiceImpl.class);

    // Constants for locks and limits
    private static final String SYNC_TODAY_LOCK_KEY = "sync:bzj:message:update:today";
    private static final String REREAD_LOCK_KEY = "sync:bzj:device:productKey:lock";
    private static final long LOCK_WAIT_TIME = 5000;
    private static final long LOCK_LEASE_TIME = 10000;
    private static final int MAX_DAYS_RANGE = 15;
    private static final BigDecimal HECTARE_TO_MU_FACTOR = new BigDecimal(15);
    private static final BigDecimal AREA_CALC_DIVISOR = new BigDecimal(10000000);

	@Autowired
    private AliYunService aliYunService;
	/**
	 *  产品策略
	 */
	@Autowired
	private Map<String, CodeReadStrategy> strategyMap;
	
	@Autowired
	private DataElectricSeederMessageLineService messageLineService;
	@Autowired
	private DataAlarmService alarmService;
	
	@Autowired
	@Lazy
	DataBzjDeviceService dataBzjDeviceService;
	
	@Autowired
	DataElectricSeederMessageLineService dataElectricSeederMessageLineService;
	@Autowired
	DataAlarmService dataAlarmService;
	
	@Autowired
    private DataElectricSeederMessageMapper dataElectricSeederMessageMapper;
 
	@Autowired
	private RedissonClient redissonClient;
	
	@Autowired
	private RedisDeviceManager redisDeviceManager;

 // Service层示例
    @Override
    public IPage<DataElectricSeederMessageEntity> getMessageList(DataElectricSeederMessageEntity queryEntity) {
    	 Page<DataElectricSeederMessageEntity> page = new Page<>(queryEntity.getPage(), queryEntity.getLimit());

        IPage<DataElectricSeederMessageEntity> list = dataElectricSeederMessageMapper.selectAll(page,queryEntity);
        
        return list;
    }
    
    
    /**
     * 同步设备消息（仅限今日在线过的）
     */
    @Override
    @Scheduled(cron = "0 2/5 * * * ?")
    public boolean syncToday() {
        RLock lock = redissonClient.getLock(SYNC_TODAY_LOCK_KEY);
        try {
            if (!lock.tryLock()) {
                logger.warn("Failed to acquire lock for syncToday");
                return false;
            }
            long startTime = System.currentTimeMillis();
            List<DataBzjDeviceEntity> devices = redisDeviceManager.getTodayOnlineDeviceList();
            devices.forEach(this::addAndCheck);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("syncToday completed in {} ms for {} devices", duration, devices.size());
            return true;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            logger.error("syncToday interrupted", e);
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 同步设备消息
     */
    @Override
    @Transactional
    public boolean syncMessages(String dayString) {
        long startTime = System.currentTimeMillis();
        List<DataBzjDeviceEntity> devices = redisDeviceManager.getAllCachedDevices();
        devices.forEach(device -> this.addAndCheck(device, dayString));
        long duration = System.currentTimeMillis() - startTime;
        logger.info("syncMessages for {} completed in {} ms for {} devices", dayString, duration, devices.size());
        return true;
    }

    @Override
    public boolean addAndCheck(DataBzjDeviceEntity device) {
        return addAndCheck(device, DateUtil.getTodayString());
    }

    @Override
    public boolean addAndCheck(DataBzjDeviceEntity device, String dayString) {
        String startTime = DateUtil.getStartOfDayString(dayString);
        String endTime = DateUtil.getEndOfDayString(dayString);
        if (!DateUtil.isWithinDays(startTime, endTime, MAX_DAYS_RANGE)) {
            logger.warn("Date range exceeds maximum allowed days: {}", MAX_DAYS_RANGE);
            return false;
        }
        return addAndCheck(device, startTime, endTime);
    }
    
    
    @Override
    @Transactional
    public boolean addAndCheck(DataBzjDeviceEntity device, String startTime, String endTime) {
        List<DataElectricSeederMessageEntity> addMessageList = new ArrayList<>();
        List<DataElectricSeederMessageEntity> deleteMessageList = new ArrayList<>();

        // 1. 从阿里云获得数据
        List<DataElectricSeederMessageEntity> aliyunList = aliYunService.getMessage(device.getDeviceName(), device.getRawdata(), device.getProductKey(), DateUtil.getTimestamp(startTime), DateUtil.getTimestamp(endTime));
        // 2. 本地数据集合
        List<DataElectricSeederMessageEntity> databaseList = getListByLotId(device.getLotId(), startTime, endTime);

        // 3. 本地数据去重
        Map<String, DataElectricSeederMessageEntity> keepMap = removeDuplicates(databaseList, deleteMessageList);

        // 4. 比较阿里云数据和本地数据，确定新增和删除
        identifyChanges(aliyunList, keepMap, addMessageList, deleteMessageList);

        // 5. 批量保存新增和删除
        batchSaveAndDelete(addMessageList, deleteMessageList, device);

        return true;
    }

    /**
     * 去重本地数据，返回保留的映射，并将重复项加入删除列表
     */
    private Map<String, DataElectricSeederMessageEntity> removeDuplicates(List<DataElectricSeederMessageEntity> databaseList, List<DataElectricSeederMessageEntity> deleteMessageList) {
        Map<String, DataElectricSeederMessageEntity> keepMap = new HashMap<>();
        Map<String, List<DataElectricSeederMessageEntity>> groupByKey = databaseList.stream()
                .collect(Collectors.groupingBy(this::buildUniqueKey));

        for (Map.Entry<String, List<DataElectricSeederMessageEntity>> entry : groupByKey.entrySet()) {
            List<DataElectricSeederMessageEntity> records = entry.getValue();
            records.sort((a, b) -> b.getId().compareTo(a.getId()));
            DataElectricSeederMessageEntity keep = records.get(0);
            keepMap.put(entry.getKey(), keep);
            for (int i = 1; i < records.size(); i++) {
                deleteMessageList.add(records.get(i));
            }
        }
        return keepMap;
    }

    /**
     * 比较阿里云数据和本地数据，确定新增和删除
     */
    private void identifyChanges(List<DataElectricSeederMessageEntity> aliyunList, Map<String, DataElectricSeederMessageEntity> keepMap,
                                 List<DataElectricSeederMessageEntity> addMessageList, List<DataElectricSeederMessageEntity> deleteMessageList) {
        Set<String> aliyunKeySet = aliyunList.stream()
                .map(this::buildUniqueKey)
                .collect(Collectors.toSet());

        for (DataElectricSeederMessageEntity aliyun : aliyunList) {
            String key = buildUniqueKey(aliyun);
            if (!keepMap.containsKey(key)) {
                addMessageList.add(aliyun);
            }
        }

        for (Map.Entry<String, DataElectricSeederMessageEntity> entry : keepMap.entrySet()) {
            String key = entry.getKey();
            if (!aliyunKeySet.contains(key)) {
                deleteMessageList.add(entry.getValue());
            }
        }
    }

    /**
     * 批量保存新增和删除
     */
    private void batchSaveAndDelete(List<DataElectricSeederMessageEntity> addMessageList, List<DataElectricSeederMessageEntity> deleteMessageList, DataBzjDeviceEntity device) {
        if (!addMessageList.isEmpty()) {
            addMessageList.forEach(entity -> addNewMessage(entity, device));
        }
        if (!deleteMessageList.isEmpty()) {
            deleteMessageList.forEach(entity -> entity.setStatus(1));
            updateBatchById(deleteMessageList);
        }
    }

    /**
     * 构建业务唯一键
     * 假设 DataElectricSeederMessageEntity 中有 getLotId(), getDataTime(), getAliyun() 方法
     * 注意 data_time 格式需要统一，建议使用字符串形式（如 "20260408083656.812"）
     */
    private String buildUniqueKey(DataElectricSeederMessageEntity entity) {
        // 如果 data_time 是 LocalDateTime 类型，需要格式化为字符串
    	LocalDateTime dataTimeStr = entity.getDataTime(); // 假设是字符串
        // 如果存在 null 值，需要处理
        return entity.getLotId() + "|" + dataTimeStr + "|" + entity.getAliyun();
    }
    
    public List<DataElectricSeederMessageEntity> getListByLotId(String lotId, String startOfDay,String endOfDay) {
        LambdaQueryWrapper<DataElectricSeederMessageEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper
            .eq(DataElectricSeederMessageEntity::getLotId, lotId)
            .ne(DataElectricSeederMessageEntity::getStatus, 1);
        
        queryWrapper.between(DataElectricSeederMessageEntity::getDataTime, startOfDay, endOfDay);
        
        return list(queryWrapper);
    }
    public List<DataElectricSeederMessageEntity> getListByLotId(String lotId) {
    	LambdaQueryWrapper<DataElectricSeederMessageEntity> queryWrapper = Wrappers.lambdaQuery();
    	queryWrapper
    	.eq(DataElectricSeederMessageEntity::getLotId, lotId)
    	.ne(DataElectricSeederMessageEntity::getStatus, 1);
    	
    	return list(queryWrapper);
    }
    
    


	@Override
	public Date selectMaxDataTimeByDevice(DataBzjDeviceEntity device) {
		return dataElectricSeederMessageMapper.selectMaxDataTimeByDevice(device);
	}

//
//	@Override
//	public boolean saveByHexStr(DataBzjDeviceEntity device,Date dataTime,String hexData) {
//		
//		DataElectricSeederMessageEntity entity = DataAnalysisUtil.transToEntity(hexData);
//        
//        entity.setLotId(device.getLotId());
//        entity.setDeviceName(device.getDeviceName());
//        entity.setDataTime(dataTime);
//        entity.setAliyun(hexData);
//        
//        entity.getLines().forEach(li -> {
//        	li.setLotId(device.getLotId());
//        	li.setDataTime(entity.getDataTime());
//        });
//        
//        dataElectricSeederMessageLineService.saveBatch(entity.getLines());
//		
//        save(entity);
//        
//        return true;
//	}
	
	/**
	 * 联合主键更新
	 * @param entity
	 * @return
	 */
	public boolean updateByCombinedKey(DataElectricSeederMessageEntity entity) {
	    UpdateWrapper<DataElectricSeederMessageEntity> updateWrapper = new UpdateWrapper<>();
	    updateWrapper.eq("lot_id", entity.getLotId())
	                .eq("data_time", entity.getDataTime());
	    
	    return update(entity, updateWrapper);
	}
	@Override
	public boolean reRead(List<DataBzjDeviceEntity> paramEntity) {
        RLock lock = redissonClient.getLock(REREAD_LOCK_KEY);
        try {
            if (!lock.tryLock(LOCK_WAIT_TIME, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                logger.warn("Failed to acquire lock for reRead");
                return false;
            }
            paramEntity.forEach(this::reRead);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("reRead interrupted", e);
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
	}
	
	@Transactional(rollbackFor = Exception.class)
    @Override
    public boolean reRead(DataBzjDeviceEntity paramEntit) {
        List<DataElectricSeederMessageEntity> list = getListByLotId(paramEntit.getLotId());
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        list.forEach(li -> addNewMessage(li, paramEntit));
        return true;
    }

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean reRead(DataBzjDeviceEntity paramEntit, String messageId) {
		DataElectricSeederMessageEntity byId = getById(messageId);
		if (byId == null) {
			return false;
		}
		addNewMessage(byId, paramEntit);
		return true;
	}
    /**
     * 重新保存单条消息及其关联数据。
     * 此方法没有独立事务，会加入到 reRead 的事务中。
     * @param li 原始消息实体
     * @param paramEntit 参数实体
     */
    // 3. 移除了 @Transactional 注解
	@Override
    public void addNewMessage(DataElectricSeederMessageEntity entity, DataBzjDeviceEntity paramEntit) {
        // 4. 增加空指针检查
        CodeReadStrategy strategy = strategyMap.get(paramEntit.getProductKey());
        if (strategy == null) {
            return; 
        }

        // 5. 重新读取和构建实体
        strategy.readCode(entity);
        
        
        calculateData(entity);
        
        
//        redisDeviceManager.accumulateSeederData(paramEntit.getLotId(), entity.getSeedCount(), entity.getWorkedArea());


        if(entity.getId() != null) {
        	// 6. 精确删除关联数据
            Map<String, Object> alarmParaMap = new HashMap<>();
            alarmParaMap.put("message_id", entity.getId());
            alarmParaMap.put("lot_id", entity.getLotId()); // 加上 lotId，更安全
            alarmService.removeByMap(alarmParaMap);
            

            Map<String, Object> lineParaMap = new HashMap<>();
            lineParaMap.put("message_id", entity.getId());
            lineParaMap.put("lot_id", entity.getLotId()); // 加上 lotId，更安全
            messageLineService.removeByMap(lineParaMap);
        	
        }
        
        // 更新，需要先查询出旧实体，再更新它的字段
        saveOrUpdate(entity);
        // 7. 批量保存新的关联数据
        List<DataElectricSeederMessageLineEntity> lines = entity.getLines();
        if (lines != null && !lines.isEmpty()) {
            lines.forEach(line -> {
                line.setMessageId(entity.getId()); // 使用新的 message_id
                line.setLotId(entity.getLotId());
            });
            dataElectricSeederMessageLineService.saveBatch(lines);
        }

        List<DataAlarmEntity> alarms = entity.getAlarms();
        if (alarms != null && !alarms.isEmpty()) {
            alarms.forEach(alarm -> {
                alarm.setMessageId(entity.getId()); // 使用新的 message_id
                alarm.setLotId(entity.getLotId());
            });
            dataAlarmService.saveBatch(alarms);
        }
    }

	
	/**
	 * 为页面提供查询功能
	 * @param entity
	 */
	public void calculateData(DataElectricSeederMessageEntity entity) {
		/**
		 * 种子数量
		 */
		List<DataElectricSeederMessageLineEntity> lines = entity.getLines();
		int totalSeedCount = Optional.ofNullable(lines)
				.orElse(Collections.emptyList())
				.stream()
				.filter(line -> line != null && line.getSeedNum() != null)
				.mapToInt(DataElectricSeederMessageLineEntity::getSeedNum)
				.sum();
		entity.setSeedCount(totalSeedCount);

		/**
		 * 每公顷种子数
		 */
		Integer sowDistance = entity.getSowDistance();
		Integer sowingWidth = entity.getSowingWidth();
		BigDecimal areaInHectares = BigDecimal.ZERO;
		if (sowDistance != null && sowDistance > 0 && sowingWidth != null && sowingWidth > 0) {
			BigDecimal distance = new BigDecimal(sowDistance);
			BigDecimal width = new BigDecimal(sowingWidth);
			areaInHectares = distance.multiply(width).divide(AREA_CALC_DIVISOR, 4, RoundingMode.HALF_UP);
		}

		if (areaInHectares.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal totalSeeds = new BigDecimal(totalSeedCount);
			BigDecimal seedCountPerHectare = totalSeeds.divide(areaInHectares, 0, RoundingMode.HALF_UP);
			entity.setSeedCountHectare(seedCountPerHectare.intValue());
		} else {
			entity.setSeedCountHectare(0);
		}

		/**
		 * 作业面积（亩）
		 */
		BigDecimal workedAreaInMu = areaInHectares.multiply(HECTARE_TO_MU_FACTOR);
		entity.setWorkedArea(workedAreaInMu.toString());

		/**
		 * 株距
		 */
		Integer aSowInterval = entity.getASowInterval();
		Integer bSowInterval = entity.getBSowInterval();
		String sowIntervalString = Optional.ofNullable(aSowInterval)
				.filter(a -> a != 0)
				.map(a -> Optional.ofNullable(bSowInterval)
						.filter(b -> b != 0)
						.map(b -> a + "/" + b)
						.orElse(String.valueOf(a)))
				.orElse(Optional.ofNullable(bSowInterval)
						.filter(b -> b != 0)
						.map(String::valueOf)
						.orElse("0"));
		entity.setSowInterval(sowIntervalString);
	}



	
}