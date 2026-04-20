package com.company.project.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.company.project.entity.DataMessageDayEntity;
import com.company.project.service.DataMessageDayService;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



/**
 * 每日播种统计表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-04-16 14:40:26
 */
@Controller
@RequestMapping("/")
public class DataMessageDayController {
    @Autowired
    private DataMessageDayService dataMessageDayService;

    /**
    * 跳转到页面
    */
    @GetMapping("/index/dataMessageDay")
    public String dataMessageDay() {
        return "datamessageday/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("dataMessageDay/listByPage")
    @SaCheckPermission("dataMessageDay:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody DataMessageDayEntity dataMessageDay){
        LambdaQueryWrapper<DataMessageDayEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper
        .eq(dataMessageDay.getId() != null, DataMessageDayEntity::getId, dataMessageDay.getId())
        .eq(dataMessageDay.getDayString() != null, DataMessageDayEntity::getDayString, dataMessageDay.getDayString())
        .like(dataMessageDay.getDeviceName() != null, DataMessageDayEntity::getDeviceName, dataMessageDay.getDeviceName())
        .orderByDesc(DataMessageDayEntity::getId);
        IPage<DataMessageDayEntity> iPage = dataMessageDayService.page(dataMessageDay.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }
    


    @ApiOperation(value = "大屏推送")
    @PostMapping("dataMessageDay/getDayData")
    @ResponseBody
    public DataResult getDayData(@RequestBody(required = false) DataMessageDayEntity dataMessageDay){
        // 获取当前日期时间
        LocalDate now = LocalDate.now();
        String todayStr = now.toString();                // yyyy-MM-dd
        String yearStr = String.valueOf(now.getYear());  // yyyy
        String yearMonthStr = now.toString().substring(0, 7); // yyyy-MM (如 "2025-04")
        
        // 今日查询
        DataMessageDayEntity todayQuery = new DataMessageDayEntity();
        todayQuery.setDayString(todayStr);
        List<DataMessageDayEntity> todayList = dataMessageDayService.queryDayData(todayQuery);
        
        // 本月查询
        DataMessageDayEntity monthQuery = new DataMessageDayEntity();
        monthQuery.setYearString(yearStr);
        monthQuery.setMonthString(yearMonthStr);
        List<DataMessageDayEntity> monthList = dataMessageDayService.queryDayData(monthQuery);
        
        // 今年查询
        DataMessageDayEntity yearQuery = new DataMessageDayEntity();
        yearQuery.setYearString(yearStr);
        List<DataMessageDayEntity> yearList = dataMessageDayService.queryDayData(yearQuery);
        
        // 计算聚合值
        Map<String, Object> result = new HashMap<>();
        // 今日
        result.put("todayDeviceNum", todayList.size());
        result.put("todayWorkedArea", roundSum(todayList, "workedArea"));
        result.put("todaySeedCount", sumLong(todayList, "seedCount"));
        // 本月
        result.put("monthDeviceNum", monthList.size());
        result.put("monthWorkedArea", roundSum(monthList, "workedArea"));
        result.put("monthSeedCount", sumLong(monthList, "seedCount"));
        // 今年
        result.put("yearDeviceNum", yearList.size());
        result.put("yearWorkedArea", roundSum(yearList, "workedArea"));
        result.put("yearSeedCount", sumLong(yearList, "seedCount"));
        
        return DataResult.success(result);
    }
    
    @ApiOperation(value = "新增")
    @PostMapping("dataMessageDay/add")
    @SaCheckPermission("dataMessageDay:add")
    @ResponseBody
    public DataResult add(@RequestBody DataMessageDayEntity dataMessageDay){
    	dataMessageDayService.save(dataMessageDay);
    	return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("dataMessageDay/delete")
    @SaCheckPermission("dataMessageDay:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            dataMessageDayService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("dataMessageDay/update")
    @SaCheckPermission("dataMessageDay:update")
    @ResponseBody
    public DataResult update(@RequestBody DataMessageDayEntity dataMessageDay){
            dataMessageDayService.updateById(dataMessageDay);
        return DataResult.success();
    }

 // 辅助方法：求和并保留两位小数
    private Double roundSum(List<DataMessageDayEntity> list, String field) {
        double sum = list.stream().mapToDouble(item -> {
            if ("workedArea".equals(field)) return item.getWorkedArea() == null ? 0.0 : item.getWorkedArea();
            return 0.0;
        }).sum();
        return Math.round(sum * 100) / 100.0;
    }

    private Long sumLong(List<DataMessageDayEntity> list, String field) {
        return list.stream().mapToLong(item -> {
            if ("seedCount".equals(field)) return item.getSeedCount() == null ? 0L : item.getSeedCount();
            return 0L;
        }).sum();
    }

}
