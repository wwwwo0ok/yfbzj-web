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
 * 农场主/合作社
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-01-15 18:37:08
 */
@Data
@TableName("sys_farm")
public class SysFarmEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId("id")
	private String id;

	/**
	 * 手机号
	 */
		@TableField("phone")
		private String phone;

	/**
	 * 省
	 */
		@TableField("province_id")
		private String provinceId;

	/**
	 * 市
	 */
		@TableField("city_id")
		private String cityId;

	/**
	 * 区
	 */
		@TableField("country_id")
		private String countryId;

	/**
	 * 农场名称
	 */
		@TableField("name")
		private String name;

	/**
	 * 户主名称
	 */
		@TableField("ownerName")
		private String ownerName;

	/**
	 * 是否删除(1未删除；0已删除)
	 */
		@TableField(value = "deleted", fill = FieldFill.INSERT)
		private Integer deleted;

	/**
	 * 创建时间
	 */
		@TableField("create_time")
		private Date createTime;

	/**
	 * 
	 */
		@TableField("update_time")
		private Date updateTime;

		
		 // 新增的非数据库字段（用于存储省市区名称）
	    @TableField(exist = false)
	    private String provinceName;

	    @TableField(exist = false)
	    private String cityName;

	    @TableField(exist = false)
	    private String countryName;

}
