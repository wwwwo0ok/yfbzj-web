package com.company.project.controller;

import com.alibaba.fastjson.JSONObject;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.company.project.common.utils.DataResult;

import com.company.project.entity.ParseRegularEntity;
import com.company.project.service.ParseRegularService;



/**
 * 数据转换类（输入为元消息、输出为json）
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-03-24 14:05:37
 */
@Controller
@RequestMapping("/")
public class ParseRegularController {

    private final AppVersionController appVersionController;
    @Autowired
    private ParseRegularService parseRegularService;

    ParseRegularController(AppVersionController appVersionController) {
        this.appVersionController = appVersionController;
    }

    /**
    * 跳转到页面
    */
    @GetMapping("/index/parseRegular")
    public String parseRegular() {
        return "parseregular/list";
    }
    /**
     * 跳转到页面
     */
    @GetMapping("/index/sowingMachineConfig")
    public String machineConfig() {
    	return "parseregular/sowing_machine_config";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("parseRegular/listByPage")
    @SaCheckPermission("parseRegular:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody ParseRegularEntity parseRegular){
        LambdaQueryWrapper<ParseRegularEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(parseRegular.getId() != null, ParseRegularEntity::getId, parseRegular.getId());
        queryWrapper.orderByDesc(ParseRegularEntity::getId);
        IPage<ParseRegularEntity> iPage = parseRegularService.page(parseRegular.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }


    @ApiOperation(value = "新增")
    @PostMapping("parseRegular/add")
    @SaCheckPermission("parseRegular:add")
    @ResponseBody
    public DataResult add(@RequestBody ParseRegularEntity parseRegular){
    	
    	/**
    	 * TODO 新增的时候，需要勾选配置。这个随着我更了解业务之后开始配置。
    	 * 初步就是产品需要设定一个业务选择，就是这个产品有哪些支持，根据平台既定框架，让用户选择
    	 * 比如播种机、收割机？
    	 * 比如电驱还是机械？
    	 * 比如是否支持复合播种？
    	 * 是否有口肥还是主肥？
    	 * 是否有编码器？
    	 * 选完之后，最后确定产品。那么这些选项肯定是固定的。
    	 * 当我最后选择了生成规则之后，会立即按照这个模板初始化一个regularBody
    	 * 这里就是现阶段的新增
    	 * 
    	 */
    	
            parseRegularService.saveRegular(parseRegular);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("parseRegular/delete")
    @SaCheckPermission("parseRegular:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            parseRegularService.removeByIds(ids);
        return DataResult.success();
    }
    @ApiOperation(value = "删除")
    @PostMapping("parseRegular/getOne")
    @ResponseBody
    public DataResult getOne(@RequestBody ParseRegularEntity parseRegular){
    	ParseRegularEntity byId = parseRegularService.getById(parseRegular.getId());
    	return DataResult.success(byId);
    }

    @ApiOperation(value = "更新")
    @PutMapping("parseRegular/update")
    @SaCheckPermission("parseRegular:update")
    @ResponseBody
    public DataResult update(@RequestBody ParseRegularEntity parseRegular){
            parseRegularService.updateById(parseRegular);
        return DataResult.success();
    }



}
