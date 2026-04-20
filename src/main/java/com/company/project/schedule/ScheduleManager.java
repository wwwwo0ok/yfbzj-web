package com.company.project.schedule;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.company.project.service.DataElectricSeederMessageService;
import com.company.project.service.DataMessageDayService;
import com.company.project.util.DateUtil;

/**
 *  定时器中心
 */
@Component
public class ScheduleManager {
	
	@Autowired
	private DataElectricSeederMessageService messageService;
	
	@Autowired
	private DataMessageDayService messageDayService;

	@Autowired
	private RedissonClient redissonClient;
	/**
	 *  每日凌晨
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void everyMorning() {
		
		// 定义锁的key，可以根据业务需求调整
    	String lockKey = "sync:sys:scheduled:everyMorning";
    	
    	RLock lock = redissonClient.getLock(lockKey);
    	
    	// 尝试获取锁，最多等待waitTime毫秒
    	boolean isLocked;
    	try {
    		isLocked = lock.tryLock();
    		if (!isLocked) {
    			// 获取锁失败
    			return;
    		}
    		//全设备同步昨日消息
    		messageService.syncMessages(DateUtil.getYesterdayString());
    		//统计日消息,需要再统计消息之后
    		messageDayService.addDayData(DateUtil.getYesterdayString());
	    		
		} finally {
			// 确保锁被释放
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
		
		
	
	}
	
	
	@Scheduled(cron = "0 0/10 * * * ?")
	public void everyTenMinute() {
		// 定义锁的key，可以根据业务需求调整
    	String lockKey = "sync:sys:scheduled:everyTenMinute";
    	
    	RLock lock = redissonClient.getLock(lockKey);
    	
    	// 尝试获取锁，最多等待waitTime毫秒
    	boolean isLocked;
    	try {
    		isLocked = lock.tryLock();
    		if (!isLocked) {
    			// 获取锁失败
    			return;
    		}
    		//更新统计信息
    		messageDayService.addDayData(DateUtil.getTodayString());
	    		
		} finally {
			// 确保锁被释放
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
		
	}

}
