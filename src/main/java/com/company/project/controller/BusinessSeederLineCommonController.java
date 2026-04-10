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

import com.company.project.entity.BusinessSeederLineCommonEntity;
import com.company.project.service.BusinessSeederLineCommonService;



/**
 * 播种机行业通用行数据表（所有型号共有的行级基础数据）
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-03-24 14:05:37
 */
@Controller
@RequestMapping("/")
public class BusinessSeederLineCommonController {
    @Autowired
    private BusinessSeederLineCommonService businessSeederLineCommonService;

    /**
    * 跳转到页面
    */
    @GetMapping("/index/businessSeederLineCommon")
    public String businessSeederLineCommon() {
        return "businessseederlinecommon/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("businessSeederLineCommon/listByPage")
    @SaCheckPermission("businessSeederLineCommon:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody BusinessSeederLineCommonEntity businessSeederLineCommon){
        LambdaQueryWrapper<BusinessSeederLineCommonEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(businessSeederLineCommon.getId() != null, BusinessSeederLineCommonEntity::getId, businessSeederLineCommon.getId());
        queryWrapper.orderByDesc(BusinessSeederLineCommonEntity::getId);
        IPage<BusinessSeederLineCommonEntity> iPage = businessSeederLineCommonService.page(businessSeederLineCommon.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }


    @ApiOperation(value = "新增")
    @PostMapping("businessSeederLineCommon/add")
    @SaCheckPermission("businessSeederLineCommon:add")
    @ResponseBody
    public DataResult add(@RequestBody BusinessSeederLineCommonEntity businessSeederLineCommon){
            businessSeederLineCommonService.save(businessSeederLineCommon);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("businessSeederLineCommon/delete")
    @SaCheckPermission("businessSeederLineCommon:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            businessSeederLineCommonService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("businessSeederLineCommon/update")
    @SaCheckPermission("businessSeederLineCommon:update")
    @ResponseBody
    public DataResult update(@RequestBody BusinessSeederLineCommonEntity businessSeederLineCommon){
            businessSeederLineCommonService.updateById(businessSeederLineCommon);
        return DataResult.success();
    }



}
