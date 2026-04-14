package com.company.project.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    	
    	// 定义锁的key，可以根据业务需求调整
        String lockKey = "sync:bzj:message:update:today";
        // 锁等待时间(毫秒)，防止线程长时间等待
        long waitTime = 5000;
        // 锁持有时间(毫秒)，防止死锁
        long leaseTime = 10000;
        
        RLock lock = redissonClient.getLock(lockKey);
        
        // 尝试获取锁，最多等待waitTime毫秒
        boolean isLocked;
		try {
			isLocked = lock.tryLock();
			if (!isLocked) {
	            // 获取锁失败
	            return false;
	        }
			

			long timeMillis = System.currentTimeMillis();
	    	
			//获取今日累计在线的设备  
			List<DataBzjDeviceEntity> dqlist = redisDeviceManager.getTodayOnlineDeviceList();
			
			dqlist.forEach(this::addAndCheck);
			
			//循环调用增量保存
	        timeMillis = System.currentTimeMillis()- timeMillis ;
		} finally {
            // 确保锁被释放
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    	
    	
        
        
        return true;
    }
    /**
     * 同步设备消息
     */
    @Override
    public boolean syncMessages(String dayString) {
    	
    	// 定义锁的key，可以根据业务需求调整
    	String lockKey = "sync:bzj:message:update:"+dayString;
    	// 锁等待时间(毫秒)，防止线程长时间等待
    	long waitTime = 5000;
    	// 锁持有时间(毫秒)，防止死锁
    	long leaseTime = 10000;
    	
    	RLock lock = redissonClient.getLock(lockKey);
    	
    	// 尝试获取锁，最多等待waitTime毫秒
    	boolean isLocked;
    	try {
    		isLocked = lock.tryLock();
    		if (!isLocked) {
    			// 获取锁失败
    			return false;
    		}
    		
    		
    		long timeMillis = System.currentTimeMillis();
    		
    		//获取全部的设备
    		List<DataBzjDeviceEntity> dqlist = redisDeviceManager.getAllCachedDevices();
    		
    		dqlist.forEach(li -> this.addAndCheck(li,dayString));
    		
    		//循环调用增量保存
    		timeMillis = System.currentTimeMillis()- timeMillis ;
    	} finally {
    		// 确保锁被释放
    		if (lock.isHeldByCurrentThread()) {
    			lock.unlock();
    		}
    	}
    	
    	
    	
    	
    	return true;
    }
    

    @Override
    public boolean addAndCheck(DataBzjDeviceEntity device) {
    	return addAndCheck(device,DateUtil.getTodayString());
    }
    @Override
    public boolean addAndCheck(DataBzjDeviceEntity device,String dayString) {
    	
    	String startTime = DateUtil.getStartOfDayString(dayString);
    	String endTime = DateUtil.getEndOfTodayString();
    	if(DateUtil.isWithinDays(startTime, endTime, 15)) {
    		return addAndCheck(device,startTime,endTime);
    	}else {
    		return false;
    	}
    }
    
    
    @Override
    @Transactional
    public boolean addAndCheck(DataBzjDeviceEntity device,String startTime,String endTime) {
        List<DataElectricSeederMessageEntity> addMessageList = new ArrayList<>();
        List<DataElectricSeederMessageEntity> deleteMessageList = new ArrayList<>();

        
        // 1. 从阿里云获得数据
        List<DataElectricSeederMessageEntity> aliyunList = aliYunService.getMessage(device.getDeviceName(), device.getRawdata(), device.getProductKey(),DateUtil.getTimestamp(startTime),DateUtil.getTimestamp(endTime));
        // 2. 本地数据集合
        List<DataElectricSeederMessageEntity> databaseList = getListByLotId(device.getLotId(),startTime,endTime);

        // ========== 新增：本地重复数据去重 ==========
        // 按 key 分组，每组只保留一条（这里按 id 降序取最大）
        Map<String, DataElectricSeederMessageEntity> keepMap = new HashMap<>();
        Map<String, List<DataElectricSeederMessageEntity>> groupByKey = databaseList.stream()
                .collect(Collectors.groupingBy(this::buildUniqueKey));
        
        for (Map.Entry<String, List<DataElectricSeederMessageEntity>> entry : groupByKey.entrySet()) {
            List<DataElectricSeederMessageEntity> records = entry.getValue();
            // 按 id 降序排序，第一条为要保留的（id 最大）
            records.sort((a, b) -> b.getId().compareTo(a.getId()));
            DataElectricSeederMessageEntity keep = records.get(0);
            keepMap.put(entry.getKey(), keep);
            // 其余重复记录加入删除集合
            for (int i = 1; i < records.size(); i++) {
                deleteMessageList.add(records.get(i));
            }
        }
        // =======================================

        // 3. 构建本地去重后的映射（用于比较）
        // 注意：这里直接用 keepMap，而不是原始的 databaseList

        // 4. 记录阿里云中存在的所有 key
        Set<String> aliyunKeySet = new HashSet<>();

        // 5. 遍历阿里云数据，判断本地去重后的映射中是否存在
        for (DataElectricSeederMessageEntity aliyun : aliyunList) {
            String key = buildUniqueKey(aliyun);
            aliyunKeySet.add(key);
            if (!keepMap.containsKey(key)) {
                addMessageList.add(aliyun);
            }
        }

        // 6. 遍历本地去重后的映射，判断是否在阿里云中不存在（多余）
        for (Map.Entry<String, DataElectricSeederMessageEntity> entry : keepMap.entrySet()) {
            String key = entry.getKey();
            if (!aliyunKeySet.contains(key)) {
                deleteMessageList.add(entry.getValue());  // 保留的这条也要删除
            }
        }

        // 7. 批量保存新增
        if (!addMessageList.isEmpty()) {
            addMessageList.forEach(li -> addNewMessage(li, device));
        }
        // 8. 批量删除（包括重复多余的和阿里云没有的 key 对应的记录）
        if (!deleteMessageList.isEmpty()) {
            deleteMessageList.forEach(li -> li.setStatus(1));
            updateBatchById(deleteMessageList);
        }
        return true;
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
		
		// 定义锁的key，可以根据业务需求调整
        String lockKey = "sync:bzj:device:productKey:lock";
        // 锁等待时间(毫秒)，防止线程长时间等待
        long waitTime = 5000;
        // 锁持有时间(毫秒)，防止死锁
        long leaseTime = 10000;
        
        
        RLock lock = redissonClient.getLock(lockKey);
        
        // 尝试获取锁，最多等待waitTime毫秒
        boolean isLocked;
		try {
			isLocked = lock.tryLock();
			if (!isLocked) {
	            // 获取锁失败
	            return false;
	        }
			paramEntity.forEach(this::reRead);
		} finally {
            // 确保锁被释放
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
		
		return true;
	}
	
	
	@Transactional(rollbackFor = Exception.class) // 1. 将事务注解移到这里
    @Override
    public boolean reRead(DataBzjDeviceEntity paramEntit) {
		

        List<DataElectricSeederMessageEntity> list = getListByLotId(paramEntit.getLotId());

        if (list == null || list.isEmpty()) {
            return false; // 没有数据需要处理，直接返回
        }

        // 2. 循环处理，reSave 不再需要独立事务
        list.forEach(li -> addNewMessage(li, paramEntit));
        
        return true;
    }
	@Transactional(rollbackFor = Exception.class) // 1. 将事务注解移到这里
	@Override
	public boolean reRead(DataBzjDeviceEntity paramEntit,String messageId) {
		
     

		DataElectricSeederMessageEntity byId = getById(messageId);
		
		if (byId == null ) {
			return false; // 没有数据需要处理，直接返回
		}
		
		// 2. 循环处理，reSave 不再需要独立事务
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
        
        
        redisDeviceManager.accumulateSeederData(paramEntit.getLotId(), entity.getSeedCount(), entity.getWorkedArea());


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
		
		Integer sowLine = entity.getSowLine();
		/**
		 * 种子数量
		 */
		List<DataElectricSeederMessageLineEntity> lines = entity.getLines();
		  // 初始化总和为 BigDecimal.ZERO
		int totalSeedCount = 0;

	    // 确保 lines 不为 null，然后进行求和计算
	    if (lines != null && !lines.isEmpty()) {
	        totalSeedCount = lines.stream() // 1. 将列表转换为流
	                .filter(line -> line != null && line.getSeedNum() != null) // 2. 过滤掉null的行和seedCount为null的行，防止空指针异常
	                .mapToInt(DataElectricSeederMessageLineEntity::getSeedNum) // 3. 提取每个对象的 seedCount 属性，并转换为 IntStream
	                .sum(); // 4. 对流中的所有整数求和
	    }
	    entity.setSeedCount(totalSeedCount);
	    /**
	     * 每公顷种子数
	     */
	    // 提取参数并处理 null 值，提供默认值 0
	    Integer sowDistance = entity.getSowDistance();
	    Integer sowingWidth = entity.getSowingWidth();

	    // 使用 BigDecimal 进行精确计算，避免整数除法精度丢失
	    BigDecimal areaInHectares = BigDecimal.ZERO;
	    if (sowDistance > 0 && sowingWidth > 0) {
	        // 面积（公顷） = (距离(米) * 宽度(毫米)) / 10,000,000
	        // 解释：米 * 毫米 = 0.001 平方米，所以除以 10,000,000 得到公顷（因为 1 公顷 = 10,000 平方米）
	        BigDecimal distance = new BigDecimal(sowDistance);
	        BigDecimal width = new BigDecimal(sowingWidth);
	        BigDecimal divisor = new BigDecimal(10000000); // 10,000,000
	        areaInHectares = distance.multiply(width).divide(divisor, 4, RoundingMode.HALF_UP); // 保留4位小数
	    }

	    // 计算每公顷种子数（播种密度）
	    if (areaInHectares.compareTo(BigDecimal.ZERO) > 0) { // 确保面积大于0
	        BigDecimal totalSeeds = new BigDecimal(totalSeedCount);
	        BigDecimal seedCountPerHectare = totalSeeds.divide(areaInHectares, 0, RoundingMode.HALF_UP); // 保留0位小数，四舍五入
	        entity.setSeedCountHectare(seedCountPerHectare.intValue());
	    } else {
	        entity.setSeedCountHectare(0); // 面积为零时，设置密度为0
	    }

	    /**
	     * 作业面积（亩）
	     */
	    // 1 公顷 = 15 亩
	    BigDecimal workedAreaInMu = areaInHectares.multiply(new BigDecimal(15));
	    entity.setWorkedArea(workedAreaInMu.toString()); // 转换为 int，注意可能丢失小数部分

		/**
		 * 株距
		 */
		Integer aSowInterval = entity.getASowInterval();
		Integer bSowInterval = entity.getBSowInterval();
		String sowIntervalString = "";
		if(aSowInterval!=0 && bSowInterval!=0) {
			sowIntervalString = aSowInterval + "/" + bSowInterval ;
		}else if(aSowInterval==0) {
			sowIntervalString = Integer.toString(bSowInterval) ;
		}else if(bSowInterval==0) {
			sowIntervalString = Integer.toString(aSowInterval) ;
		}else {
			sowIntervalString = "0";
		}
		entity.setSowInterval(sowIntervalString);
	}



	
}