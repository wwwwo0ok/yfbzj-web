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

import com.company.project.entity.DataProductEntity;
import com.company.project.service.DataProductService;



/**
 * 批次表（对应阿里云的产品表）
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-02-23 13:22:43
 */
@Controller
@RequestMapping("/")
public class DataProductController {
    @Autowired
    private DataProductService dataProductService;

    /**
    * 跳转到页面
    */
    @GetMapping("/index/dataProduct")
    public String dataProduct() {
        return "dataproduct/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("dataProduct/listByPage")
    @SaCheckPermission("dataProduct:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody DataProductEntity dataProduct){
        LambdaQueryWrapper<DataProductEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(dataProduct.getId() != null, DataProductEntity::getId, dataProduct.getId());
        queryWrapper.orderByDesc(DataProductEntity::getLevel)
        .orderByDesc(DataProductEntity::getCreateTime)
        ;
        IPage<DataProductEntity> iPage = dataProductService.page(dataProduct.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }
    @ApiOperation(value = "提供菜单查询")
    @PostMapping("dataProduct/getSelectList")
    @ResponseBody
    public DataResult getSelectList(@RequestBody DataProductEntity dataProduct){
    	LambdaQueryWrapper<DataProductEntity> queryWrapper = Wrappers.lambdaQuery();
    	//查询条件示例
    	queryWrapper.eq(dataProduct.getCode() != null, DataProductEntity::getCode, dataProduct.getCode());
    	queryWrapper.orderByDesc(DataProductEntity::getLevel)
    	.orderByDesc(DataProductEntity::getCreateTime)
    	;
    	List<DataProductEntity> list = dataProductService.list( queryWrapper);
    	return DataResult.success(list);
    }


    @ApiOperation(value = "新增")
    @PostMapping("dataProduct/add")
    @SaCheckPermission("dataProduct:add")
    @ResponseBody
    public DataResult add(@RequestBody DataProductEntity dataProduct){
            dataProductService.save(dataProduct);
        return DataResult.success();
    }
    @ApiOperation(value = "更新产品列表")
    @PostMapping("dataProduct/updateProduct")
    @ResponseBody
    public DataResult updateProduct(){
    	dataProductService.updateProduct();
    	return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("dataProduct/delete")
    @SaCheckPermission("dataProduct:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            dataProductService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("dataProduct/update")
    @SaCheckPermission("dataProduct:update")
    @ResponseBody
    public DataResult update(@RequestBody DataProductEntity dataProduct){
            dataProductService.updateById(dataProduct);
        return DataResult.success();
    }



}
