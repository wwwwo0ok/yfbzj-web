package com.company.project.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 报警表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-02-24 16:33:15
 */
@Data
@TableName("data_alarm")
public class DataAlarmEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 种报警
	 */
	public static int SEED_ALARM_TYPE = 0;
	/**
	 * 主肥监控报警
	 */
	public static int FERT_MONITORING_TYPE_ = 1;
	/**
	 * 主肥电机报警
	 */
	public static int MAIN_FERT_ALARM_TYPE = 2;
	/**
	 * 口肥电机报警
	 */
	public static int DEPUTY_FERT_ALARM_TYPE = 3;
	/**
	 * 肥报警（电驱）
	 */
	public static int FERT_ALARM_TYPE = 4;
	
	/**
	 * 
	 */
	@TableId("id")
	private String id;

	/**
	 * 报警类型（0种报警、1肥报警）
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
