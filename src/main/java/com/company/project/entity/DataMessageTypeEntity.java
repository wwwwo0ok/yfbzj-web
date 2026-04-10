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
 * 数据的消息类型
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-03-24 14:05:37
 */
@Data
@TableName("data_message_type")
public class DataMessageTypeEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId("id")
	private String id;

	/**
	 * 产品id
	 */
		@TableField("product_key")
		private String productKey;

	/**
	 * 消息编码
	 */
		@TableField("code")
		private String code;

	/**
	 * 消息名称
	 */
		@TableField("name")
		private String name;

	/**
	 * 描述
	 */
		@TableField("description")
		private String description;

	/**
	 * 消息状态（1开启、0关闭）
	 */
		@TableField("message_status")
		private Integer messageStatus;

	/**
	 * 创建时间
	 */
		@TableField("create_time")
		private Date createTime;

	/**
	 * 上次同步时间
	 */
		@TableField("last_update_time")
		private Date lastUpdateTime;


}
