package com.company.project.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.project.aliyun.RedisDeviceManager;
import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.entity.DataElectricSeederMessageEntity;
import com.company.project.entity.DataMessageDayEntity;
import com.company.project.mapper.DataMessageDayMapper;
import com.company.project.service.DataMessageDayService;
import com.company.project.util.DateUtil;


@Service("dataMessageDayService")
public class DataMessageDayServiceImpl extends ServiceImpl<DataMessageDayMapper, DataMessageDayEntity> implements DataMessageDayService {

	
	@Autowired
	private RedisDeviceManager redisDeviceManager;

	
	@Override
	@Transactional
	public void addDayData(String dayString) {
	    
	    List<DataMessageDayEntity> dayData = getBaseMapper().addDayData(
	        DateUtil.getStartOfDayString(dayString),
	        DateUtil.getEndOfDayString(dayString)
	    );
	    
	    String monthString = dayString.substring(0,7);
	    String yearString = dayString.substring(0,4);
	    
	    List<DataBzjDeviceEntity> onlineDeviceList = redisDeviceManager.getOnlineDeviceList(dayString);

	    // 提取在线设备的 lotId 集合
	    Set<String> onlineLotIds = onlineDeviceList.stream()
	            .map(DataBzjDeviceEntity::getLotId)   // 根据实际 getter 方法名调整
	            .collect(Collectors.toSet());

	    // 设置 dataStatus
	    if (dayData != null && !dayData.isEmpty()) {
	        for (DataMessageDayEntity entity : dayData) {
	        	
	        	entity.setDayString(dayString);
	        	entity.setYearString(yearString);
	        	entity.setMonthString(monthString);
	            if (onlineLotIds.contains(entity.getLotId())) {
	                entity.setDataStatus(0);
	            } else {
	                entity.setDataStatus(1);
	            }
	        }
	    }

	    dayData.forEach(this::saveOrUpdateList);
	}


	private void saveOrUpdateList(DataMessageDayEntity dayData) {
	    // 查询是否存在
	    LambdaQueryWrapper<DataMessageDayEntity> queryWrapper = new LambdaQueryWrapper<>();
	    queryWrapper.eq(DataMessageDayEntity::getLotId, dayData.getLotId())
	                .eq(DataMessageDayEntity::getDayString, dayData.getDayString());
	    DataMessageDayEntity exist = getOne(queryWrapper);
	    
	    if (exist != null) {
	        // 存在则更新（保留原有id）
	        dayData.setId(exist.getId());
	        updateById(dayData);
	    } else {
	        // 不存在则插入
	        save(dayData);
	    }
	}
	@Override
	public List<DataMessageDayEntity> queryDayData(DataMessageDayEntity queryEntity){
		if(
				StringUtils.isNotBlank(queryEntity.getDayString())
				||
				StringUtils.isNotBlank(queryEntity.getMonthString())
				||
				StringUtils.isNotBlank(queryEntity.getYearString())
				) {
			
			return getBaseMapper().queryDayData(queryEntity);
		}
		return new ArrayList<>();
	}
}