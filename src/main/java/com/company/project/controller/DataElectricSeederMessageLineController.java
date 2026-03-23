package com.company.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.company.project.common.utils.DataResult;
import com.company.project.entity.DataElectricSeederMessageLineEntity;
import com.company.project.service.DataElectricSeederMessageLineService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



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
    @ResponseBody
    public DataResult findListByPage(@RequestBody DataElectricSeederMessageLineEntity dataElectricSeederMessageLine){
        LambdaQueryWrapper<DataElectricSeederMessageLineEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(dataElectricSeederMessageLine.getMessageId() != null, DataElectricSeederMessageLineEntity::getMessageId, dataElectricSeederMessageLine.getMessageId());
        queryWrapper.orderByAsc(DataElectricSeederMessageLineEntity::getLineNo);
        IPage<DataElectricSeederMessageLineEntity> iPage = dataElectricSeederMessageLineService.page(dataElectricSeederMessageLine.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }


    @ApiOperation(value = "新增")
    @PostMapping("dataElectricSeederMessageLine/add")
    @ResponseBody
    public DataResult add(@RequestBody DataElectricSeederMessageLineEntity dataElectricSeederMessageLine){
            dataElectricSeederMessageLineService.save(dataElectricSeederMessageLine);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("dataElectricSeederMessageLine/delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            dataElectricSeederMessageLineService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("dataElectricSeederMessageLine/update")
    @ResponseBody
    public DataResult update(@RequestBody DataElectricSeederMessageLineEntity dataElectricSeederMessageLine){
            dataElectricSeederMessageLineService.updateById(dataElectricSeederMessageLine);
        return DataResult.success();
    }



}
