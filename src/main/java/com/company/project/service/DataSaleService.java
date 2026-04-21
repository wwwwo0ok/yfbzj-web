package com.company.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.project.entity.DataSaleEntity;

/**
 * 销售信息表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-04-01 10:19:02
 */
public interface DataSaleService extends IService<DataSaleEntity> {

	/**
	 * 新增
	 * @param newSale
	 */
	void addNewSale(DataSaleEntity newSale);

	/**
	 * 根据屏幕编码获取用户信息
	 * @param screenCode
	 * @return
	 */
	DataSaleEntity getByScreenCode(String screenCode);

	DataSaleEntity getByPhone(String phone);

}

