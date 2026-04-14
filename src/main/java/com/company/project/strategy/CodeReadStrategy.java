package com.company.project.strategy;

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
	 * 解码
	 * @param entity 解码的携带对象
	 */
	public void readCode(DataElectricSeederMessageEntity entity);

}
