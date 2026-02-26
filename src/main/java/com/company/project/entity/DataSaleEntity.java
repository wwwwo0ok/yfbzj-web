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
 * 销售信息表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-02-26 09:18:21
 */
@Data
@TableName("data_sale")
public class DataSaleEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId("id")
	private String id;

	/**
	 * 经销商
	 */
		@TableField("agent")
		private String agent;

	/**
	 * 买家
	 */
		@TableField("buyer")
		private String buyer;

	/**
	 * 联系电话
	 */
		@TableField("phone")
		private String phone;

	/**
	 * 机器名称
	 */
		@TableField("machine_name")
		private String machineName;

	/**
	 * 机器型号
	 */
		@TableField("machine_model")
		private String machineModel;

	/**
	 * 名牌编号
	 */
		@TableField("brand_number")
		private String brandNumber;

	/**
	 * 行数
	 */
		@TableField("lines")
		private Integer lines;

	/**
	 * 激光喷码
	 */
		@TableField("product_code")
		private String productCode;

	/**
	 * 创建时间
	 */
		@TableField("create_time")
		private Date createTime;


}
