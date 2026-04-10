package com.company.project.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.company.project.entity.BaseEntity;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldFill;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import lombok.Data;

/**
 * 数据转换类（输入为元消息、输出为json）
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-03-24 14:05:37
 */
@Data
@TableName(value = "parse_regular", autoResultMap = true)
public class ParseRegularEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId("id")
	private String id;

	/**
	 * 消息类型id
	 */
		@TableField("message_type_id")
		private String messageTypeId;

	/**
	 * 解析器名称
	 */
		@TableField("parse_name")
		private String parseName;

	/**
	 * 消息id
	 */
		@TableField("message_id")
		private String messageId;

	/**
	 * 解析规则体
	 */
		@TableField(value="regular_body" , typeHandler = FastjsonTypeHandler.class)
		private JSONObject regularBody; 

	/**
	 * 转换状态（0、保存、1提交审批、2审批通过、3审批驳回）
	 */
		@TableField("parse_status")
		private Integer parseStatus;

	/**
	 * 解析器编码
	 */
		@TableField("code")
		private String code;

	/**
	 * 
	 */
		@TableField("create_time")
		private Date createTime;

	/**
	 * 修改时间
	 */
		@TableField("update_time")
		private Date updateTime;


}
