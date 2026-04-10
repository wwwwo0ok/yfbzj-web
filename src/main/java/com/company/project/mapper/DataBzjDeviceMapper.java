package com.company.project.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.project.dto.DeviceAndSaleQueryDTO;
import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.entity.DataElectricSeederMessageEntity;

/**
 * 播种机设备表（来自物联网）
 * 
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-11-29 13:21:40
 */
public interface DataBzjDeviceMapper extends BaseMapper<DataBzjDeviceEntity> {

	Map<String,Object> pointMap(@Param("query") DataBzjDeviceEntity dataBzjDevice);
	
	IPage<DataBzjDeviceEntity> getSelect(
			Page<DataBzjDeviceEntity> page, @Param("query")DeviceAndSaleQueryDTO dataDto);
	
}
