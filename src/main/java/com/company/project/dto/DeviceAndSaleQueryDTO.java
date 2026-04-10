package com.company.project.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.company.project.entity.BaseEntity;
import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.entity.DataSaleEntity;

import lombok.Data;

// 创建 DTO 类
@Data
public class DeviceAndSaleQueryDTO extends BaseEntity{
    
	/**
	 * 买家
	 */
		private String buyer;

	/**
	 * 联系电话
	 */
		private String phone;
	/**
	 * 省
	 */
		private String provinceId;

	/**
	 * 市
	 */
		private String cityId;

	/**
	 * 区
	 */
		private String countryId;
		

	/**
	 * 设备名称
	 */
		private String deviceName;
		
		private String deviceStatus;
		
		private String deviceType;

	
	
}