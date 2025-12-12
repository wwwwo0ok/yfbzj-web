package com.company.project.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.entity.DataElectricSeederMessageEntity;

/**
 * 电驱播种机消息记录表 数据来自于播种机
 * 
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-12-02 13:37:41
 */
public interface DataElectricSeederMessageMapper extends BaseMapper<DataElectricSeederMessageEntity> {
	
    Date selectMaxDataTimeByDevice(@Param("device") DataBzjDeviceEntity device);

	IPage<DataElectricSeederMessageEntity> selectAll(
			Page<DataElectricSeederMessageEntity> page, 
			@Param("query") DataElectricSeederMessageEntity queryEntity
			);

    
}
