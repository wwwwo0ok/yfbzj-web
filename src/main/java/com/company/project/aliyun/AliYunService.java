package com.company.project.aliyun;

import java.util.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.sdk.service.iot20180120.AsyncClient;
import com.aliyun.sdk.service.iot20180120.models.ListAnalyticsDataRequest;
import com.aliyun.sdk.service.iot20180120.models.ListAnalyticsDataResponse;
import com.aliyun.sdk.service.iot20180120.models.ListAnalyticsDataResponseBody;
import com.aliyun.sdk.service.iot20180120.models.QueryDevicePropertyDataRequest;
import com.aliyun.sdk.service.iot20180120.models.QueryDevicePropertyDataResponse;
import com.aliyun.sdk.service.iot20180120.models.QueryDevicePropertyDataResponseBody;
import com.aliyun.sdk.service.iot20180120.models.QueryDevicePropertyDataResponseBody.Data;
import com.aliyun.sdk.service.iot20180120.models.QueryDevicePropertyDataResponseBody.PropertyInfo;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceRequest;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceResponse;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceResponseBody;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceResponseBody.DeviceInfo;
import com.company.project.entity.DataElectricSeederMessageEntity;
import com.company.project.service.DataProductService;
import com.company.project.strategy.CodeReadStrategy;
import com.company.project.util.AliyunIotConstants;

/**
 *  阿里云服务封装
 */
@Component
public class AliYunService {

	@Autowired
	private AsyncClient client;
	
	/**
	 *  产品策略
	 */
	@Autowired
	private Map<String, CodeReadStrategy> strategyMap;
	
	/**
	 *  批次服务
	 */
	@Autowired
	private DataProductService productService;
	
	
	/**
	 * 获取所有设备
	 * @return
	 */
	public List<DeviceInfo> getBzjDevice(){
		
		
		List<DeviceInfo> result = new ArrayList<>();
		
		strategyMap.forEach((k,v) -> {
		
			if(productService.isActive(v.getCode())) {
				
				List<DeviceInfo> queryIotBzjDevice = queryIotBzjDevice(v.getCode());
				
				result.addAll(queryIotBzjDevice);
			}
		});
		
		
		return result;
	}
	/**
	 * 获取所有设备
	 * @return
	 */
	public List<DeviceInfo> getBzjDevice(String productKey){
		
		List<DeviceInfo> result = new ArrayList<>();
		if(strategyMap.containsKey(productKey)){
			
			CodeReadStrategy v = strategyMap.get(productKey);
			if(productService.isActive(v.getCode())) {
				
				List<DeviceInfo> queryIotBzjDevice = queryIotBzjDevice(v.getCode());
				
				result.addAll(queryIotBzjDevice);
			}
		}
		
		return result;
	}
	
	/**
     * 查询指定产品下的所有的设备列表
     *
     * @return -
     */
    public List<QueryDeviceResponseBody.DeviceInfo> queryIotBzjDevice(String productKey) {
        List<QueryDeviceResponseBody.DeviceInfo> deviceInfos = new ArrayList<>();
        int currentPage = 1;
        final int pageSize = 100;
        try {
	        while (true) {
	            QueryDeviceResponse resp = client.queryDevice(createRequest(currentPage,pageSize,productKey)).get();
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

    private QueryDeviceRequest createRequest(int currentPage, int pageSize,String productKey) {
        return QueryDeviceRequest.builder()
            .iotInstanceId(AliyunIotConstants.IOT_INSTANCE_ID)
            .productKey(productKey)
            .pageSize(pageSize)       // 显式设置每页大小
            .currentPage(currentPage) // 设置当前页码
            .build();
    }
	
    /**
     * 获取满足条件的设备消息
     */
    public List<DataElectricSeederMessageEntity> getMessage(String deviceName,String rawData,String productKey,long beginTimeTimestamp,long endTimeTimestamp) {
    	
    	List<DataElectricSeederMessageEntity> iotLit = new ArrayList<>();
    	
    	
    	int currentPage = 1;
        final int pageSize = 100;
    	try {
	    	ListAnalyticsDataRequest.Condition condition0 = ListAnalyticsDataRequest.Condition.builder()
	                .operate("BETWEEN")
	                .fieldName("timestamp")
	                .betweenStart(String.valueOf(beginTimeTimestamp))
	                .betweenEnd(String.valueOf(endTimeTimestamp))
	                .build();
	        ListAnalyticsDataRequest.Condition condition1 = ListAnalyticsDataRequest.Condition.builder()
	                .operate("=")
	                .value(deviceName)
	                .fieldName("device_name")
	                .build();
	    	
	        while (true) {
	        	
	        	 ListAnalyticsDataRequest listAnalyticsDataRequest = ListAnalyticsDataRequest.builder()
	                     .iotInstanceId(AliyunIotConstants.IOT_INSTANCE_ID)
	                     .apiPath(rawData)
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
	            
	            if(list == null) break;
	            for (Map<String, Object> data : list) {
	                String iotId = data.get("iot_id").toString();
	                String timestamp = data.get("timestamp").toString();
	                String dataTimeRaw = data.get("date_time").toString();
	                LocalDateTime dateTime = null;
	                // 如果毫秒部分不足3位，补零
	                if (dataTimeRaw.matches("\\d{14}\\.\\d{1,2}")) {
	                    // 补足到3位毫秒
	                    String[] parts = dataTimeRaw.split("\\.");
	                    String millis = parts[1];
	                    while (millis.length() < 3) millis += "0";
	                    dataTimeRaw = parts[0] + "." + millis;
	                }
	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS");
	                dateTime = LocalDateTime.parse(dataTimeRaw, formatter);
	                if(data.containsKey("BZJ")) {
	                	String hexData = data.get("BZJ").toString().trim();
	                	DataElectricSeederMessageEntity entity = new DataElectricSeederMessageEntity();
	                	entity.setLotId(iotId);
	                	entity.setDeviceName(deviceName);
	                	entity.setDataTime(dateTime);
	                	entity.setAliyun(hexData);
	                	
	                	iotLit.add(entity);
	                }
	            }
	            
	            ListAnalyticsDataResponseBody body = resp.getBody();
	            
	            // 关键：正确判断是否还有下一页
	            if (!resp.getBody().getData().getHasNext()) {
	                break;
	            }
	            currentPage++;
	        }
    	}catch (Exception e) {
    		e.printStackTrace();
		}
        
        return iotLit;
    	
    }
    /**
     * 获取满足条件的设备消息
     */
    public List<Map<String, Object>> getMessageBody(String deviceName,String rawData,String productKey,long beginTimeTimestamp,long endTimeTimestamp) {
    	
    	List<Map<String, Object>> resultList = new ArrayList<>();
    	
    	int currentPage = 1;
    	final int pageSize = 100;
    	try {
    		ListAnalyticsDataRequest.Condition condition0 = ListAnalyticsDataRequest.Condition.builder()
    				.operate("BETWEEN")
    				.fieldName("timestamp")
    				.betweenStart(String.valueOf(beginTimeTimestamp))
    				.betweenEnd(String.valueOf(endTimeTimestamp))
    				.build();
    		ListAnalyticsDataRequest.Condition condition1 = ListAnalyticsDataRequest.Condition.builder()
    				.operate("=")
    				.value(deviceName)
    				.fieldName("device_name")
    				.build();
    		
    		while (true) {
    			
    			ListAnalyticsDataRequest listAnalyticsDataRequest = ListAnalyticsDataRequest.builder()
    					.iotInstanceId(AliyunIotConstants.IOT_INSTANCE_ID)
    					.apiPath(rawData)
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
    			if(list != null) {
    				resultList.addAll(list);
    			}
    			
    			
    			// 关键：正确判断是否还有下一页
    			if (!resp.getBody().getData().getHasNext()) {
    				break;
    			}
    			currentPage++;
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return resultList;
    	
    }
    
    public Map<String, Double> getLocation(String deviceName,String rawData,String productKey,long beginTimeTimestamp,long endTimeTimestamp){
    	
    	
    	Map<String, Double> resultMap = null;
    	
    	
    	
    	int currentPage = 1;
        final int pageSize = 100;
    	try {
	        	
             QueryDevicePropertyDataRequest queryDevicePropertyDataRequest = QueryDevicePropertyDataRequest.builder()
                     .iotInstanceId(AliyunIotConstants.IOT_INSTANCE_ID)
                     .productKey(productKey)
                     .deviceName(deviceName)
                     .startTime(beginTimeTimestamp)
                     .identifier("BZJ2")
                     .endTime(endTimeTimestamp)
                     .asc(0)
                     .pageSize(pageSize)
                     // Request-level configuration rewrite, can set Http request parameters, etc.
                     // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                     .build();

             // Asynchronously get the return value of the API request
             CompletableFuture<QueryDevicePropertyDataResponse> response = client.queryDevicePropertyData(queryDevicePropertyDataRequest);
             // Synchronously get the return value of the API request
             QueryDevicePropertyDataResponse resp = response.get();
             // Asynchronous processing of return values
             
             List<PropertyInfo> propertyInfoList = Collections.emptyList();

             if (resp != null && resp.getBody() != null) {
                 QueryDevicePropertyDataResponseBody body = resp.getBody();
                 if (body.getData() != null) {
                     Data data = body.getData();
                     if (data.getList() != null) {
                         propertyInfoList = data.getList().getPropertyInfo();
                         if (propertyInfoList == null) {
                             propertyInfoList = Collections.emptyList();
                         }
                     }
                 }
             }


             
             for(PropertyInfo info:propertyInfoList ){
            	 
            	 String value = info.getValue();
            	 
            	 if(value.contains(",")) {
                	String[] split = value.split(",");
                	if(split.length == 2) {
                		String x = split[1];
                		String y = split[0];
                		
                		double position_x = 0;
                		double position_y = 0;
                		
                		if(!StringUtils.isEmpty(x)) {
                			position_x = Double.parseDouble(x);
                		}
                		if(!StringUtils.isEmpty(y)) {
                			position_y = Double.parseDouble(y);
                		}
                		if(position_x!=0&&position_y!=0) {
                			resultMap = new HashMap<>();
                			resultMap.put("x", position_x);
                			resultMap.put("y", position_y);
                			break;
                		}
                	}
                }
             };
	            
	            
    	}catch (Exception e) {
    		e.printStackTrace();
		}
        
    	return resultMap;
    	
    }
	
    
    
}
