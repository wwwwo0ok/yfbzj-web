package com.company.project.controller;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
import com.company.project.entity.DataElectricSeederMessageEntity;
import com.company.project.service.DataElectricSeederMessageLineService;
import com.company.project.service.DataElectricSeederMessageService;

import cn.dev33.satoken.annotation.SaCheckPermission;
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
	@Autowired
	private RedissonClient redissonClient;
    /**
    * 跳转到页面
    */
    @GetMapping("/index/dataElectricSeederMessage")
    public String dataElectricSeederMessage() {
        return "dataelectricseedermessage/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("dataElectricSeederMessage/listByPage")
    @ResponseBody
    public DataResult findListByPage(@RequestBody DataElectricSeederMessageEntity dataElectricSeederMessage){
        
        //空不查询
        if(StringUtils.isBlank(dataElectricSeederMessage.getLotId())) {
        	return DataResult.success();
        }
        //单数据同步
        //查询条件示例
//        queryWrapper.eq(dataElectricSeederMessage.getLotId() != null, DataElectricSeederMessageEntity::getLotId, dataElectricSeederMessage.getLotId());
//        queryWrapper.orderByDesc(DataElectricSeederMessageEntity::getDataTime);
//        IPage<DataElectricSeederMessageEntity> iPage = dataElectricSeederMessageService.page(dataElectricSeederMessage.getQueryPage(), queryWrapper);
        IPage<DataElectricSeederMessageEntity> iPage = dataElectricSeederMessageService.getMessageList(dataElectricSeederMessage);
        iPage.getRecords().forEach(li -> {
        	if(li.getDataTime()!=null) {
        		li.setDataTimeString(li.getDataTime().toString().replace("T", " "));
        	}
        });
        return DataResult.success(iPage);
    }
    


    @ApiOperation(value = "新增")
    @PostMapping("dataElectricSeederMessage/add")
    @ResponseBody
    public DataResult add(@RequestBody DataElectricSeederMessageEntity dataElectricSeederMessage){
            dataElectricSeederMessageService.save(dataElectricSeederMessage);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("dataElectricSeederMessage/delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            dataElectricSeederMessageService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("dataElectricSeederMessage/update")
    @ResponseBody
    public DataResult update(@RequestBody DataElectricSeederMessageEntity dataElectricSeederMessage){
            dataElectricSeederMessageService.updateById(dataElectricSeederMessage);
        return DataResult.success();
    }

    
   

}
