package com.company.project.service;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.entity.DataElectricSeederMessageEntity;

/**
 * 电驱播种机消息记录表 数据来自于播种机
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-12-02 13:37:41
 */
public interface DataElectricSeederMessageService extends IService<DataElectricSeederMessageEntity> {

	/**
	 * 同步消息内容
	 * @return 
	 */
	boolean sync();

	/**
	 * 增量保存
	 * @param device
	 * @return
	 */
	boolean insertNewData(DataBzjDeviceEntity device);

	

	
	Date selectMaxDataTimeByDevice(@Param("device") DataBzjDeviceEntity device);

	boolean saveByHexStr(DataBzjDeviceEntity device, Date dataTime, String hexData);

	void insertNewData(String lotId);

	IPage<DataElectricSeederMessageEntity> getMessageList(DataElectricSeederMessageEntity queryEntity);
}

