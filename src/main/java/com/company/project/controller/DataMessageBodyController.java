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

import com.company.project.entity.DataMessageBodyEntity;
import com.company.project.service.DataMessageBodyService;



/**
 * 原数据表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-03-24 14:05:38
 */
@Controller
@RequestMapping("/")
public class DataMessageBodyController {
    @Autowired
    private DataMessageBodyService dataMessageBodyService;

    /**
    * 跳转到页面
    */
    @GetMapping("/index/dataMessageBody")
    public String dataMessageBody() {
        return "datamessagebody/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("dataMessageBody/listByPage")
    @SaCheckPermission("dataMessageBody:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody DataMessageBodyEntity dataMessageBody){
        LambdaQueryWrapper<DataMessageBodyEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(dataMessageBody.getId() != null, DataMessageBodyEntity::getId, dataMessageBody.getId());
        queryWrapper.orderByDesc(DataMessageBodyEntity::getId);
        IPage<DataMessageBodyEntity> iPage = dataMessageBodyService.page(dataMessageBody.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }


    @ApiOperation(value = "新增")
    @PostMapping("dataMessageBody/add")
    @SaCheckPermission("dataMessageBody:add")
    @ResponseBody
    public DataResult add(@RequestBody DataMessageBodyEntity dataMessageBody){
            dataMessageBodyService.save(dataMessageBody);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("dataMessageBody/delete")
    @SaCheckPermission("dataMessageBody:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            dataMessageBodyService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("dataMessageBody/update")
    @SaCheckPermission("dataMessageBody:update")
    @ResponseBody
    public DataResult update(@RequestBody DataMessageBodyEntity dataMessageBody){
            dataMessageBodyService.updateById(dataMessageBody);
        return DataResult.success();
    }



}
