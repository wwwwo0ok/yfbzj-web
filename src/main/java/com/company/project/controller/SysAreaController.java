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

import com.company.project.entity.SysAreaEntity;
import com.company.project.service.SysAreaService;



/**
 * 地区表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-01-15 18:37:07
 */
@Controller
@RequestMapping("/")
public class SysAreaController {
    @Autowired
    private SysAreaService sysAreaService;

    /**
    * 跳转到页面
    */
    @GetMapping("/index/sysArea")
    public String sysArea() {
        return "sysarea/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("sysArea/listByPage")
    @SaCheckPermission("sysArea:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody SysAreaEntity sysArea){
        LambdaQueryWrapper<SysAreaEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(sysArea.getId() != null, SysAreaEntity::getId, sysArea.getId());
        queryWrapper.orderByDesc(SysAreaEntity::getId);
        IPage<SysAreaEntity> iPage = sysAreaService.page(sysArea.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }
    
    @ApiOperation(value = "查询省")
    @PostMapping("sysArea/getArea")
    @ResponseBody
    public DataResult getArea(@RequestBody SysAreaEntity sysArea){
    	LambdaQueryWrapper<SysAreaEntity> queryWrapper = Wrappers.lambdaQuery();
    	//查询条件示例
    	queryWrapper.eq(StringUtils.isNotBlank(sysArea.getParentId()), SysAreaEntity::getParentId, sysArea.getParentId());
    	queryWrapper.orderByDesc(SysAreaEntity::getId);
    	List<SysAreaEntity> list = sysAreaService.list(queryWrapper);
    	return DataResult.success(list);
    }


    @ApiOperation(value = "新增")
    @PostMapping("sysArea/add")
    @SaCheckPermission("sysArea:add")
    @ResponseBody
    public DataResult add(@RequestBody SysAreaEntity sysArea){
            sysAreaService.save(sysArea);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("sysArea/delete")
    @SaCheckPermission("sysArea:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            sysAreaService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("sysArea/update")
    @SaCheckPermission("sysArea:update")
    @ResponseBody
    public DataResult update(@RequestBody SysAreaEntity sysArea){
            sysAreaService.updateById(sysArea);
        return DataResult.success();
    }



}
