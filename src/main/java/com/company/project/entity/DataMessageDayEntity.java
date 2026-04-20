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
 * 每日播种统计表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-04-16 14:40:26
 */
@Data
@TableName("data_message_day")
public class DataMessageDayEntity extends BaseEntity implements Serializable {
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
	 * 设备名称
	 */
		@TableField("device_name")
		private String deviceName;

	/**
	 * 月份
	 */
		@TableField("month_string")
		private String monthString;

	/**
	 * 年
	 */
		@TableField("year_string")
		private String yearString;

	/**
	 * 日期
	 */
		@TableField("day_string")
		private String dayString;

	/**
	 * 种子数（计算得来）
	 */
		@TableField("seed_count")
		private Integer seedCount;

	/**
	 * 每公顷种子数（计算得来）
	 */
		@TableField("seed_count_hectare")
		private Integer seedCountHectare;

	/**
	 * 亩数（计算得来）
	 */
		@TableField("worked_area")
		private Double workedArea;

		/**
		 * 垄数
		 */
			@TableField("sow_line")
			private int sowLine;

	/**
	 * 播种距离
	 */
		@TableField("sow_distance")
		private Integer sowDistance;

	/**
	 * 播种次数
	 */
		@TableField("sow_count")
		private Integer sowCount;

	/**
	 * 是否正常统计（0，是，1不是）
	 */
		@TableField("data_status")
		private Integer dataStatus;

	/**
	 * 创建时间
	 */
		@TableField("create_time")
		private Date createTime;

	/**
	 * 更新时间
	 */
		@TableField("update_time")
		private Date updateTime;

	/**
	 * 状态
	 */
		@TableField("status")
		private Integer status;


}
