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

import com.company.project.entity.DataElectricSeederMessageLineEntity;
import com.company.project.service.DataElectricSeederMessageLineService;



/**
 * 电驱播种机行数据表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-12-02 09:54:02
 */
@Controller
@RequestMapping("/")
public class DataElectricSeederMessageLineController {
    @Autowired
    private DataElectricSeederMessageLineService dataElectricSeederMessageLineService;

    /**
    * 跳转到页面
    */
    @GetMapping("/index/dataElectricSeederMessageLine")
    public String dataElectricSeederMessageLine() {
        return "dataelectricseedermessageline/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("dataElectricSeederMessageLine/listByPage")
    @SaCheckPermission("dataElectricSeederMessageLine:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody DataElectricSeederMessageLineEntity dataElectricSeederMessageLine){
        LambdaQueryWrapper<DataElectricSeederMessageLineEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(dataElectricSeederMessageLine.getLotId() != null, DataElectricSeederMessageLineEntity::getLotId, dataElectricSeederMessageLine.getLotId());
        queryWrapper.orderByDesc(DataElectricSeederMessageLineEntity::getLotId);
        IPage<DataElectricSeederMessageLineEntity> iPage = dataElectricSeederMessageLineService.page(dataElectricSeederMessageLine.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }


    @ApiOperation(value = "新增")
    @PostMapping("dataElectricSeederMessageLine/add")
    @SaCheckPermission("dataElectricSeederMessageLine:add")
    @ResponseBody
    public DataResult add(@RequestBody DataElectricSeederMessageLineEntity dataElectricSeederMessageLine){
            dataElectricSeederMessageLineService.save(dataElectricSeederMessageLine);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("dataElectricSeederMessageLine/delete")
    @SaCheckPermission("dataElectricSeederMessageLine:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            dataElectricSeederMessageLineService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("dataElectricSeederMessageLine/update")
    @SaCheckPermission("dataElectricSeederMessageLine:update")
    @ResponseBody
    public DataResult update(@RequestBody DataElectricSeederMessageLineEntity dataElectricSeederMessageLine){
            dataElectricSeederMessageLineService.updateById(dataElectricSeederMessageLine);
        return DataResult.success();
    }



}
