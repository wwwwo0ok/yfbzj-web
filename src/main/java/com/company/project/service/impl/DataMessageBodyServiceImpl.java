package com.company.project.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceResponseBody.DeviceInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.project.aliyun.AliYunService;
import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.entity.DataMessageBodyEntity;
import com.company.project.entity.DataMessageTypeEntity;
import com.company.project.mapper.DataMessageBodyMapper;
import com.company.project.service.DataBzjDeviceService;
import com.company.project.service.DataMessageBodyService;
import com.company.project.service.DataMessageTypeService;


@Service("dataMessageBodyService")
public class DataMessageBodyServiceImpl extends ServiceImpl<DataMessageBodyMapper, DataMessageBodyEntity> implements DataMessageBodyService {

    private final DataMessageTypeServiceImpl dataMessageTypeService;

	@Autowired
	AliYunService aliYunService;
	
	@Autowired
	private DataMessageTypeService messageTypeService;
	
	@Autowired
	private DataBzjDeviceService bzjService;

	@Autowired
	private RedissonClient redissonClient;
	
	private ConcurrentHashMap<String, List<DataMessageTypeEntity>> typeMap = new ConcurrentHashMap<>();

    DataMessageBodyServiceImpl(DataMessageTypeServiceImpl dataMessageTypeService) {
        this.dataMessageTypeService = dataMessageTypeService;
    }
	
	/**
     * 增量同步全部电驱消息内容（2025年12月开始）
     */
    @Override
    @Scheduled(cron = "0 3/5 * * * ?")
    public boolean sync() {

    	// 定义锁的key，可以根据业务需求调整
        String lockKey = "sync:data:messageBody:lock";
    	
    	RLock lock = redissonClient.getLock(lockKey);
    	
    	try {
            // 尝试获取锁，最多等待waitTime毫秒
            boolean isLocked = lock.tryLock();
            if (!isLocked) {
                // 获取锁失败
                return false;
            }
            
            long timeMillis = System.currentTimeMillis();
        	
    		LambdaQueryWrapper<DataBzjDeviceEntity> productQueryWrapper = Wrappers.lambdaQuery();
        	//查询条件示例
        	productQueryWrapper.in(DataBzjDeviceEntity::getDeviceStatus, "ONLINE","OFFLINE");
    		
    		
    		//获取所有的设备  
    		List<DataBzjDeviceEntity> dqlist = bzjService.list(productQueryWrapper);
    		
    		
    		dqlist.forEach(this::insertNewData);
    		
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
     * 增量保存
     * @param device
     * @return
     */
    @Override
    @Transactional
    public void insertNewData(DataBzjDeviceEntity device) {
    	
        
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
            
	    	String productKey = device.getProductKey();
	    	
	    	if(!typeMap.contains(productKey)) {
	    		
	    		LambdaQueryWrapper<DataMessageTypeEntity> typeWrapper = new LambdaQueryWrapper<>();
	    		typeWrapper.eq(DataMessageTypeEntity::getProductKey, productKey);
	    		
	    		List<DataMessageTypeEntity> list = dataMessageTypeService.list(typeWrapper);
	    		
	    		typeMap.put(productKey, list);
	    		
	    	}
	    	List<DataMessageTypeEntity> list2 = typeMap.get(productKey);
	    	
	    	if(list2 == null || list2.isEmpty()) {
	    		return;
	    	}
	        
            List<Map<String, Object>> list = aliYunService.getMessageBody(device.getDeviceName(),device.getRawdata(),device.getProductKey(),beginTimeTimestamp, endTimeTimestamp);
	        
            List<DataMessageBodyEntity> bodyEntities = new ArrayList<>();
            
			for (Map<String, Object> data : list) {
				String iotId = data.get("iot_id").toString();
				String timestamp = data.get("timestamp").toString();
				
				Set<String> keySet = data.keySet();
				
				for(DataMessageTypeEntity typeEntity : list2) {
					
					String keyString = typeEntity.getCode();
					
					if(data.containsKey(keyString)) {
						String hexData = data.get(keyString).toString().trim();
						if(!StringUtils.isEmpty(hexData)) {
							
							DataMessageBodyEntity entity = new DataMessageBodyEntity();
							
							entity.setDataTime(new Date(Long.parseLong(timestamp)));
							entity.setLotId(iotId);
							entity.setMessageBody(hexData);
							entity.setCreateTime(new Date());
							entity.setMessageCode(keyString);
							entity.setInvalidateFlag(0);
							
							bodyEntities.add(entity);
						}
					}
				}
			}
            
			
			
	        saveBatch(bodyEntities);
            
	    	
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    }

	/**
	 * 查询最大时间。
	 * @param device
	 * @return
	 */
	private Date selectMaxDataTimeByDevice(DataBzjDeviceEntity device) {
		LambdaQueryWrapper<DataMessageBodyEntity> lambdaQuery = new LambdaQueryWrapper<>();
	    lambdaQuery.eq(DataMessageBodyEntity::getLotId, device.getLotId())
	               .select(DataMessageBodyEntity::getDataTime)
	               .orderByDesc(DataMessageBodyEntity::getDataTime)
	               .last("LIMIT 1"); // 只取第一条，即最大值
	    
	    DataMessageBodyEntity entity = baseMapper.selectOne(lambdaQuery);
	    return entity != null ? entity.getDataTime() : null;
	}

}