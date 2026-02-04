package com.company.project.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.project.entity.DataBzjDeviceEntity;

/**
 * 播种机设备表（来自物联网）
 * 
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-11-29 13:21:40
 */
public interface DataBzjDeviceMapper extends BaseMapper<DataBzjDeviceEntity> {

	Map<String,Object> pointMap();
	
	
	
}
