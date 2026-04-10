package com.company.project.entity;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

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
	public static int FERT_MONITORING_TYPE = 1;
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
	
	public static final String[] SEED_ALARM_TYPE_STRING = {"无故障","缺种报警","断流","堵塞","头坏","种头重复","无故障"};
	public static final String[] FERT_MONITORING_TYPE_STRING = {"无故障","主肥缺肥报警","断流报警","堵塞报警","肥头零点错误",
			"电容系统报警","本肥头重合","本肥头未连接"};
	public static final String[] MAIN_FERT_ALARM_TYPE_STRING = {
			"无故障","主肥电机报警","重合","主肥电机未连接报警"
			};
	public static final String[] DEPUTY_FERT_ALARM_TYPE_STRING = {
			"无故障","口肥电机报警","重合","口肥电机未连接报警"
			};
	public static final String[] FERT_ALARM_TYPE_STRING = {
			};
	
	
	
	/**
	 * 
	 */
	@TableId("id")
	private String id;

	/**
	 * 报警类型（0种报警、1肥报警）
	 */
		@TableField("alarm_type")
		private int alarmType;
		/**
		 * 报警状态
		 */
		@TableField("alarm_status")
		private Integer alarmStatus;

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
		private int code;
		/**
		 * 报警名称
		 */
		@TableField("alarm_name")
		private String alarmName;

	/**
	 * 
	 */
		@TableField("create_time")
		private Date createTime;
		
		public void setCode(int code) {
			
			String alarmNameString = null;
			
			if(alarmType == SEED_ALARM_TYPE)alarmNameString = createAlarmName(SEED_ALARM_TYPE_STRING,code);
			if(alarmType == FERT_MONITORING_TYPE)alarmNameString = createAlarmName(FERT_MONITORING_TYPE_STRING,code);
			if(alarmType == MAIN_FERT_ALARM_TYPE)alarmNameString = createAlarmName(MAIN_FERT_ALARM_TYPE_STRING,code);
			if(alarmType == DEPUTY_FERT_ALARM_TYPE)alarmNameString = createAlarmName(DEPUTY_FERT_ALARM_TYPE_STRING,code);
			if(alarmType == FERT_ALARM_TYPE)alarmNameString = createAlarmName(FERT_ALARM_TYPE_STRING,code);
			
			this.alarmName = alarmNameString ;
		}

		
		public String createAlarmName(String[] names,int code) {
			return code < names.length?names[code]:Integer.toString(code);
		}

}
