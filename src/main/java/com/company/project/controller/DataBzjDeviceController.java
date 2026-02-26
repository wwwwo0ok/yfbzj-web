package com.company.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.company.project.common.utils.DataResult;
import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.entity.SysFarmEntity;
import com.company.project.entity.SysUser;
import com.company.project.service.DataBzjDeviceService;
import com.company.project.service.DataElectricSeederMessageService;
import com.company.project.service.SysFarmService;
import com.company.project.service.UserService;

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
    private SysFarmService sysFarmService;

    @Resource
    private UserService userService;
    
    @Autowired
    private DataElectricSeederMessageService messageService;
    
    /**
    * 跳转到页面
    */
    @GetMapping("/index/dataBzjDevice")
    public String dataBzjDevice() {
        return "databzjdevice/list";
    }
    /**
     * 跳转到监控器页面
     */
    @GetMapping("/index/dataBzjDevice1")
    public String dataBzjDevice1() {
    	return "databzjdevice/list1";
    }
    /**
     * 跳转到播种机页面
     */
    @GetMapping("/index/dataBzjDevice2")
    public String dataBzjDevice2() {
    	return "databzjdevice/list2";
    }


    
    
    
    @ApiOperation(value = "查询分页数据")
    @PostMapping("dataBzjDevice/listByPage")
    @SaCheckPermission("dataBzjDevice:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody DataBzjDeviceEntity dataBzjDevice){
    	
//    	DataAnalysisUtil.test();
    	/**
    	 * 
    	 */
    	
    	
        LambdaQueryWrapper<DataBzjDeviceEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper
        .eq(StringUtils.isNotBlank(dataBzjDevice.getLotId()), DataBzjDeviceEntity::getLotId, dataBzjDevice.getLotId())
        .eq(StringUtils.isNotBlank(dataBzjDevice.getDeviceName()),DataBzjDeviceEntity::getDeviceName,dataBzjDevice.getDeviceName())
        .eq(StringUtils.isNotBlank(dataBzjDevice.getDeviceType()),DataBzjDeviceEntity::getDeviceType,dataBzjDevice.getDeviceType())
        .eq(StringUtils.isNotBlank(dataBzjDevice.getDeviceStatus()),DataBzjDeviceEntity::getDeviceStatus,dataBzjDevice.getDeviceStatus())
        .orderByDesc(DataBzjDeviceEntity::getLotId);
        
        Object loginId = StpUtil.getLoginId();
    	if(loginId!=null) {
    		
    		SysUser byId = userService.getById(loginId.toString());
    		
    		
    		LambdaQueryWrapper<SysFarmEntity> lambdaQuery = Wrappers.lambdaQuery();
    		
    		lambdaQuery.eq(SysFarmEntity::getPhone, byId.getUsername());
    		
    		SysFarmEntity one = sysFarmService.getOne(lambdaQuery);
    		
    		if(one!=null) {
    			queryWrapper.eq
    			(StringUtils.isNotBlank(one.getId()),DataBzjDeviceEntity::getFarmId,one.getId());
    		}
    	}
        
        IPage<DataBzjDeviceEntity> iPage = dataBzjDeviceService.page(dataBzjDevice.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }
    
    
    @ApiOperation(value = "查询地图数据(用作显示）")
    @PostMapping("dataBzjDevice/selectForMap")
    @ResponseBody
    public DataResult selectForMap(@RequestBody DataBzjDeviceEntity dataBzjDevice){
    	
    	
    	LambdaQueryWrapper<DataBzjDeviceEntity> queryWrapper = Wrappers.lambdaQuery();
    	//查询条件示例
    	queryWrapper
    	.eq(StringUtils.isNotBlank(dataBzjDevice.getLotId()), DataBzjDeviceEntity::getLotId, dataBzjDevice.getLotId())
    	.eq(StringUtils.isNotBlank(dataBzjDevice.getDeviceName()),DataBzjDeviceEntity::getDeviceName,dataBzjDevice.getDeviceName())
    	.eq(StringUtils.isNotBlank(dataBzjDevice.getDeviceType()),DataBzjDeviceEntity::getDeviceType,dataBzjDevice.getDeviceType())
    	.eq(StringUtils.isNotBlank(dataBzjDevice.getDeviceStatus()),DataBzjDeviceEntity::getDeviceStatus,dataBzjDevice.getDeviceStatus())
    	.eq(StringUtils.isNotBlank(dataBzjDevice.getProductKey()),DataBzjDeviceEntity::getProductKey,dataBzjDevice.getProductKey())
    	.isNotNull(DataBzjDeviceEntity::getBaiduX)
    	.isNotNull(DataBzjDeviceEntity::getBaiduY)
    	.orderByDesc(DataBzjDeviceEntity::getLotId);
    	
    	Object loginId = StpUtil.getLoginId();
    	if(loginId!=null) {
    		
    		SysUser byId = userService.getById(loginId.toString());
    		
    		
    		LambdaQueryWrapper<SysFarmEntity> lambdaQuery = Wrappers.lambdaQuery();
    		
    		lambdaQuery.eq(SysFarmEntity::getPhone, byId.getUsername());
    		
    		SysFarmEntity one = sysFarmService.getOne(lambdaQuery);
    		
    		if(one!=null) {
    			queryWrapper.eq
    			(StringUtils.isNotBlank(one.getId()),DataBzjDeviceEntity::getFarmId,one.getId());
    		}
    	}
    	
    	List<DataBzjDeviceEntity> list = dataBzjDeviceService.list(queryWrapper);
    	
    	List<DataBzjDeviceEntity> collect = list.stream().filter(li -> li.getLatestX()!=null&&li.getLatestY()!=null&&li.getLatestX()!=0&&li.getLatestY()!=0).collect(Collectors.toList());
    	
    	
    	return DataResult.success(collect);
    }
    

    @ApiOperation(value = "查询分页数据")
    @PostMapping("dataBzjDevice/pointMap")
    @SaCheckPermission("dataBzjDevice:list")
    @ResponseBody
    public DataResult pointMap(){
    	
//    	DataAnalysisUtil.test();
    	
    	
        
        JSONObject json = dataBzjDeviceService.pointMap();
        
        
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
