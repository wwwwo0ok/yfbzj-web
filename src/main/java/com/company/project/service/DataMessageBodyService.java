package com.company.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.entity.DataMessageBodyEntity;

/**
 * 原数据表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-03-24 14:05:38
 */
public interface DataMessageBodyService extends IService<DataMessageBodyEntity> {

	/**
	 * 增量同步全部电驱消息内容（2025年12月开始）
	 */
	boolean sync();

	/**
	 * 增量保存
	 * @param device
	 * @return
	 */
	void insertNewData(DataBzjDeviceEntity device);

}

