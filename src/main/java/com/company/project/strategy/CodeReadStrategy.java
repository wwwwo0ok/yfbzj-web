package com.company.project.strategy;

import java.util.List;

import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.entity.DataElectricSeederMessageEntity;

/**
 *  阿里云数据处理接口
 */
public interface CodeReadStrategy {
	
	/**
	 * 获取策略实现代码
	 */
	public String getCode();
	
	
	/**
	 * 解析编码
	 */
	public DataElectricSeederMessageEntity readCode(String code);

}
