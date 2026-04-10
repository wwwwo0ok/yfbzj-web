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

import com.company.project.entity.BusinessAlarmCommonEntity;
import com.company.project.service.BusinessAlarmCommonService;



/**
 * 播种机自带通用报警表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-03-24 14:05:37
 */
@Controller
@RequestMapping("/")
public class BusinessAlarmCommonController {
    @Autowired
    private BusinessAlarmCommonService businessAlarmCommonService;

    /**
    * 跳转到页面
    */
    @GetMapping("/index/businessAlarmCommon")
    public String businessAlarmCommon() {
        return "businessalarmcommon/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("businessAlarmCommon/listByPage")
    @SaCheckPermission("businessAlarmCommon:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody BusinessAlarmCommonEntity businessAlarmCommon){
        LambdaQueryWrapper<BusinessAlarmCommonEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(businessAlarmCommon.getId() != null, BusinessAlarmCommonEntity::getId, businessAlarmCommon.getId());
        queryWrapper.orderByDesc(BusinessAlarmCommonEntity::getId);
        IPage<BusinessAlarmCommonEntity> iPage = businessAlarmCommonService.page(businessAlarmCommon.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }


    @ApiOperation(value = "新增")
    @PostMapping("businessAlarmCommon/add")
    @SaCheckPermission("businessAlarmCommon:add")
    @ResponseBody
    public DataResult add(@RequestBody BusinessAlarmCommonEntity businessAlarmCommon){
            businessAlarmCommonService.save(businessAlarmCommon);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("businessAlarmCommon/delete")
    @SaCheckPermission("businessAlarmCommon:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            businessAlarmCommonService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("businessAlarmCommon/update")
    @SaCheckPermission("businessAlarmCommon:update")
    @ResponseBody
    public DataResult update(@RequestBody BusinessAlarmCommonEntity businessAlarmCommon){
            businessAlarmCommonService.updateById(businessAlarmCommon);
        return DataResult.success();
    }



}
