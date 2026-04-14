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
	boolean syncToday();

	
	Date selectMaxDataTimeByDevice(@Param("device") DataBzjDeviceEntity device);


	IPage<DataElectricSeederMessageEntity> getMessageList(DataElectricSeederMessageEntity queryEntity);
	/**
	 * 重新加载消息,根据设备
	 * @param li
	 */
	boolean reRead(DataBzjDeviceEntity li);

	boolean reRead(DataBzjDeviceEntity entity, String messageId);

	boolean reRead(List<DataBzjDeviceEntity> list);

	/**
	 * 检查对比某个设备的所有消息
	 * @param device 设备
	 * @param startTime 开始时间 HHHH-MM-DD HH:MM:SS
	 * @param endTime 结束时间 HHHH-MM-DD HH:MM:SS
	 * @return
	 */
	boolean addAndCheck(DataBzjDeviceEntity device,String startTime,String endTime);
	/**
	 * 检查对比某个设备的所有消息
	 * @param device 设备
	 * @param dayString 日期 HHHH-MM-DD
	 * @return
	 */
	boolean addAndCheck(DataBzjDeviceEntity device,String dayString);
	/**
	 * 检查对比某个设备的所有消息（今天）
	 * @param device 设备
	 * @return
	 */
	boolean addAndCheck(DataBzjDeviceEntity device);

	/**
	 * 重新保存单条消息及其关联数据。
	 * 此方法没有独立事务，会加入到 reRead 的事务中。
	 * @param li 原始消息实体
	 * @param paramEntit 参数实体
	 */
	void addNewMessage(DataElectricSeederMessageEntity entity, DataBzjDeviceEntity paramEntit);


	/**
	 * 同步设备消息
	 */
	boolean syncMessages(String dayString);



}

