package com.company.project.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 电驱播种机消息记录表 数据来自于播种机
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-12-12 18:24:41
 */
@Data
@TableName("data_electric_seeder_message")
public class DataElectricSeederMessageEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId("id")
	private String id;

	/**
	 * 设备id
	 */
	@TableField(exist = false)
	private List<DataElectricSeederMessageLineEntity> lines;
	/**
	 * 设备id
	 */
	@TableField(exist = false)
	private List<DataAlarmEntity> alarms;
	
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
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
		private LocalDateTime dataTime;
		
	/**
	 * 电子齿轮比
	 */
		@TableField("electronic_gear_ratio")
		private int electronicGearRatio;

	/**
	 * B组孔盘数。
	 */
		@TableField("b_holes_num")
		private int bHolesNum;

	/**
	 * B组株距
	 */
		@TableField("b_sow_interval")
		private int bSowInterval;

	/**
	 * B组电机与主轴的减速比,单位0.1
	 */
		@TableField("b_reduction_ratio")
		private int bReductionRatio;

	/**
	 * B组电机一圈需要的脉冲数
	 */
		@TableField("b_send_pulse")
		private int bSendPulse;

	/**
	 * B组电机电机一圈需要的脉冲数
	 */
		@TableField("b_recieve_pulse")
		private int bRecievePulse;

	/**
	 * A组孔盘数。
	 */
		@TableField("a_holes_num")
		private int aHolesNum;

	/**
	 * A组株距
	 */
		@TableField("a_sow_interval")
		private int aSowInterval;

	/**
	 * A组电机与主轴的减速比,单位0.1
	 */
		@TableField("a_reduction_ratio")
		private int aReductionRatio;

	/**
	 * A组电机一圈需要的脉冲数
	 */
		@TableField("a_send_pulse")
		private int aSendPulse;

	/**
	 * A组电机电机一圈需要的脉冲数，单位个
	 */
		@TableField("a_recieve_pulse")
		private int aRecievePulse;

	/**
	 * 新头重复
	 */
		@TableField("new_head_repeat_flag")
		private int newHeadRepeatFlag;

	/**
	 * 限制距离到强制停机
	 */
		@TableField("shut_down_distance_flag")
		private int shutDownDistanceFlag;

	/**
	 * 种电机报警
	 */
		@TableField("seed_motor_alarm_flag")
		private int seedMotorAlarmFlag;

	/**
	 * 编码器取反
	 */
		@TableField("encoder_mode_flag")
		private int encoderModeFlag;

	/**
	 * 行程开关方向取反控制
	 */
		@TableField("driving_direction_flag")
		private int drivingDirectionFlag;

	/**
	 * 有无编码器
	 */
		@TableField("has_encoder_flag")
		private int hasEncoderFlag;

	/**
	 * 测试主肥量
	 */
		@TableField("real_main_fertilizer_value")
		private int realMainFertilizerValue;

	/**
	 * 设定主肥量
	 */
		@TableField("set_main_fertilizer_value")
		private int setMainFertilizerValue;

	/**
	 * 主肥系数
	 */
		@TableField("main_fertilizer_rate")
		private int mainFertilizerRate;

	/**
	 * 测试口肥量
	 */
		@TableField("real_deputy_fertilizer_value")
		private int realDeputyFertilizerValue;

	/**
	 * 设定口肥量
	 */
		@TableField("set_deputy_fertilizer_value")
		private int setDeputyFertilizerValue;

	/**
	 * 口肥系数
	 */
		@TableField("deputy_fertilizer_rate")
		private int deputyFertilizerRate;

	/**
	 * 主肥监控的缺肥灵敏度
	 */
		@TableField("lack_fertilizer_rate")
		private int lackFertilizerRate;

	/**
	 * 主肥监控标志
	 */
		@TableField("main_fertilizer_monitoring_flag")
		private int mainFertilizerMonitoringFlag;

	/**
	 * 主肥电机标志
	 */
		@TableField("main_fertilizer_motor_flag")
		private int mainFertilizerMotorFlag;

	/**
	 * 口肥电机标志
	 */
		@TableField("deputy_fertilizer_motor_flag")
		private int deputyFertilizerMotorFlag;

	/**
	 * 种物位标志
	 */
		@TableField("seed_location_flag")
		private int seedLocationFlag;

	/**
	 * 主肥物位标志
	 */
		@TableField("main_fertilizer_location_flag")
		private int mainFertilizerLocationFlag;

	/**
	 * 口肥物位标志
	 */
		@TableField("deputy_fertilizer_location_flag")
		private int deputyFertilizerLocationFlag;

	/**
	 * 报声音
	 */
		@TableField("sound_flag")
		private int soundFlag;

	/**
	 * 平行播种
	 */
		@TableField("sow_type")
		private int sowType;

	/**
	 * 播种苗带选择；=0是单苗带，=1是双苗带，=2是三苗带；
	 */
		@TableField("sow_mode")
		private int sowMode;

	/**
	 * 播种行数
	 */
		@TableField("sow_line")
		private int sowLine;

	/**
	 * 总播种宽度
	 */
		@TableField("sowing_width")
		private int sowingWidth;

	/**
	 * 种预判时间
	 */
		@TableField("predicted_time")
		private int predictedTime;

	/**
	 * 急停阈值
	 */
		@TableField("deceleration_value")
		private int decelerationValue;

	/**
	 * 无电机主肥监控的缺肥底线
	 */
		@TableField("lack_fertilizer_line")
		private int lackFertilizerLine;

	/**
	 * 单次播种运行时记录的运行时间
	 */
		@TableField("running_time")
		private int runningTime;

	/**
	 * 单次播种的距离显示
	 */
		@TableField("sow_distance")
		private int sowDistance;

	/**
	 * 雷达频率
	 */
		@TableField("radar_frequency")
		private int radarFrequency;

	/**
	 * 运行8秒时系统电压
	 */
		@TableField("system_voltage")
		private int systemVoltage;

	/**
	 * 有新头标志
	 */
		@TableField("system_new_head_flag")
		private int systemNewHeadFlag;

	/**
	 * 压行程（运输中）标志
	 */
		@TableField("system_compression_stroke_flag")
		private int systemCompressionStrokeFlag;

	/**
	 * 种手动中标志
	 */
		@TableField("system_seed_manually_flag")
		private int systemSeedManuallyFlag;

	/**
	 * 主肥测试手动中标志
	 */
		@TableField("system_main_fertilizer_testing_flag")
		private int systemMainFertilizerTestingFlag;

	/**
	 * 有编码器时编码器报警标志
	 */
		@TableField("encoder_alarm_flag")
		private int encoderAlarmFlag;

	/**
	 * 播种中标志
	 */
		@TableField("sowing_flag")
		private int sowingFlag;

	/**
	 * 屏连接标志
	 */
		@TableField("pad_connect_flag")
		private int padConnectFlag;

	/**
	 * 雷达报警标志
	 */
		@TableField("system_radar_warn_flag")
		private int systemRadarWarnFlag;

	/**
	 * 单次播种，种子报警次数
	 */
		@TableField("seed_alarm_count")
		private int seedAlarmCount;

	/**
	 * 单次播种肥报警次数
	 */
		@TableField("fertilizer_alarm_count")
		private int fertilizerAlarmCount;

	/**
	 * 运行8秒时风机压力
	 */
		@TableField("fan_pressure")
		private int fanPressure;

	/**
	 * 运行8秒时风机转速
	 */
		@TableField("fan_speed")
		private int fanSpeed;

	/**
	 * 物联上传数据类型
	 */
		@TableField("upload_type")
		private Integer uploadType;

	/**
	 * 当前到限里程停止时的剩余路程，4095是无限里程，单位是512米
	 */
		@TableField("remaining_mileage")
		private int remainingMileage;

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
	 * 数据状态(1.正常 0.忽略 )
	 */
		@TableField("status")
		private int status;

	/**
	 * 阿里云原数据
	 */
		@TableField("aliyun")
		@JsonIgnore
		private String aliyun;
		

		@TableField("seed_flag")
	   private int seedFlag;

		/**
		 * 额外数据
		 */
		@TableField("data1")
		private String data1;
		/**
		 * 额外数据
		 */
		@TableField("data2")
		private String data2;
		/**
		 * 额外数据
		 */
		@TableField("data3")
		private String data3;
		/**
		 * 额外数据
		 */
		@TableField("data4")
		private String data4;
		/**
		 * 额外数据
		 */
		@TableField("data5")
		private String data5;
		/**
		 * 额外数据
		 */
		@TableField("data6")
		private String data6;
		/**
		 * 额外数据
		 */
		@TableField("data7")
		private String data7;
		/**
		 * 额外数据
		 */
		@TableField("data8")
		private String data8;
		/**
		 * 作物
		 */
		@TableField("data9")
		private String data9;
		
		/**
		 * 
		 *   计算结果区
		 * 
		 * 
		 */
	/**
	 * 种子数量
	 */
	@TableField("seed_count")
	private int seedCount;
	/**
	 * 每公顷种子数
	 */
	@TableField("seed_count_hectare")
	private int seedCountHectare;
	/**
	 * 作业面积（亩）
	 */
	@TableField("worked_area")
	private String workedArea;
	/**
	 * 株距
	 */
	@TableField("sow_interval")
	private String sowInterval;
	/**
	 * 株距
	 */
	@TableField(exist = false)
	private String dataTimeString;
		
		

}
