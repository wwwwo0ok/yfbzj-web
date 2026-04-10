package com.company.project.service;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceResponseBody.DeviceInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
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
public interface DataBzjDeviceService extends IService<DataBzjDeviceEntity> {


	/**
	 * 同步物联网数据
	 */
	boolean sync();

	/**
	 * 显示界面的数据
	 * @param dataBzjDevice 
	 * @return
	 */
	JSONObject pointMap(DataBzjDeviceEntity dataBzjDevice);

	/**
	 *  更新定位信息
	 */
	void syncLocation();

	/**
	 * 根据批号重新加载消息
	 * @param productKey
	 */
	void reRead(String productKey);

	/**
	 * 根据批号同步
	 * @param productKey
	 */
	boolean sync(String productKey);

	boolean reRead(DataBzjDeviceEntity entity);

	boolean reRead(DataElectricSeederMessageEntity messageEntity);

	/**
	 * 产品复合查询
	 * @param dataDto
	 * @return
	 */
	IPage<DataBzjDeviceEntity> selectPage(DeviceAndSaleQueryDTO dataDto);


}

