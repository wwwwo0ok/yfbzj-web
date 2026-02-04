package com.company.project.service.impl;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.sdk.service.iot20180120.AsyncClient;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceRequest;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceResponse;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceResponseBody;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.project.common.exception.BusinessException;
import com.company.project.common.exception.code.BaseResponseCode;
import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.mapper.DataBzjDeviceMapper;
import com.company.project.service.DataBzjDeviceService;
import com.company.project.util.AliyunIotConstants;

import lombok.extern.slf4j.Slf4j;


@Service("dataBzjDeviceService")
@Slf4j
public class DataBzjDeviceServiceImpl extends ServiceImpl<DataBzjDeviceMapper, DataBzjDeviceEntity> implements DataBzjDeviceService {

	@Autowired
	AsyncClient client;
	
	
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
    	
    		List<QueryDeviceResponseBody.DeviceInfo> iotBzjDeviceList  = queryIotBzjDevice();

            log.info("查询到 {} 条设备数据", iotBzjDeviceList.size());
            
            // 确保每次转换创建新的对象
            List<DataBzjDeviceEntity> bzjDeviceList = iotBzjDeviceList.stream()
                    .map(device -> {
                        DataBzjDeviceEntity entity = convertToDataBzjDeviceEntity(device);
                        log.debug("转换设备: {}, 实体: {}", device.getDeviceId(), entity.getLotId());
                        return entity;
                    })
                    .collect(Collectors.toList());
    		
            log.info("转换后得到 {} 条实体数据", bzjDeviceList.size());

            boolean batchResult = saveOrUpdateBatch(bzjDeviceList);
            
            timeMillis = System.currentTimeMillis()- timeMillis ;
            
            
            
            return false;
    }
    
 // 实体转换封装方法
    private DataBzjDeviceEntity convertToDataBzjDeviceEntity(QueryDeviceResponseBody.DeviceInfo device) {
        DataBzjDeviceEntity bzjDevice = new DataBzjDeviceEntity();
        bzjDevice.setLotId(device.getIotId());
        bzjDevice.setDeviceName(device.getDeviceName());
        bzjDevice.setNickname(device.getNickname());
        bzjDevice.setDeviceSecret(device.getDeviceSecret());
        bzjDevice.setDeviceStatus(device.getDeviceStatus());
        
        //默认0
        if(StringUtils.isBlank(device.getNickname())) {
        	bzjDevice.setDeviceType("0");
        //包含电驱是电驱
        }else if(device.getNickname().contains("电驱")) {
        	bzjDevice.setDeviceType("2");
        }else{//否则是监控器
        	bzjDevice.setDeviceType("1");
        }
        
        

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
	
    
    
    /**
     * 查询指定产品下的所有的设备列表
     *
     * @return -
     */
    public List<QueryDeviceResponseBody.DeviceInfo> queryIotBzjDevice() {
        List<QueryDeviceResponseBody.DeviceInfo> deviceInfos = new ArrayList<>();
        int currentPage = 1;
        final int pageSize = 100;
        
        
        try {
	        while (true) {
	            QueryDeviceResponse resp = client.queryDevice(createRequest(currentPage,pageSize)).get();
	            List<QueryDeviceResponseBody.DeviceInfo> pageData = resp.getBody().getData().getDeviceInfo();
	            deviceInfos.addAll(pageData);
	            Integer pageCount = resp.getBody().getPageCount();
	            // 关键：正确判断是否还有下一页
	            if (currentPage >= resp.getBody().getPageCount()) {
	                break;
	            }
	            currentPage++;
	        }
        }catch(Exception e) {
        	e.printStackTrace();
        }
        return deviceInfos;
    }

    private QueryDeviceRequest createRequest(int currentPage, int pageSize) {
        return QueryDeviceRequest.builder()
            .iotInstanceId(AliyunIotConstants.IOT_INSTANCE_ID)
            .productKey(AliyunIotConstants.PRODUCT_KEY)
            .pageSize(pageSize)       // 显式设置每页大小
            .currentPage(currentPage) // 设置当前页码
            .build();
    }
    
    
    @Override
    public JSONObject pointMap() {
    	//在线数量
    	Map<String, Object> pointMap = getBaseMapper().pointMap();
    	
    	return new JSONObject(pointMap);
    }
    
}