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
 * 电驱播种机行数据表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-12-12 18:24:42
 */
@Data
@TableName("data_electric_seeder_message_line")
public class DataElectricSeederMessageLineEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId("id")
	private String id;

	/**
	 * 
	 */
		@TableField("message_id")
		private String messageId;

	/**
	 * 行数
	 */
		@TableField("line_no")
		private Integer lineNo;

	/**
	 * 种数（最大16位二进制）
	 */
		@TableField("seed_num_")
		private Integer seedNum;

	/**
	 * 多种百分比（最大8位二进制）
	 */
		@TableField("excess_seeds")
		private Integer excessSeeds;

	/**
	 * 缺种百分比（最大8位二进制）
	 */
		@TableField("lack_seeds")
		private Integer lackSeeds;

	/**
	 * 主肥检测转速反馈比（最大8位二进制）
	 */
		@TableField("fertilizer_speed_ratio")
		private Integer fertilizerSpeedRatio;

	/**
	 * 种报警（最大3位二进制）
	 */
		@TableField("seed_alarm_ratio")
		private Integer seedAlarmRatio;

	/**
	 * AB模式
	 */
		@TableField("seed_ab_type")
		private Integer seedAbType;

	/**
	 * 主肥电机方向
	 */
		@TableField("main_fertilizer_direct")
		private Integer mainFertilizerDirect;

	/**
	 * 口肥电机方向
	 */
		@TableField("deputy_fertilizer_direct")
		private Integer deputyFertilizerDirect;

	/**
	 * 种使能
	 */
		@TableField("seed_swtich")
		private Integer seedSwtich;

	/**
	 * 主肥电机使能
	 */
		@TableField("main_fertilizer_swtich")
		private Integer mainFertilizerSwtich;

	/**
	 * 口肥电机使能
	 */
		@TableField("deputy_fertilizer_swtich")
		private Integer deputyFertilizerSwtich;

	/**
	 * 主肥监控使能
	 */
		@TableField("main_fertilizer_monitor_swtich")
		private Integer mainFertilizerMonitorSwtich;

	/**
	 * 肥报警
	 */
		@TableField("fertilizer_alarm_ratio")
		private Integer fertilizerAlarmRatio;


}
