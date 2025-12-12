package com.company.project.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.iot20180120.AsyncClient;
import com.aliyun.sdk.service.iot20180120.models.ListAnalyticsDataRequest;
import com.aliyun.sdk.service.iot20180120.models.ListAnalyticsDataResponse;
import com.aliyun.sdk.service.iot20180120.models.ListAnalyticsDataResponseBody;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.company.project.common.utils.DataResult;
import com.company.project.entity.DataElectricSeederMessageEntity;
import com.company.project.entity.DataElectricSeederMessageLineEntity;
import com.company.project.service.DataElectricSeederMessageLineService;
import com.company.project.service.DataElectricSeederMessageService;
import com.company.project.util.AliyunIotConstants;
import com.company.project.util.BzjUtil;
import com.company.project.util.DataAnalysisUtil;

import cn.dev33.satoken.annotation.SaCheckPermission;
import darabonba.core.client.ClientOverrideConfiguration;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



/**
 * 电驱播种机消息记录表 数据来自于播种机
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-12-02 09:53:58
 */
@Controller
@RequestMapping("/")
public class DataElectricSeederMessageController {
    @Autowired
    private DataElectricSeederMessageService dataElectricSeederMessageService;
    
    @Autowired
    private DataElectricSeederMessageLineService dataElectricSeederMessageLineService;

    /**
    * 跳转到页面
    */
    @GetMapping("/index/dataElectricSeederMessage")
    public String dataElectricSeederMessage() {
        return "dataelectricseedermessage/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("dataElectricSeederMessage/listByPage")
//    @SaCheckPermission("dataElectricSeederMessage:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody DataElectricSeederMessageEntity dataElectricSeederMessage){
        LambdaQueryWrapper<DataElectricSeederMessageEntity> queryWrapper = Wrappers.lambdaQuery();
        
        //空不查询
        if(StringUtils.isBlank(dataElectricSeederMessage.getLotId())) {
        	return DataResult.success();
        }
        //单数据同步
        dataElectricSeederMessageService.insertNewData(dataElectricSeederMessage.getLotId());
        //查询条件示例
//        queryWrapper.eq(dataElectricSeederMessage.getLotId() != null, DataElectricSeederMessageEntity::getLotId, dataElectricSeederMessage.getLotId());
//        queryWrapper.orderByDesc(DataElectricSeederMessageEntity::getDataTime);
//        IPage<DataElectricSeederMessageEntity> iPage = dataElectricSeederMessageService.page(dataElectricSeederMessage.getQueryPage(), queryWrapper);
        
        IPage<DataElectricSeederMessageEntity> iPage = dataElectricSeederMessageService.getMessageList(dataElectricSeederMessage);
        return DataResult.success(iPage);
    }


    @ApiOperation(value = "新增")
    @PostMapping("dataElectricSeederMessage/add")
    @SaCheckPermission("dataElectricSeederMessage:add")
    @ResponseBody
    public DataResult add(@RequestBody DataElectricSeederMessageEntity dataElectricSeederMessage){
            dataElectricSeederMessageService.save(dataElectricSeederMessage);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("dataElectricSeederMessage/delete")
    @SaCheckPermission("dataElectricSeederMessage:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            dataElectricSeederMessageService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("dataElectricSeederMessage/update")
    @SaCheckPermission("dataElectricSeederMessage:update")
    @ResponseBody
    public DataResult update(@RequestBody DataElectricSeederMessageEntity dataElectricSeederMessage){
            dataElectricSeederMessageService.updateById(dataElectricSeederMessage);
        return DataResult.success();
    }

    
   

}
