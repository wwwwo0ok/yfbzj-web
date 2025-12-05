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
 * 播种机设备表（来自物联网）
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-12-05 11:05:41
 */
@Data
@TableName("data_bzj_device")
public class DataBzjDeviceEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 设备id
	 */
	@TableId("lot_id")
	private String lotId;

	/**
	 * 组织机构id
	 */
		@TableField("dept_id")
		private Long deptId;

	/**
	 * 用户名
	 */
		@TableField("username")
		private String username;

	/**
	 * 地址
	 */
		@TableField("address")
		private String address;

	/**
	 * 电话
	 */
		@TableField("telephone")
		private String telephone;

	/**
	 * 设备名称
	 */
		@TableField("device_name")
		private String deviceName;

	/**
	 * 备注名称
	 */
		@TableField("nickname")
		private String nickname;

	/**
	 * 产品 ProductKey
	 */
		@TableField("product_key")
		private String productKey;

	/**
	 * 设备信息最后一次更新时的时间
	 */
		@TableField("device_modified")
		private String deviceModified;

	/**
	 * 设备状态：ONLINE-设备在线，OFFLINE-设备离线，UNACTIVE-设备未激活，DISABLE-设备已禁用
	 */
		@TableField("device_status")
		private String deviceStatus;

	/**
	 * 设备密钥
	 */
		@TableField("device_secret")
		private String deviceSecret;

	/**
	 * 设备类型（0未知，1机械；2电驱）
	 */
		@TableField("device_type")
		private String deviceType;

	/**
	 * 最新定位X坐标
	 */
		@TableField("latest_x")
		private Double latestX;

	/**
	 * 最新定位Y坐标
	 */
		@TableField("latest_y")
		private Double latestY;

	/**
	 * 删除标志（0代表存在 2代表删除）
	 */
		@TableField("del_flag")
		private String delFlag;

	/**
	 * 创建者
	 */
		@TableField("create_by")
		private String createBy;

	/**
	 * 创建时间
	 */
		@TableField("create_time")
		private Date createTime;

	/**
	 * 更新者
	 */
		@TableField("update_by")
		private String updateBy;

	/**
	 * 更新时间
	 */
		@TableField("update_time")
		private Date updateTime;

	/**
	 * 备注
	 */
		@TableField("remark")
		private String remark;


}
