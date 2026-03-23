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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceResponseBody;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceResponseBody.DeviceInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.project.aliyun.AliYunService;
import com.company.project.common.exception.BusinessException;
import com.company.project.common.exception.code.BaseResponseCode;
import com.company.project.entity.DataBzjDeviceEntity;
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
	
	 /**
     * 同步播种机设备
     *
     * @return -
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Scheduled(cron = "0 0/5 * * * ?")
    public boolean sync() {
    	
		long timeMillis = System.currentTimeMillis();
        
        List<DeviceInfo> queryIotBzjDevice = aliYunService.getBzjDevice();

        List<DataBzjDeviceEntity> bzjDeviceList = queryIotBzjDevice.stream()
                .map(device -> {
                    DataBzjDeviceEntity entity = convertToDataBzjDeviceEntity(device);
                    return entity;
                })
                .collect(Collectors.toList());
        boolean batchResult = saveOrUpdateBatch(bzjDeviceList);
        timeMillis = System.currentTimeMillis()- timeMillis ;
        
        return false;
    }

    
    /**
     *  更新定位信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Scheduled(cron = "0 3/10 * * * ?")
    public void syncLocation() {

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
    public JSONObject pointMap() {
    	//在线数量
    	Map<String, Object> pointMap = getBaseMapper().pointMap();
    	
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
    	
    	
    	list.forEach(li -> {
    		//3、重新加载消息
    		messageService.reRead(li);
    	});
    	
	}
    
}