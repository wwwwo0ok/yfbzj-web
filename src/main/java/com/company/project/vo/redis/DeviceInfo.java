package com.company.project.vo.redis;

import java.io.Serializable;

import lombok.Data;

/**
 *  设备信息
 */
@Data
public class DeviceInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1509595099803219777L;
	
	
	private String lotId;
	
	private int lines;
	
	private int seedCount;
	
	private int alarms;
	
	

}
