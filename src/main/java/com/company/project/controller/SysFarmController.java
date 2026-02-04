package com.company.project.controller;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

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
import com.company.project.entity.SysAreaEntity;
import com.company.project.entity.SysFarmEntity;
import com.company.project.entity.SysUser;
import com.company.project.service.SysAreaService;
import com.company.project.service.SysFarmService;
import com.company.project.service.UserService;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



/**
 * 农场主/合作社
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-01-15 18:37:08
 */
@Controller
@RequestMapping("/")
public class SysFarmController {
    @Autowired
    private SysFarmService sysFarmService;
    @Autowired
    private SysAreaService sysAreaService;
    
    @Resource
    private UserService userService;

    /**
    * 跳转到页面
    */
    @GetMapping("/index/sysFarm")
    public String sysFarm() {
        return "sysfarm/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("sysFarm/listByPage")
    @SaCheckPermission("sysFarm:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody SysFarmEntity sysFarm){
        LambdaQueryWrapper<SysFarmEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(sysFarm.getId() != null, SysFarmEntity::getId, sysFarm.getId());
        queryWrapper.orderByDesc(SysFarmEntity::getId);
        IPage<SysFarmEntity> iPage = sysFarmService.page(sysFarm.getQueryPage(), queryWrapper);
        
        iPage.getRecords().forEach(li -> {
        	
        	String provinceId = li.getProvinceId();
        	SysAreaEntity province = sysAreaService.getById(provinceId);
        	if(province!=null) {
        		li.setProvinceName(province.getName());
        	}
        	String cityId = li.getCityId();
        	SysAreaEntity city = sysAreaService.getById(cityId);
        	if(city!=null) {
        		li.setCityName(city.getName());
        	}
        	String countryId = li.getCountryId();
        	SysAreaEntity country = sysAreaService.getById(countryId);
        	if(country!=null) {
        		li.setCountryName(country.getName());
        	}
        	
        });
        
        return DataResult.success(iPage);
    }
    
    
    
    @ApiOperation(value = "下拉展示")
    @PostMapping("sysFarm/select")
    @ResponseBody
    public DataResult select(){
        List<SysFarmEntity> list = sysFarmService.list();
        
        final Map<String,String> resultMap = new TreeMap<>();
        
        list.forEach(li -> {
        	
        	String nameStr = "";
        	
        	
        	String provinceId = li.getProvinceId();
        	SysAreaEntity province = sysAreaService.getById(provinceId);
        	if(province!=null) {
        		nameStr+=(province.getName());
        	}
        	nameStr+="-";
        	String cityId = li.getCityId();
        	SysAreaEntity city = sysAreaService.getById(cityId);
        	if(city!=null) {
        		nameStr+=(city.getName());
        	}
        	nameStr+="-";
        	String countryId = li.getCountryId();
        	SysAreaEntity country = sysAreaService.getById(countryId);
        	if(country!=null) {
        		nameStr+=(country.getName());
        	}
        	
        	nameStr+="-";
        	nameStr+=li.getName();
        	
        	resultMap.put(li.getId(), nameStr);
        });
        
        return DataResult.success(resultMap);
    }

    @ApiOperation(value = "新增")
    @PostMapping("sysFarm/add")
    @SaCheckPermission("sysFarm:add")
    @ResponseBody
    public DataResult add(@RequestBody SysFarmEntity sysFarm){
            sysFarmService.save(sysFarm);
        //同时新增用户
        if(sysFarm.getPhone()!=null) {
        	
        	//检查是否包含这个user
        	LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(SysUser::getUsername, sysFarm.getPhone());
        	List<SysUser> list = userService.list(queryWrapper);
        	if(list==null || list.isEmpty()) {
        		SysUser user = new SysUser();
        		user.setUsername(sysFarm.getPhone());
        		user.setPassword("bzj"+sysFarm.getPhone());
        		user.setRealName(sysFarm.getOwnerName());
        		user.setPhone(sysFarm.getPhone());
        		user.setStatus(0);
        		userService.addUser(user);
        	}
        	
        }
        
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("sysFarm/delete")
    @SaCheckPermission("sysFarm:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            sysFarmService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("sysFarm/update")
    @SaCheckPermission("sysFarm:update")
    @ResponseBody
    public DataResult update(@RequestBody SysFarmEntity sysFarm){
            sysFarmService.updateById(sysFarm);
        return DataResult.success();
    }



}
