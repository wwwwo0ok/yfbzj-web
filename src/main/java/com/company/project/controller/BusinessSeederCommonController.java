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

import com.company.project.entity.BusinessSeederCommonEntity;
import com.company.project.service.BusinessSeederCommonService;



/**
 * 播种机行业公用字段表（所有型号共有的核心业务数据）
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-03-24 14:05:37
 */
@Controller
@RequestMapping("/")
public class BusinessSeederCommonController {
    @Autowired
    private BusinessSeederCommonService businessSeederCommonService;

    /**
    * 跳转到页面
    */
    @GetMapping("/index/businessSeederCommon")
    public String businessSeederCommon() {
        return "businessseedercommon/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("businessSeederCommon/listByPage")
    @SaCheckPermission("businessSeederCommon:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody BusinessSeederCommonEntity businessSeederCommon){
        LambdaQueryWrapper<BusinessSeederCommonEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(businessSeederCommon.getId() != null, BusinessSeederCommonEntity::getId, businessSeederCommon.getId());
        queryWrapper.orderByDesc(BusinessSeederCommonEntity::getId);
        IPage<BusinessSeederCommonEntity> iPage = businessSeederCommonService.page(businessSeederCommon.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }


    @ApiOperation(value = "新增")
    @PostMapping("businessSeederCommon/add")
    @SaCheckPermission("businessSeederCommon:add")
    @ResponseBody
    public DataResult add(@RequestBody BusinessSeederCommonEntity businessSeederCommon){
            businessSeederCommonService.save(businessSeederCommon);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("businessSeederCommon/delete")
    @SaCheckPermission("businessSeederCommon:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            businessSeederCommonService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("businessSeederCommon/update")
    @SaCheckPermission("businessSeederCommon:update")
    @ResponseBody
    public DataResult update(@RequestBody BusinessSeederCommonEntity businessSeederCommon){
            businessSeederCommonService.updateById(businessSeederCommon);
        return DataResult.success();
    }



}
