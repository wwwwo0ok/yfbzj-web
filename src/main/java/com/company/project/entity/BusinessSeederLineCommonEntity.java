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
 * 播种机行业通用行数据表（所有型号共有的行级基础数据）
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-03-24 14:05:37
 */
@Data
@TableName("business_seeder_line_common")
public class BusinessSeederLineCommonEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId("id")
	private String id;

	/**
	 * 关联主表ID（business_seeder_common.id）
	 */
		@TableField("message_id")
		private String messageId;

	/**
	 * 设备ID
	 */
		@TableField("lot_id")
		private String lotId;

	/**
	 * 行号（从1开始）
	 */
		@TableField("line_no")
		private Integer lineNo;

	/**
	 * 每行种数（最大16位二进制）
	 */
		@TableField("seed_num")
		private Integer seedNum;

	/**
	 * 种子监控使能（0:关闭,1:开启）
	 */
		@TableField("seed_swtich")
		private Integer seedSwtich;

	/**
	 * 主肥电机使能（0:关闭,1:开启）
	 */
		@TableField("main_fertilizer_swtich")
		private Integer mainFertilizerSwtich;

	/**
	 * 口肥电机使能（0:关闭,1:开启）
	 */
		@TableField("deputy_fertilizer_swtich")
		private Integer deputyFertilizerSwtich;

	/**
	 * 主肥监控使能（0:关闭,1:开启）
	 */
		@TableField("main_fertilizer_monitor_swtich")
		private Integer mainFertilizerMonitorSwtich;

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


}
