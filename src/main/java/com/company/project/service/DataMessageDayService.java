package com.company.project.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.project.entity.DataMessageDayEntity;

/**
 * 每日播种统计表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-04-16 14:40:26
 */
public interface DataMessageDayService extends IService<DataMessageDayEntity> {

	/**
	 * 同步昨天数据
	 * @param yesterdayString
	 */
	void addDayData(String yesterdayString);

	/**
	 * 统计信息，可以传入年，月，日。进行分别统计。
	 * @param queryEntity
	 * @return
	 */
	List<DataMessageDayEntity> queryDayData(DataMessageDayEntity queryEntity);

}

