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
 * 播种机行业公用字段表（所有型号共有的核心业务数据）
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-03-24 14:05:37
 */
@Data
@TableName("business_seeder_common")
public class BusinessSeederCommonEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId("id")
	private String id;

	/**
	 * 设备ID
	 */
		@TableField("lot_id")
		private String lotId;

	/**
	 * 设备名称
	 */
		@TableField("device_name")
		private String deviceName;

	/**
	 * 数据发生时刻（硬件时刻）
	 */
		@TableField("data_time")
		private Date dataTime;

	/**
	 * 物联上传数据类型（0:蓝牙上传,1:运行停止上传,2:物联控制后上传,3:设置改变上传）
	 */
		@TableField("upload_type")
		private Integer uploadType;

	/**
	 * 播种类型（0:平行播种）
	 */
		@TableField("sow_type")
		private Integer sowType;

	/**
	 * 播种行数
	 */
		@TableField("sow_line")
		private Integer sowLine;

	/**
	 * 主肥监控标志
	 */
		@TableField("main_fertilizer_monitoring_flag")
		private Integer mainFertilizerMonitoringFlag;

	/**
	 * 主肥电机标志
	 */
		@TableField("main_fertilizer_motor_flag")
		private Integer mainFertilizerMotorFlag;

	/**
	 * 口肥电机标志
	 */
		@TableField("deputy_fertilizer_motor_flag")
		private Integer deputyFertilizerMotorFlag;

	/**
	 * 种物位标志
	 */
		@TableField("seed_location_flag")
		private Integer seedLocationFlag;

	/**
	 * 主肥物位标志
	 */
		@TableField("main_fertilizer_location_flag")
		private Integer mainFertilizerLocationFlag;

	/**
	 * 口肥物位标志
	 */
		@TableField("deputy_fertilizer_location_flag")
		private Integer deputyFertilizerLocationFlag;

	/**
	 * 株距（单位mm）
	 */
		@TableField("sow_interval")
		private Integer sowInterval;

	/**
	 * 总播种宽度（单位mm）
	 */
		@TableField("sowing_width")
		private Integer sowingWidth;

	/**
	 * 设定主肥量（单位kg）
	 */
		@TableField("set_main_fertilizer_value")
		private Integer setMainFertilizerValue;

	/**
	 * 实测主肥量（单位g）
	 */
		@TableField("real_main_fertilizer_value")
		private Integer realMainFertilizerValue;

	/**
	 * 设定口肥量（单位kg）
	 */
		@TableField("set_deputy_fertilizer_value")
		private Integer setDeputyFertilizerValue;

	/**
	 * 实测口肥量（单位g）
	 */
		@TableField("real_deputy_fertilizer_value")
		private Integer realDeputyFertilizerValue;

	/**
	 * 主肥系数
	 */
		@TableField("main_fertilizer_rate")
		private Integer mainFertilizerRate;

	/**
	 * 口肥系数
	 */
		@TableField("deputy_fertilizer_rate")
		private Integer deputyFertilizerRate;

	/**
	 * 主肥监控缺肥灵敏度
	 */
		@TableField("lack_fertilizer_rate")
		private Integer lackFertilizerRate;

	/**
	 * 单次播种运行时间（单位秒）
	 */
		@TableField("running_time")
		private Integer runningTime;

	/**
	 * 单次播种距离（单位m）
	 */
		@TableField("sow_distance")
		private Integer sowDistance;

	/**
	 * 种子使能（是否开启播种）
	 */
		@TableField("seed_flag")
		private Integer seedFlag;

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
	 * 数据状态（1:正常,0:忽略）
	 */
		@TableField("status")
		private Integer status;


}
