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
 * 播种机自带通用报警表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-03-24 14:05:37
 */
@Data
@TableName("business_alarm_common")
public class BusinessAlarmCommonEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId("id")
	private String id;

	/**
	 * 报警类型
	 */
		@TableField("alarm_type")
		private Integer alarmType;

	/**
	 * 
	 */
		@TableField("lot_id")
		private String lotId;

	/**
	 * 
	 */
		@TableField("message_id")
		private String messageId;

	/**
	 * 行号
	 */
		@TableField("line_no")
		private Integer lineNo;

	/**
	 * 报警编码
	 */
		@TableField("code")
		private String code;

	/**
	 * 
	 */
		@TableField("create_time")
		private Date createTime;


}
