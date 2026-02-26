package com.company.project.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.project.entity.DataProductEntity;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;

/**
 * 批次表（对应阿里云的产品表）
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-02-23 13:22:43
 */
public interface DataProductService extends IService<DataProductEntity> {

	void updateProduct();

	List<DataProductEntity> getListByLevel(int level);
	
	/**
	 * 获取设备级和消息级别
	 * @return
	 */
	default public List<DataProductEntity> getDeviceLevel(){
		return getListByLevel(DataProductEntity.LEVEL_MESSAGE);
	}

	
	/**
	 * 当前编码是否激活
	 * @param code
	 * @return
	 */
	boolean isActive(String code);

}

