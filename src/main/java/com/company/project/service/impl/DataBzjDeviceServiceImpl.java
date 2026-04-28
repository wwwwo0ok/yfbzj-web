package com.company.project.service.impl;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceResponseBody;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceResponseBody.DeviceInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.project.aliyun.AliYunService;
import com.company.project.aliyun.RedisDeviceManager;
import com.company.project.common.exception.BusinessException;
import com.company.project.common.exception.code.BaseResponseCode;
import com.company.project.dto.DeviceAndSaleQueryDTO;
import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.entity.DataElectricSeederMessageEntity;
import com.company.project.mapper.DataBzjDeviceMapper;
import com.company.project.service.DataBzjDeviceService;
import com.company.project.service.DataElectricSeederMessageService;
import com.company.project.util.BaiduCoordConverterHttp;

import lombok.extern.slf4j.Slf4j;

@Service("dataBzjDeviceService")
@Slf4j
public class DataBzjDeviceServiceImpl extends ServiceImpl<DataBzjDeviceMapper, DataBzjDeviceEntity> implements DataBzjDeviceService {

	@Autowired
	private AliYunService aliYunService;
	
	@Autowired
	private DataElectricSeederMessageService messageService;
	
	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private RedisDeviceManager redisDeviceManager;
	
	
	 /**
     * 同步播种机设备
     * 使用Redisson分布式锁保证分布式环境下的同步安全
     *
     * @return true-同步成功，false-同步失败(获取锁失败或业务处理失败)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Scheduled(cron = "0 2/5 * * * ?")
    public boolean sync() {
    	
    	// 定义锁的key，可以根据业务需求调整
        String lockKey = "sync:bzj:device:lock";
        
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 尝试获取锁，最多等待waitTime毫秒
            boolean isLocked = lock.tryLock();
            if (!isLocked) {
                // 获取锁失败
                return false;
            }
            
            long startTime = System.currentTimeMillis();
            
            // 1. 从阿里云获取设备列表
            List<DeviceInfo> queryIotBzjDevice = aliYunService.getBzjDevice();
            
            // 2. 转换为本地实体
            List<DataBzjDeviceEntity> bzjDeviceList = queryIotBzjDevice.stream()
                    .map(this::convertToDataBzjDeviceEntity)
                    .collect(Collectors.toList());
            
            List<DataBzjDeviceEntity> syncAndGetChangedDevices = redisDeviceManager.syncAndGetChangedDevices(bzjDeviceList);
            
            
            // 3. 批量保存或更新
            if(syncAndGetChangedDevices.isEmpty()) {
            	return false;
            }
            boolean batchResult = saveOrUpdateBatch(syncAndGetChangedDevices);
            
            long elapsedTime = System.currentTimeMillis() - startTime;
            // 可以记录日志或监控耗时
            
            return batchResult;
        } finally {
            // 确保锁被释放
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    
    @Override
    @Transactional
    public boolean sync(String productKey) {
    	
    	
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
	    	
	    	long timeMillis = System.currentTimeMillis();
	    	
	    	List<DeviceInfo> queryIotBzjDevice = aliYunService.getBzjDevice(productKey);
	    	
	    	List<DataBzjDeviceEntity> bzjDeviceList = queryIotBzjDevice.stream()
	    			.map(device -> {
	    				DataBzjDeviceEntity entity = convertToDataBzjDeviceEntity(device);
	    				return entity;
	    			})
	    			.collect(Collectors.toList());
	    	saveOrUpdateBatch(bzjDeviceList);
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
     *  更新定位信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Scheduled(cron = "0 3/10 * * * ?")
    public void syncLocation() {

    	// 定义锁的key，可以根据业务需求调整
        String lockKey = "sync:bzj:device:location:lock";
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
	            return;
	        }
			
			List<DataBzjDeviceEntity> list = list();
	    	
	    	list.forEach(entity -> {
	    		
	    		try {
	                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	                //只处理新版代码之后的数据。
	    	    	
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
	                
	                Map<String, Double> location = aliYunService.getLocation(entity.getDeviceName(),entity.getRawdata(),entity.getProductKey(),beginTimeTimestamp, endTimeTimestamp);
	                
	                if(location!=null) {
	                	entity.setLatestX(location.get("x"));
	                	entity.setLatestY(location.get("y"));
	                	Map<String, Double> wgs84ToBd09 = BaiduCoordConverterHttp.wgs84ToBd09(location);
	                	if(wgs84ToBd09!=null) {
	                		entity.setBaiduX(wgs84ToBd09.get("x"));
	                		entity.setBaiduY(wgs84ToBd09.get("y"));
	                		updateById(entity);
	                	}
	                }
	                
	                
	            } catch (Exception e) {
	            	// TODO Auto-generated catch block
	            	e.printStackTrace();
	            }
	    	});
		} finally {
            // 确保锁被释放
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        
    	
    	
    	
    	
        
    }
    
 // 实体转换封装方法
    private DataBzjDeviceEntity convertToDataBzjDeviceEntity(QueryDeviceResponseBody.DeviceInfo device) {
        DataBzjDeviceEntity bzjDevice = new DataBzjDeviceEntity();
        bzjDevice.setLotId(device.getIotId());
        bzjDevice.setDeviceName(device.getDeviceName());
        bzjDevice.setNickname(device.getNickname());
        bzjDevice.setDeviceSecret(device.getDeviceSecret());
        bzjDevice.setDeviceStatus(device.getDeviceStatus());
        

        Optional.ofNullable(device.getUtcModified()).ifPresent(ts -> {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                bzjDevice.setDeviceModified(ZonedDateTime.parse(ts).format(formatter));
            } catch (Exception e) {
                log.error("时间转换异常: {}", ts);
                throw new BusinessException(BaseResponseCode.DATA_ERROR);
            }
        });

        bzjDevice.setProductKey(device.getProductKey());
        return bzjDevice;
    }
	
    
    
    
    
    
    @Override
    public JSONObject pointMap(DataBzjDeviceEntity dataBzjDevice) {
    	//在线数量
    	Map<String, Object> pointMap = getBaseMapper().pointMap(dataBzjDevice);
    	
    	return new JSONObject(pointMap);
    }


    @Override
	public void reRead(String productKey) {
		
    	//1、查询全部设备
    	LambdaQueryWrapper<DataBzjDeviceEntity> queryWrapper = Wrappers.lambdaQuery();
    	//查询条件示例
    	queryWrapper
    	.eq(StringUtils.isNotBlank(productKey),DataBzjDeviceEntity::getProductKey,productKey)
    	;
    	List<DataBzjDeviceEntity> list = list(queryWrapper);
    	
    	messageService.reRead(list);
    	
	}
    @Override
    public boolean reRead(DataBzjDeviceEntity paramEntit) {
    	
    	// 定义锁的key，可以根据业务需求调整
        String allRereadKey = "sync:bzj:device:productKey:lock";
        String lockKey = "sync:bzj:message:device:"+paramEntit.getLotId()+":lock";
        // 锁等待时间(毫秒)，防止线程长时间等待
        long waitTime = 1000;
        
        RLock allRereadLock = redissonClient.getLock(allRereadKey);
        if(allRereadLock.isLocked()) {
        	return false;
        }
        
        RLock lock = redissonClient.getLock(lockKey);
        
        // 尝试获取锁，最多等待waitTime毫秒
        boolean isLocked;
		try {
			isLocked = lock.tryLock();
			if (!isLocked) {
	            // 获取锁失败
	            return false;
	        }

    		messageService.reRead(paramEntit);
		} finally {
            // 确保锁被释放
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    	return true;
    }
    @Override
    public boolean reRead(DataElectricSeederMessageEntity messageEntity) {
    	
    	String lotId = messageEntity.getLotId();
    	
    	DataBzjDeviceEntity byId = getById(lotId);
    	
    	if(byId == null) {
    		return false;
    	}
    	
    	// 定义锁的key，可以根据业务需求调整
        String allRereadKey = "sync:bzj:device:productKey:lock";
        String thisProductKey = "sync:bzj:message:device:"+byId.getLotId()+":lock";
        // 定义锁的key，可以根据业务需求调整
        String lockKey = "sync:bzj:message:device:message:"+messageEntity.getId()+":lock";
        // 锁等待时间(毫秒)，防止线程长时间等待
        long waitTime = 5000;
        // 锁持有时间(毫秒)，防止死锁
        long leaseTime = 10000;
        
        RLock allRereadLock = redissonClient.getLock(allRereadKey);
        if(allRereadLock.isLocked()) {
        	return false;
        }
        RLock thisProductLock = redissonClient.getLock(thisProductKey);
        if(thisProductLock.isLocked()) {
        	return false;
        }
        
        RLock lock = redissonClient.getLock(lockKey);
        
        // 尝试获取锁，最多等待waitTime毫秒
        boolean isLocked;
		try {
			isLocked = lock.tryLock();
			if (!isLocked) {
	            // 获取锁失败
	            return false;
	        }
	    	messageService.reRead(byId,messageEntity.getId());
		} finally {
            // 确保锁被释放
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
		return true;
    	
    }
    
    @Override
    public IPage<DataBzjDeviceEntity> selectPage(DeviceAndSaleQueryDTO dataDto) {
    	Page<DataBzjDeviceEntity> page = new Page<>(dataDto.getPage(), dataDto.getLimit());
    	IPage<DataBzjDeviceEntity> select = getBaseMapper().getSelect(page,dataDto);
    	return select;
    }
    @Override
    public List<DataBzjDeviceEntity> selectList(DataBzjDeviceEntity dataDto) {
    	List<DataBzjDeviceEntity> select = getBaseMapper().getSelectList(dataDto);
    	return select;
    }
    
}