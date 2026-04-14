package com.company.project.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.company.project.common.exception.BusinessException;
import com.company.project.common.utils.DataResult;
import com.company.project.entity.SysDictDetailEntity;
import com.company.project.entity.SysDictEntity;
import com.company.project.service.SysDictDetailService;
import com.company.project.service.SysDictService;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * 字典管理
 *
 * @author wenbin
 * @version V1.0
 * @date 2020年3月18日
 */
@Api(tags = "字典管理")
@RestController
@RequestMapping("/sysDict")
public class SysDictController {
    @Resource
    private SysDictService sysDictService;
    @Resource
    private SysDictDetailService sysDictDetailService;


    

    @ApiOperation(value = "查阅词典")
    @PostMapping("/getDict")
    @ResponseBody
    public DataResult getDict(@RequestBody List<String> dictTypes) {  // 或者 @RequestBody String[] dictTypes
        // 1. 从数据库查询字典数据（根据传入的 dictTypes）
        Map<String, JSONObject> dictInfoByTypes = sysDictService.getDictInfoByTypes(dictTypes);
        
        // 2. 返回成功结果
        return DataResult.success(dictInfoByTypes);
    }

    
    
    @ApiOperation(value = "新增")
    @PostMapping("/add")
    @SaCheckPermission("sysDict:add")
    public void add(@RequestBody SysDictEntity sysDict) {
        if (StringUtils.isEmpty(sysDict.getName())) {
            throw new BusinessException("字典名称不能为空");
        }
        SysDictEntity q = sysDictService.getOne(Wrappers.<SysDictEntity>lambdaQuery().eq(SysDictEntity::getName, sysDict.getName()));
        if (q != null) {
            throw new BusinessException("字典名称已存在");
        }
        sysDictService.save(sysDict);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/delete")
    @SaCheckPermission("sysDict:delete")
    public void delete(@RequestBody @ApiParam(value = "id集合") List<String> ids) {
        sysDictService.removeByIds(ids);
        //删除detail
        sysDictDetailService.remove(Wrappers.<SysDictDetailEntity>lambdaQuery().in(SysDictDetailEntity::getDictId, ids));
    }

    @ApiOperation(value = "更新")
    @PutMapping("/update")
    @SaCheckPermission("sysDict:update")
    public void update(@RequestBody SysDictEntity sysDict) {
        if (StringUtils.isEmpty(sysDict.getName())) {
            throw new BusinessException("字典名称不能为空");
        }

        SysDictEntity q = sysDictService.getOne(Wrappers.<SysDictEntity>lambdaQuery().eq(SysDictEntity::getName, sysDict.getName()));
        if (q != null && !q.getId().equals(sysDict.getId())) {
            throw new BusinessException("字典名称已存在");
        }

        sysDictService.updateById(sysDict);
    }

    @ApiOperation(value = "查询分页数据")
    @PostMapping("/listByPage")
    @SaCheckPermission("sysDict:list")
    public IPage<SysDictEntity> findListByPage(@RequestBody SysDictEntity sysDict) {
        LambdaQueryWrapper<SysDictEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        if (!StringUtils.isEmpty(sysDict.getName())) {
            queryWrapper.like(SysDictEntity::getName, sysDict.getName());
            queryWrapper.or();
            queryWrapper.like(SysDictEntity::getRemark, sysDict.getName());
        }
        queryWrapper.orderByAsc(SysDictEntity::getName);
        return sysDictService.page(sysDict.getQueryPage(), queryWrapper);
    }

}
