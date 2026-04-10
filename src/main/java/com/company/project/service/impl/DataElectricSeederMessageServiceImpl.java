package com.company.project.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.project.aliyun.AliYunService;
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

 // Service层示例
    @Override
    public IPage<DataElectricSeederMessageEntity> getMessageList(DataElectricSeederMessageEntity queryEntity) {
    	 Page<DataElectricSeederMessageEntity> page = new Page<>(queryEntity.getPage(), queryEntity.getLimit());

        IPage<DataElectricSeederMessageEntity> list = dataElectricSeederMessageMapper.selectAll(page,queryEntity);
        
        return list;
    }
    
    
    /**
     * 增量同步全部电驱消息内容（2025年12月开始）
     */
    @Override
    @Scheduled(cron = "0 0/30 * * * ?")
    public boolean sync() {
    	
    	// 定义锁的key，可以根据业务需求调整
        String lockKey = "sync:bzj:message:add:lock";
        // 锁等待时间(毫秒)，防止线程长时间等待
        long waitTime = 5000;
        // 锁持有时间(毫秒)，防止死锁
        long leaseTime = 10000;
        
        RLock lock = redissonClient.getLock(lockKey);
        
        // 尝试获取锁，最多等待waitTime毫秒
        boolean isLocked;
		try {
			isLocked = lock.tryLock(waitTime, leaseTime, java.util.concurrent.TimeUnit.MILLISECONDS);
			if (!isLocked) {
	            // 获取锁失败
	            return false;
	        }
			

			long timeMillis = System.currentTimeMillis();
	    	
			//获取所有的设备  
			List<DataBzjDeviceEntity> dqlist = dataBzjDeviceService.list();
			
			dqlist.forEach(this::insertNewData);
			
			//循环调用增量保存
	        timeMillis = System.currentTimeMillis()- timeMillis ;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            // 确保锁被释放
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    	
    	
        
        
        return true;
    }
    
    
    @Override
    public void insertNewData(String lotId) {
    	DataBzjDeviceEntity byId = dataBzjDeviceService.getById(lotId);
    	if(byId!=null) {
    		insertNewData(byId);
    	}
    }
    
    /**
     * 增量保存
     * @param device
     * @return
     */
    @Override
    @Transactional
    public boolean insertNewData(DataBzjDeviceEntity device) {
    	
        
    	try {
	    	//查询最大结束时间位起始时间
	    	Date beginTimeDate = selectMaxDataTimeByDevice(device);
	    	

	    	Date endTimeDate = new Date();
	    	long endTimeTimestamp = endTimeDate.getTime();
	    	
	    	
	    	// 转换为Instant（时间戳）
	    	Instant endInstant = endTimeDate.toInstant();
	    	
	    	// 或者更推荐的方式：
	    	Instant thirtyDaysBeforeInstant2 = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault())
	    			.minusDays(30)
	    			.atZone(ZoneId.systemDefault())
	    			.toInstant();
	    	
	    	// 转换回Date
	    	Date thirtyDaysBefore = Date.from(thirtyDaysBeforeInstant2);
	    	long beginTimeTimestamp = thirtyDaysBefore.getTime();
	    	if(beginTimeDate != null) {
	    		long time = beginTimeDate.getTime();
	    		if(time>=beginTimeTimestamp) {
	    			beginTimeTimestamp = time;
	    		}
	    	}
            
	        
            List<DataElectricSeederMessageEntity> iotLit = aliYunService.getMessage(device.getDeviceName(),device.getRawdata(),device.getProductKey(),beginTimeTimestamp, endTimeTimestamp);
	        
            iotLit.forEach(this::calculateData);
            
	        saveBatch(iotLit);
            
            iotLit.forEach(li -> {
            	String id = li.getId();
            	List<DataElectricSeederMessageLineEntity> lines = li.getLines();
            	lines.forEach(line -> {
            		line.setMessageId(id);
            		line.setLotId(li.getLotId());
            	});
            	dataElectricSeederMessageLineService.saveBatch(lines);
            	
            	List<DataAlarmEntity> alarms = li.getAlarms();
            	alarms.forEach(alarm -> {
            		alarm.setMessageId(id);
            		alarm.setLotId(li.getLotId());
            	});
            	dataAlarmService.saveBatch(alarms);
            });
	    	
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	
    	return true;
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
			isLocked = lock.tryLock(waitTime, leaseTime, java.util.concurrent.TimeUnit.MILLISECONDS);
			if (!isLocked) {
	            // 获取锁失败
	            return false;
	        }
			paramEntity.forEach(this::reRead);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
        LambdaQueryWrapper<DataElectricSeederMessageEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(paramEntit.getLotId() != null, DataElectricSeederMessageEntity::getLotId, paramEntit.getLotId());
        
        // 假设这里还有其他查询条件，比如产品类型等
        // queryWrapper.eq(..., ...);

        List<DataElectricSeederMessageEntity> list = list(queryWrapper);

        if (list == null || list.isEmpty()) {
            return false; // 没有数据需要处理，直接返回
        }

        // 2. 循环处理，reSave 不再需要独立事务
        list.forEach(li -> reSave(li, paramEntit));
        
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
		reSave(byId, paramEntit);
		
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
    public void reSave(DataElectricSeederMessageEntity li, DataBzjDeviceEntity paramEntit) {
        // 4. 增加空指针检查
        CodeReadStrategy strategy = strategyMap.get(paramEntit.getProductKey());
        if (strategy == null) {
            return; 
        }

        // 5. 重新读取和构建实体
        DataElectricSeederMessageEntity entity = strategy.readCode(li.getAliyun());
        if (entity == null) {
            return;
        }

        // 复制基础属性
        entity.setId(li.getId());
        entity.setLotId(li.getLotId());
        entity.setDeviceName(li.getDeviceName());
        entity.setDataTime(li.getDataTime());
        entity.setAliyun(li.getAliyun());
        
        calculateData(entity);


        // 更新，需要先查询出旧实体，再更新它的字段
        updateById(entity);
        String newId = entity.getId(); // 获取新保存实体的ID

        // 6. 精确删除关联数据
        Map<String, Object> alarmParaMap = new HashMap<>();
        alarmParaMap.put("message_id", li.getId());
        alarmParaMap.put("lot_id", li.getLotId()); // 加上 lotId，更安全
        alarmService.removeByMap(alarmParaMap);
        

        Map<String, Object> lineParaMap = new HashMap<>();
        lineParaMap.put("message_id", li.getId());
        lineParaMap.put("lot_id", li.getLotId()); // 加上 lotId，更安全
        messageLineService.removeByMap(lineParaMap);
        

        // 7. 批量保存新的关联数据
        List<DataElectricSeederMessageLineEntity> lines = entity.getLines();
        if (lines != null && !lines.isEmpty()) {
            lines.forEach(line -> {
                line.setMessageId(newId); // 使用新的 message_id
                line.setLotId(entity.getLotId());
            });
            dataElectricSeederMessageLineService.saveBatch(lines);
        }

        List<DataAlarmEntity> alarms = entity.getAlarms();
        if (alarms != null && !alarms.isEmpty()) {
            alarms.forEach(alarm -> {
                alarm.setMessageId(newId); // 使用新的 message_id
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