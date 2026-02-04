package com.company.project.service;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.sdk.service.iot20180120.models.QueryDeviceResponseBody.DeviceInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.company.project.entity.DataBzjDeviceEntity;

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
	 * @return
	 */
	JSONObject pointMap();

}

