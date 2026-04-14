package com.company.project.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.company.project.service.DataElectricSeederMessageService;
import com.company.project.util.DateUtil;

/**
 *  定时器中心
 */
public class ScheduleManager {
	
	@Autowired
	private DataElectricSeederMessageService messageService;
	
	/**
	 *  每日凌晨
	 */
	@Scheduled(cron = "0 17 0 * * ?")
	public void everyMorning() {
		//全设备同步昨日消息
		messageService.syncMessages(DateUtil.getYesterdayString());
	}

}
