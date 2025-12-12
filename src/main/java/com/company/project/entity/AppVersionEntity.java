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
 * APP版本更新
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-12-08 13:50:31
 */
@Data
@TableName("app_version")
public class AppVersionEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId("id")
	private Integer id;

	/**
	 * 版本号
	 */
		@TableField("version_code")
		private String versionCode;

	/**
	 * 版本说明
	 */
		@TableField("version_name")
		private String versionName;

	/**
	 * 下载地址
	 */
		@TableField("apk_url")
		private String apkUrl;

	/**
	 * 是否强制更新（0否，1是）
	 */
		@TableField("is_force_update")
		private Integer isForceUpdate;

	/**
	 * 上架时间
	 */
		@TableField("release_time")
		private Date releaseTime;

	/**
	 * 文件大小
	 */
		@TableField("file_size")
		private Float fileSize;


}
