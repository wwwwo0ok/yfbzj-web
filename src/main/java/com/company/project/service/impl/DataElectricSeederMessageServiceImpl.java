package com.company.project.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.sdk.service.iot20180120.AsyncClient;
import com.aliyun.sdk.service.iot20180120.models.ListAnalyticsDataRequest;
import com.aliyun.sdk.service.iot20180120.models.ListAnalyticsDataResponse;
import com.aliyun.sdk.service.iot20180120.models.ListAnalyticsDataResponseBody;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.entity.DataElectricSeederMessageEntity;
import com.company.project.entity.DataElectricSeederMessageLineEntity;
import com.company.project.mapper.DataElectricSeederMessageMapper;
import com.company.project.service.DataBzjDeviceService;
import com.company.project.service.DataElectricSeederMessageLineService;
import com.company.project.service.DataElectricSeederMessageService;
import com.company.project.util.AliyunIotConstants;
import com.company.project.util.DataAnalysisUtil;


@Service("dataElectricSeederMessageService")
public class DataElectricSeederMessageServiceImpl extends ServiceImpl<DataElectricSeederMessageMapper, DataElectricSeederMessageEntity> implements DataElectricSeederMessageService {

    private final DataBzjDeviceServiceImpl dataBzjDeviceService_1;


	@Autowired
	AsyncClient client;
	
	@Autowired
	@Lazy
	DataBzjDeviceService dataBzjDeviceService;
	
	@Autowired
	DataElectricSeederMessageLineService dataElectricSeederMessageLineService;
	
	 @Autowired
	    private DataElectricSeederMessageMapper dataElectricSeederMessageMapper;

    DataElectricSeederMessageServiceImpl(DataBzjDeviceServiceImpl dataBzjDeviceService_1) {
        this.dataBzjDeviceService_1 = dataBzjDeviceService_1;
    }
	
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
    	
		long timeMillis = System.currentTimeMillis();
    	
		//获取所有的设备
		List<DataBzjDeviceEntity> dqlist = dataBzjDeviceService.list(new QueryWrapper<DataBzjDeviceEntity>().eq("device_type",2));
		
		dqlist.forEach(this::insertNewData);
		
		
		
		//获取所有的设备
		List<DataBzjDeviceEntity> jxlist = dataBzjDeviceService.list(new QueryWrapper<DataBzjDeviceEntity>().eq("device_type",1));
		
		jxlist.forEach(this::insertNewData);
		
		
		
		
//		DataBzjDeviceEntity dq = new DataBzjDeviceEntity();
//		dq.setDeviceName("gkBdNUruoMBVbCHbbzLr");//播种机
//		DataBzjDeviceEntity jx = new DataBzjDeviceEntity();
//		dq.setDeviceName("iiAriJtCvsFLPW5zfn1E");//监控器
//		List<DataBzjDeviceEntity> list = new ArrayList<>(Arrays.asList(dq,jx));
		
		//循环调用增量保存
        timeMillis = System.currentTimeMillis()- timeMillis ;
        
        
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
    	int currentPage = 1;
        final int pageSize = 100;
        
    	try {
	    	//查询最大结束时间位起始时间
	    	Date beginTimeDate = selectMaxDataTimeByDevice(device);
	    	
	    	
	    	String deviceType = device.getDeviceType();
	    	
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
                    .value(device.getDeviceName())
                    .fieldName("device_name")
                    .build();
	    	
	        while (true) {
	        	
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
	            List<DataElectricSeederMessageEntity> iotLit = new ArrayList<>();
	            
                if(list == null) break;
	            for (Map<String, Object> data : list) {
	                String iotId = data.get("iot_id").toString();
	                String deviceName = data.get("device_name").toString();
	                String timestamp = data.get("timestamp").toString();
	                String hexData = data.get("BZJ").toString().trim();
	                DataElectricSeederMessageEntity entity = new DataElectricSeederMessageEntity();
	                if("2".equals(deviceType)) {
	                	entity = DataAnalysisUtil.analysisElectricHexStr(hexData);
	                }
	                if("1".equals(deviceType)) {
	                	entity = DataAnalysisUtil.analysisMachineHexStr(hexData);
	                }
	                
	                entity.setLotId(iotId);
	                entity.setDeviceName(deviceName);
	                entity.setDataTime(new Date(Long.parseLong(timestamp)));
	                entity.setAliyun(hexData);
	                
	                
	                iotLit.add(entity);
	            }
	            
	            saveBatch(iotLit);
	            
	            iotLit.forEach(li -> {
	            	String id = li.getId();
	            	List<DataElectricSeederMessageLineEntity> lines = li.getLines();
	            	lines.forEach(line -> line.setMessageId(id));
	            	dataElectricSeederMessageLineService.saveBatch(lines);
	            	
	            });
	            
	            
	            
	            ListAnalyticsDataResponseBody body = resp.getBody();
	            
	            // 关键：正确判断是否还有下一页
	            if (!resp.getBody().getData().getHasNext()) {
	                break;
	            }
	            currentPage++;
	        }
	    	
	    	
	    	
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

	
	
}