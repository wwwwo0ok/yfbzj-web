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
 * 批次表（对应阿里云的产品表）
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-02-23 13:22:43
 */
@Data
@TableName("data_product")
public class DataProductEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public static int LEVEL_NO_UPDATE = 0;
	public static int LEVEL_DEVICE = 1;
	public static int LEVEL_MESSAGE = 1;
	
	/**
	 * 
	 */
	@TableId("id")
	private String id;

	/**
	 * 批次编码
	 */
		@TableField("code")
		private String code;

	/**
	 * 批次名称
	 */
		@TableField("name")
		private String name;

	/**
	 * 描述
	 */
		@TableField("description")
		private String description;
		/**
		 * 设备数
		 */
		@TableField("device_num")
		private int deviceNum;
		
		
		@TableField("create_time")
		private Date createTime;

	/**
	 * 维护级别（0、不维护；1、设备级别；2、消息级别）
	 */
		@TableField("level")
		private Integer level;

		
		public String getRawdata() {
			
			return "/" + code + "/rawdata/get" ;
		}

}
