package com.company.project.service.impl;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import com.company.project.util.AliyunIotConstants;
import com.company.project.util.DataAnalysisUtil;


@Service("dataElectricSeederMessageService")
public class DataElectricSeederMessageServiceImpl extends ServiceImpl<DataElectricSeederMessageMapper, DataElectricSeederMessageEntity> implements DataElectricSeederMessageService {

    private final AliYunService aliYunService;

    private final DataBzjDeviceServiceImpl dataBzjDeviceService_1;


	@Autowired
	AsyncClient client;
	
	@Autowired
	@Lazy
	DataBzjDeviceService dataBzjDeviceService;
	
	@Autowired
	DataElectricSeederMessageLineService dataElectricSeederMessageLineService;
	@Autowired
	DataAlarmService dataAlarmService;
	
	 @Autowired
	    private DataElectricSeederMessageMapper dataElectricSeederMessageMapper;

    DataElectricSeederMessageServiceImpl(DataBzjDeviceServiceImpl dataBzjDeviceService_1, AliYunService aliYunService) {
        this.dataBzjDeviceService_1 = dataBzjDeviceService_1;
        this.aliYunService = aliYunService;
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
		List<DataBzjDeviceEntity> dqlist = dataBzjDeviceService.list();
		
		dqlist.forEach(this::insertNewData);
		
		
//		
//		//获取所有的设备
//		List<DataBzjDeviceEntity> jxlist = dataBzjDeviceService.list(new QueryWrapper<DataBzjDeviceEntity>().eq("device_type",1));
//		
//		jxlist.forEach(this::insertNewData);
		
		
		
		
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
    	
        
    	try {
	    	//查询最大结束时间位起始时间
	    	Date beginTimeDate = selectMaxDataTimeByDevice(device);
	    	
	    	String deviceType = device.getDeviceType();

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
	        
            
	        saveBatch(iotLit);
            
            iotLit.forEach(li -> {
            	String id = li.getId();
            	List<DataElectricSeederMessageLineEntity> lines = li.getLines();
            	lines.forEach(line -> line.setMessageId(id));
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

	
	
}