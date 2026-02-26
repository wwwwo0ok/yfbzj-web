package com.company.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.dev33.satoken.annotation.SaCheckPermission;
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

import com.company.project.entity.DataAlarmEntity;
import com.company.project.service.DataAlarmService;



/**
 * 报警表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-02-24 16:33:15
 */
@Controller
@RequestMapping("/")
public class DataAlarmController {
    @Autowired
    private DataAlarmService dataAlarmService;

    /**
    * 跳转到页面
    */
    @GetMapping("/index/dataAlarm")
    public String dataAlarm() {
        return "dataalarm/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("dataAlarm/listByPage")
    @SaCheckPermission("dataAlarm:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody DataAlarmEntity dataAlarm){
        LambdaQueryWrapper<DataAlarmEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(dataAlarm.getId() != null, DataAlarmEntity::getId, dataAlarm.getId());
        queryWrapper.orderByDesc(DataAlarmEntity::getId);
        IPage<DataAlarmEntity> iPage = dataAlarmService.page(dataAlarm.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }


    @ApiOperation(value = "新增")
    @PostMapping("dataAlarm/add")
    @SaCheckPermission("dataAlarm:add")
    @ResponseBody
    public DataResult add(@RequestBody DataAlarmEntity dataAlarm){
            dataAlarmService.save(dataAlarm);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("dataAlarm/delete")
    @SaCheckPermission("dataAlarm:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            dataAlarmService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("dataAlarm/update")
    @SaCheckPermission("dataAlarm:update")
    @ResponseBody
    public DataResult update(@RequestBody DataAlarmEntity dataAlarm){
            dataAlarmService.updateById(dataAlarm);
        return DataResult.success();
    }



}
