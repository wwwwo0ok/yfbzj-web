package com.company.project.service.impl;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.sdk.service.iot20180120.AsyncClient;
import com.aliyun.sdk.service.iot20180120.models.ListAnalyticsDataRequest;
import com.aliyun.sdk.service.iot20180120.models.ListAnalyticsDataResponse;
import com.aliyun.sdk.service.iot20180120.models.ListAnalyticsDataResponseBody;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceRequest;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceResponse;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceResponseBody;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.project.common.exception.BusinessException;
import com.company.project.common.exception.code.BaseResponseCode;
import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.mapper.DataBzjDeviceMapper;
import com.company.project.service.DataBzjDeviceService;
import com.company.project.service.DataElectricSeederMessageService;
import com.company.project.util.AliyunIotConstants;
import com.company.project.util.DataAnalysisUtil;

import lombok.extern.slf4j.Slf4j;


@Service("dataBzjDeviceService")
@Slf4j
public class DataBzjDeviceServiceImpl extends ServiceImpl<DataBzjDeviceMapper, DataBzjDeviceEntity> implements DataBzjDeviceService {

	@Autowired
	AsyncClient client;
	
	@Autowired
	DataElectricSeederMessageService messageService;
	
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
    

	private void queryDeviceRequest(AsyncClient client, List<QueryDeviceResponseBody.DeviceInfo> deviceInfos, Integer currentPage, Integer pageSize) {
        QueryDeviceRequest queryDeviceRequest = QueryDeviceRequest.builder()
                .iotInstanceId(AliyunIotConstants.IOT_INSTANCE_ID)
                .productKey(AliyunIotConstants.PRODUCT_KEY)
                .build();
        CompletableFuture<QueryDeviceResponse> response = client.queryDevice(queryDeviceRequest);
        try {
            QueryDeviceResponse resp = response.get();
            client.close();
            QueryDeviceResponseBody body = resp.getBody();
            List<QueryDeviceResponseBody.DeviceInfo> deviceInfo = body.getData().getDeviceInfo();
            deviceInfos.addAll(deviceInfo);
            if (body.getPageCount() > currentPage) {
                queryDeviceRequest(client, deviceInfos, currentPage + 1, pageSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	

    /**
     *  定时同步未知类型的数据，并且试图绑定类型，绑定失败的不处理。
     */
    @Scheduled(cron = "0 10/30 * * * ?")
	public void syncOtherMessage() {
		
		//查询列表
		List<DataBzjDeviceEntity> list = list(new QueryWrapper<DataBzjDeviceEntity>().eq("device_type",0));
		
		

		//循环调用增量保存
		list.forEach(this::selectAndCheck);
		
		
	}
    
    /**
     *  接收数据并且检查
     */
    @Transactional
    public void selectAndCheck(DataBzjDeviceEntity deviceEntity) {
    	
    	int currentPage = 1;
        final int pageSize = 1;
        try {
	    	//查询最大结束时间位起始时间
	    	Date beginTimeDate = messageService.selectMaxDataTimeByDevice(deviceEntity);
	    	
	    	if(beginTimeDate == null) {
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	            //只处理新版代码之后的数据。
	            beginTimeDate = sdf.parse("2025-12-01 00:00:00");
	    	}
	    	
	    	Date endTimeDate = new Date();
            long beginTimeTimestamp = beginTimeDate.getTime();
            long endTimeTimestamp = endTimeDate.getTime();
            ListAnalyticsDataRequest.Condition condition0 = ListAnalyticsDataRequest.Condition.builder()
                    .operate("BETWEEN")
                    .fieldName("timestamp")
                    .betweenStart(String.valueOf(beginTimeTimestamp))
                    .betweenEnd(String.valueOf(endTimeTimestamp))
                    .build();
            ListAnalyticsDataRequest.Condition condition1 = ListAnalyticsDataRequest.Condition.builder()
                    .operate("=")
                    .value(deviceEntity.getDeviceName())
                    .fieldName("device_name")
                    .build();
	    	
	        	
        	 ListAnalyticsDataRequest listAnalyticsDataRequest = ListAnalyticsDataRequest.builder()
                     .iotInstanceId(AliyunIotConstants.IOT_INSTANCE_ID)
                     .apiPath(AliyunIotConstants.RAWDATA_GET)
                     .condition(java.util.Arrays.asList(
                             condition0,
                             condition1
                     ))
                     .pageSize(pageSize)
                     .pageNum(currentPage)
                     .build();
        	
        	CompletableFuture<ListAnalyticsDataResponse> response = client.listAnalyticsData(listAnalyticsDataRequest);
            ListAnalyticsDataResponse resp = response.get();
            ListAnalyticsDataResponseBody.Data bzjData = resp.getBody().getData(); 
        	 
            List<Map<String, Object>> list = JSON.parseObject(bzjData.getResultJson(), new TypeReference<List<Map<String, Object>>>() {
            });
            if(list == null) return;
            
            for (Map<String, Object> data : list) {
                String iotId = data.get("iot_id").toString();
                String deviceName = data.get("device_name").toString();
                String timestamp = data.get("timestamp").toString();
                String hexData = data.get("BZJ").toString().trim();
                
                
                String checkDeviceType = DataAnalysisUtil.checkDeviceType(hexData);
                
                if(!"0".equals(checkDeviceType)) {
                	deviceEntity.setDeviceType(checkDeviceType);
                	//修改类型
                	updateById(deviceEntity);
                	return;
                }
                
            }
	    	
	    	
	    	
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
}