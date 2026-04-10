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

import com.company.project.entity.DataMessageTypeEntity;
import com.company.project.service.DataMessageTypeService;



/**
 * 数据的消息类型
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-03-24 14:05:37
 */
@Controller
@RequestMapping("/")
public class DataMessageTypeController {
    @Autowired
    private DataMessageTypeService dataMessageTypeService;

    /**
    * 跳转到页面
    */
    @GetMapping("/index/dataMessageType")
    public String dataMessageType() {
        return "datamessagetype/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("dataMessageType/listByPage")
    @SaCheckPermission("dataMessageType:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody DataMessageTypeEntity dataMessageType){
        LambdaQueryWrapper<DataMessageTypeEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(dataMessageType.getId() != null, DataMessageTypeEntity::getId, dataMessageType.getId());
        queryWrapper.orderByDesc(DataMessageTypeEntity::getId);
        IPage<DataMessageTypeEntity> iPage = dataMessageTypeService.page(dataMessageType.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }


    @ApiOperation(value = "新增")
    @PostMapping("dataMessageType/add")
    @SaCheckPermission("dataMessageType:add")
    @ResponseBody
    public DataResult add(@RequestBody DataMessageTypeEntity dataMessageType){
            dataMessageTypeService.save(dataMessageType);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("dataMessageType/delete")
    @SaCheckPermission("dataMessageType:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            dataMessageTypeService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("dataMessageType/update")
    @SaCheckPermission("dataMessageType:update")
    @ResponseBody
    public DataResult update(@RequestBody DataMessageTypeEntity dataMessageType){
            dataMessageTypeService.updateById(dataMessageType);
        return DataResult.success();
    }



}
