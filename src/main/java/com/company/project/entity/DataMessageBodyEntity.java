package com.company.project.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.company.project.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.FieldFill;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 原数据表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-03-24 16:02:32
 */
@Data
@TableName("data_message_body")
public class DataMessageBodyEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId("id")
	private String id;

	/**
	 * 设备id
	 */
		@TableField("lot_id")
		private String lotId;

	/**
	 * 消息code
	 */
		@TableField("message_code")
		private String messageCode;

	/**
	 * 消息主体
	 */
		@TableField("message_body")
		private String messageBody;

	/**
	 * 数据发生时刻（硬件时刻）
	 */
		@TableField("data_time")
		private Date dataTime;

	/**
	 * 消息状态（0正常，1作废）
	 */
		@TableField("invalidate_flag")
		private Integer invalidateFlag;

	/**
	 * 作废原因
	 */
		@TableField("invalidate_reason")
		private String invalidateReason;

	/**
	 * 创建时间
	 */
		@TableField("create_time")
		private Date createTime;


}
