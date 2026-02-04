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
 * 地区表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-01-15 18:37:07
 */
@Data
@TableName("sys_area")
public class SysAreaEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId("id")
	private String id;

	/**
	 * 父id，根目录为0
	 */
		@TableField("parent_id")
		private String parentId;

	/**
	 * 名字
	 */
		@TableField("name")
		private String name;


}
