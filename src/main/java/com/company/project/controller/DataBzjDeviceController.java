package com.company.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.dev33.satoken.annotation.SaCheckPermission;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import com.company.project.common.utils.DataResult;

import com.company.project.entity.DataBzjDeviceEntity;
import com.company.project.service.DataBzjDeviceService;



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
    	
    	
        LambdaQueryWrapper<DataBzjDeviceEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(StringUtils.isNotBlank(dataBzjDevice.getLotId()), DataBzjDeviceEntity::getLotId, dataBzjDevice.getLotId())
        .eq(StringUtils.isNotBlank(dataBzjDevice.getDeviceType()),DataBzjDeviceEntity::getDeviceType,dataBzjDevice.getDeviceType())
        .eq(StringUtils.isNotBlank(dataBzjDevice.getDeviceStatus()),DataBzjDeviceEntity::getDeviceStatus,dataBzjDevice.getDeviceStatus())
        .orderByDesc(DataBzjDeviceEntity::getLotId);
        IPage<DataBzjDeviceEntity> iPage = dataBzjDeviceService.page(dataBzjDevice.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
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



}
