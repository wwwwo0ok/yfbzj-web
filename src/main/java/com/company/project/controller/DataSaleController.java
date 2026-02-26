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

import com.company.project.entity.DataSaleEntity;
import com.company.project.service.DataSaleService;



/**
 * 销售信息表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-02-26 09:18:21
 */
@Controller
@RequestMapping("/")
public class DataSaleController {
    @Autowired
    private DataSaleService dataSaleService;

    /**
    * 跳转到页面
    */
    @GetMapping("/index/dataSale")
    public String dataSale() {
        return "datasale/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("dataSale/listByPage")
    @SaCheckPermission("dataSale:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody DataSaleEntity dataSale){
        LambdaQueryWrapper<DataSaleEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(dataSale.getId() != null, DataSaleEntity::getId, dataSale.getId());
        queryWrapper.orderByDesc(DataSaleEntity::getId);
        IPage<DataSaleEntity> iPage = dataSaleService.page(dataSale.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }


    @ApiOperation(value = "新增")
    @PostMapping("dataSale/add")
    @SaCheckPermission("dataSale:add")
    @ResponseBody
    public DataResult add(@RequestBody DataSaleEntity dataSale){
            dataSaleService.save(dataSale);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("dataSale/delete")
    @SaCheckPermission("dataSale:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            dataSaleService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("dataSale/update")
    @SaCheckPermission("dataSale:update")
    @ResponseBody
    public DataResult update(@RequestBody DataSaleEntity dataSale){
            dataSaleService.updateById(dataSale);
        return DataResult.success();
    }



}
