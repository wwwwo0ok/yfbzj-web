package com.company.project.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
        .eq(StringUtils.isNotBlank(dataMessageDay.getDayString()), DataMessageDayEntity::getDayString, dataMessageDay.getDayString())
        .like(dataMessageDay.getDeviceName() != null, DataMessageDayEntity::getDeviceName, dataMessageDay.getDeviceName())
        .orderByDesc(DataMessageDayEntity::getId);
        IPage<DataMessageDayEntity> iPage = dataMessageDayService.page(dataMessageDay.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }
    


    @ApiOperation(value = "大屏推送")
    @PostMapping("dataMessageDay/getDayData")
    @ResponseBody
    public DataResult getDayData(@RequestBody(required = false) DataMessageDayEntity dataMessageDay){
        // 获取当前日期时间（建议指定时区，例如东八区）
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        String todayStr = now.toString();                      // yyyy-MM-dd
        String yearStr = String.valueOf(now.getYear());        // yyyy
        String yearMonthStr = now.toString().substring(0, 7);  // yyyy-MM

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

        // 计算原始聚合值
        double todayWorkedAreaRaw = sumDouble(todayList, "workedArea");
        double monthWorkedAreaRaw = sumDouble(monthList, "workedArea");
        double yearWorkedAreaRaw = sumDouble(yearList, "workedArea");

        long todaySeedCountRaw = sumLong(todayList, "seedCount");
        long monthSeedCountRaw = sumLong(monthList, "seedCount");
        long yearSeedCountRaw = sumLong(yearList, "seedCount");

        // 组装返回结果（字符串格式）
        Map<String, Object> result = new HashMap<>();

        // 今日
        result.put("todayDeviceNum", todayList.size());
        result.put("todayWorkedArea", formatWorkedArea(todayWorkedAreaRaw));
        result.put("todaySeedCount", formatSeedCount(todaySeedCountRaw));

        // 本月
        result.put("monthDeviceNum", monthList.size());
        result.put("monthWorkedArea", formatWorkedArea(monthWorkedAreaRaw));
        result.put("monthSeedCount", formatSeedCount(monthSeedCountRaw));

        // 今年
        result.put("yearDeviceNum", yearList.size());
        result.put("yearWorkedArea", formatWorkedArea(yearWorkedAreaRaw));
        result.put("yearSeedCount", formatSeedCount(yearSeedCountRaw));

        return DataResult.success(result);
    }

    /**
     * 格式化面积：≥1000 时转为“千”并保留一位小数，不足1000 时保留一位小数（不加单位）
     */
    private String formatWorkedArea(double value) {
        if (value >= 10000) {
            double val = value / 10000.0;
            // 四舍五入保留一位小数
            double rounded = Math.round(val * 10) / 10.0;
            // 如果 rounded 是整数，去掉 .0
            if (rounded == (long) rounded) {
                return (long) rounded + "万";
            }
            return rounded + "万";
        } else {
            // 保留一位小数
            double rounded = Math.round(value * 10) / 10.0;
            if (rounded == (long) rounded) {
                return String.valueOf((long) rounded);
            }
            return String.valueOf(rounded);
        }
    }

    /**
     * 格式化粒数：≥1亿 显示“亿”，≥1万 显示“万”，否则显示原值，均保留一位小数
     */
    private String formatSeedCount(long value) {
        if (value >= 100_000_000) {
            double val = value / 100_000_000.0;
            double rounded = Math.round(val * 10) / 10.0;
            if (rounded == (long) rounded) {
                return (long) rounded + "亿";
            }
            return rounded + "亿";
        } else if (value >= 10_000) {
            double val = value / 10_000.0;
            double rounded = Math.round(val * 10) / 10.0;
            if (rounded == (long) rounded) {
                return (long) rounded + "万";
            }
            return rounded + "万";
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * 求 Double 类型字段的和（支持 null 值）
     */
    private double sumDouble(List<DataMessageDayEntity> list, String fieldName) {
        return list.stream()
                .mapToDouble(item -> {
                    if ("workedArea".equals(fieldName)) {
                        Double val = item.getWorkedArea();
                        return val == null ? 0.0 : val;
                    }
                    return 0.0;
                })
                .sum();
    }

    /**
     * 求 Long 类型字段的和（支持 null 值）
     */
    private long sumLong(List<DataMessageDayEntity> list, String fieldName) {
        return list.stream()
                .mapToLong(item -> {
                    if ("seedCount".equals(fieldName)) {
                        Long val = item.getSeedCount();
                        return val == null ? 0L : val;
                    }
                    return 0L;
                })
                .sum();
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


}
