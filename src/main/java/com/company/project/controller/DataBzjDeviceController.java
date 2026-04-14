package com.company.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.company.project.aliyun.RedisDeviceManager;
import com.company.project.common.utils.DataResult;
import com.company.project.dto.DeviceAndSaleQueryDTO;
import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.entity.DataElectricSeederMessageEntity;
import com.company.project.entity.DataSaleEntity;
import com.company.project.entity.SysUser;
import com.company.project.service.DataBzjDeviceService;
import com.company.project.service.DataElectricSeederMessageService;
import com.company.project.service.DataSaleService;
import com.company.project.service.SysFarmService;
import com.company.project.service.UserService;
import com.company.project.service.impl.DataBzjDeviceServiceImpl;
import com.company.project.util.DateUtil;
import com.google.common.collect.Lists;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * 播种机设备表（来自物联网）
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-11-29 13:21:40
 */
@Controller
@RequestMapping("/")
public class DataBzjDeviceController {


    @Autowired
    private DataBzjDeviceService dataBzjDeviceService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private DataSaleService saleService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RedisDeviceManager redisDeviceManager;
	@Autowired
	private RedissonClient redissonClient;
	
	@Autowired
	private DataElectricSeederMessageService messageService;
    

    
    /**
    * 跳转到管理员页面
    */
    @GetMapping("/index/dataBzjDevice")
    public String dataBzjDevice() {
        return "databzjdevice/list";
    }
    /**
     * 跳转到售后服务界面
     */
    @GetMapping("/index/dataBzjDeviceForService")
    public String dataBzjDeviceForService() {
    	return "databzjdevice/service";
    }
    /**
     * 跳转到买家界面
     */
    @GetMapping("/index/dataBzjDeviceForBuyer")
    public String dataBzjDeviceForBuyer() {
    	return "databzjdevice/buyer";
    }
    /**
     * 跳转到卖家界面
     */
    @GetMapping("/index/dataBzjDeviceForSeller")
    public String dataBzjDeviceForSeller() {
    	return "databzjdevice/seller";
    }


    
    
    
    @ApiOperation(value = "查询分页数据")
    @PostMapping("dataBzjDevice/listByPage")
    @SaCheckPermission("dataBzjDevice:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody DeviceAndSaleQueryDTO dataDto
    		){
    	
        IPage<DataBzjDeviceEntity> iPage = dataBzjDeviceService.selectPage(dataDto);
        
        return DataResult.success(iPage);
    }
    
    @ApiOperation(value = "买家查询分页数据")
    @PostMapping("dataBzjDevice/listByPageForUser")
    @ResponseBody
    public DataResult listByPageForUser(@RequestBody DeviceAndSaleQueryDTO dataDto
    		){
    	
        Object loginId = StpUtil.getLoginId();
        if(loginId!=null) {
            
            SysUser byId = userService.getById(loginId.toString());
            
            dataDto.setPhone(byId.getPhone());
            
        }
    	
    	IPage<DataBzjDeviceEntity> iPage = dataBzjDeviceService.selectPage(dataDto);
    	
    	return DataResult.success(iPage);
    }
    
    @ApiOperation(value = "更新数据(根据产品)")
    @PostMapping("dataBzjDevice/refreshData")
    @ResponseBody
    public DataResult refreshData(@RequestBody DataBzjDeviceEntity dataBzjDevice){
    	LambdaQueryWrapper<DataElectricSeederMessageEntity> queryWrapper = Wrappers.lambdaQuery();
    	//空不查询
    	if(StringUtils.isBlank(dataBzjDevice.getLotId())) {
    		return DataResult.success();
    	}
    	
    	// 定义锁的key，可以根据业务需求调整
        String updateAllKey = "sync:bzj:message:add:lock";
        String lockKey = "sync:bzj:message:add:one:"+dataBzjDevice.getLotId()+":lock";
        // 锁等待时间(毫秒)，防止线程长时间等待
        
        
    	RLock updateAllLock = redissonClient.getLock(updateAllKey);
    	if(updateAllLock.isLocked()) {
    		return DataResult.fail("后台已经在同步中，请稍后查看");
    	}
        
        RLock lock = redissonClient.getLock(lockKey);
        
        // 尝试获取锁，最多等待waitTime毫秒
        boolean isLocked;
		try {
			isLocked = lock.tryLock();
			if (!isLocked) {
	            // 获取锁失败
	    		return DataResult.fail("后台已经在同步中，请稍后查看");
	        }
    	
			DataBzjDeviceEntity byId = dataBzjDeviceService.getById(dataBzjDevice.getLotId());
			
	    	//单数据同步
	    	messageService.addAndCheck(byId);
		} finally {
            // 确保锁被释放
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    	
    	return DataResult.success();
    }
    
    @ApiOperation(value = "重新加载批次数据")
    @PostMapping("dataBzjDevice/reRead")
    @ResponseBody
    public DataResult reRead(@RequestBody DataBzjDeviceEntity dataBzjDevice){
    	dataBzjDeviceService.reRead(dataBzjDevice.getProductKey());
    	return DataResult.success();
    }
    @ApiOperation(value = "重新加载该产品")
    @PostMapping("dataBzjDevice/reReadDevice")
    @ResponseBody
    public DataResult reReadDevice(@RequestBody DataBzjDeviceEntity dataBzjDevice){
    	boolean reRead = dataBzjDeviceService.reRead(dataBzjDevice);
    	return reRead?DataResult.success():DataResult.fail("此任务已经在进行中或者系统繁忙请稍后再试");
    }
    @ApiOperation(value = "主动更新全设备信息")
    @PostMapping("dataBzjDevice/updateDevice")
    @ResponseBody
    public DataResult updateDevice(@RequestBody DataBzjDeviceEntity dataBzjDevice){
    	boolean sync = dataBzjDeviceService.sync();
    	return sync?DataResult.success():DataResult.fail("此任务已经在进行中或者系统繁忙请稍后再试");
    }
    @ApiOperation(value = "主动更新单条信息")
    @PostMapping("dataBzjDevice/reReadMessage")
    @ResponseBody
    public DataResult reReadMessage(@RequestBody DataElectricSeederMessageEntity messageEntity		){
    	boolean reRead = dataBzjDeviceService.reRead(messageEntity);
    	return reRead?DataResult.success():DataResult.fail("此任务已经在进行中或者系统繁忙请稍后再试");
    }

    @ApiOperation(value = "主动更新设备信息")
    @PostMapping("dataBzjDevice/syncDevice")
    @ResponseBody
    public DataResult syncDevice(@RequestBody DataBzjDeviceEntity dataBzjDevice){
    	boolean sync = dataBzjDeviceService.sync(dataBzjDevice.getProductKey());
    	return sync?DataResult.success():DataResult.fail("有其他用户已经在同步中");
    }
    @ApiOperation(value = "批量修改")
    @PostMapping("dataBzjDevice/updateBatch")
    @ResponseBody
    public DataResult updateBatch(@RequestBody DataBzjDeviceEntity dataBzjDevice) {
        // 1. 构造 LambdaUpdateWrapper
        LambdaUpdateWrapper<DataBzjDeviceEntity> updateWrapper = new LambdaUpdateWrapper<>();
        
        // 2. 设置条件：where product_key = ?
        // 使用方法引用，避免硬编码字符串
        updateWrapper
        			.eq(DataBzjDeviceEntity::getProductKey, dataBzjDevice.getProductKey())
                    // 如果还有其他条件，比如 lot_id，继续加
                    // .eq(DataBzjDeviceEntity::getLotId, dataBzjDevice.getLotId())
                    
                    // 3. 设置要修改的字段：set device_type = ?
                    .set(DataBzjDeviceEntity::getDeviceType, dataBzjDevice.getDeviceType());
        
        // 4. 执行更新
        // 注意：这里第一个参数传 null，因为更新的字段已经在 wrapper 里指定了
        // 如果传 dataBzjDevice 对象，MyBatis-Plus 会把对象里所有非 null 字段都更新，可能覆盖掉你不想改的字段
        boolean success = dataBzjDeviceService.update(null, updateWrapper);
        
        if (success) {
            return DataResult.success("修改成功");
        } else {
            return DataResult.fail("修改失败，未找到对应数据");
        }
    }

	@ApiOperation(value = "查询地图数据(用作显示）")
	@PostMapping("dataBzjDevice/selectForMap")
	@ResponseBody
	public DataResult selectForMap(@RequestBody DataBzjDeviceEntity dataBzjDevice){
    	
		if(CollectionUtils.isEmpty(dataBzjDevice.getDeviceStatusList())||CollectionUtils.isEmpty(dataBzjDevice.getDeviceTypeList())) {
			return DataResult.success();
		}
		
    	LambdaQueryWrapper<DataBzjDeviceEntity> queryWrapper = Wrappers.lambdaQuery();
    	//查询条件示例
    	queryWrapper
    	.in(DataBzjDeviceEntity::getDeviceType,dataBzjDevice.getDeviceTypeList())
    	.in(DataBzjDeviceEntity::getDeviceStatus,dataBzjDevice.getDeviceStatusList())
    	.isNotNull(DataBzjDeviceEntity::getBaiduX)
    	.isNotNull(DataBzjDeviceEntity::getBaiduY)
    	.orderByDesc(DataBzjDeviceEntity::getLotId);
    	
    	
    	List<DataBzjDeviceEntity> list = dataBzjDeviceService.list(queryWrapper);
    	
        
        return DataResult.success(list);
    }
    

    @ApiOperation(value = "查询分页数据")
    @PostMapping("dataBzjDevice/pointMap")
    @SaCheckPermission("dataBzjDevice:list")
    @ResponseBody
    public DataResult pointMap(@RequestBody DataBzjDeviceEntity dataBzjDevice){
    	
        
        JSONObject json = dataBzjDeviceService.pointMap(dataBzjDevice);
        
        String[] lastDaysFromToday = DateUtil.getLastDaysFromToday(5);
        
        Map<String, Object> onlineMap = new TreeMap<>();
        json.put("onlineMap", onlineMap);
        
        for(String dayString : lastDaysFromToday) {
        	long onlineDeviceCount = redisDeviceManager.getOnlineDeviceCount(dayString);
        	onlineMap.put(dayString.substring(5), onlineDeviceCount);
        }
        
        
        return DataResult.success(json);
    }
    

    @ApiOperation(value = "新增")
    @PostMapping("dataBzjDevice/add")
    @SaCheckPermission("dataBzjDevice:add")
    @ResponseBody
    public DataResult add(@RequestBody DataBzjDeviceEntity dataBzjDevice){
            dataBzjDeviceService.save(dataBzjDevice);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("dataBzjDevice/delete")
    @SaCheckPermission("dataBzjDevice:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            dataBzjDeviceService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("dataBzjDevice/update")
    @SaCheckPermission("dataBzjDevice:update")
    @ResponseBody
    public DataResult update(@RequestBody DataBzjDeviceEntity dataBzjDevice){
            dataBzjDeviceService.updateById(dataBzjDevice);
        return DataResult.success();
    }

    
//    // 每 1 秒推送一次数据
//    @Scheduled(fixedRate = 1000)
//    public void sendRealTimeData() {
//        Map<String, Object> data = new HashMap<>();
//        data.put("time", System.currentTimeMillis());
//        data.put("value", Math.random() * 100);
//        
//        messagingTemplate.convertAndSend("/topic/realtime", data);
//    }


}
