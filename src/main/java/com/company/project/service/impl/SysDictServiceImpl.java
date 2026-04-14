package com.company.project.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.project.entity.SysDictDetailEntity;
import com.company.project.entity.SysDictEntity;
import com.company.project.mapper.SysDictDetailMapper;
import com.company.project.mapper.SysDictMapper;
import com.company.project.service.SysDictService;

/**
 * 数据字典 服务类
 *
 * @author wenbin
 * @version V1.0
 * @date 2020年3月18日
 */
@Service("sysDictService")
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDictEntity> implements SysDictService {

    @Resource
    private SysDictDetailMapper sysDictDetailMapper;

    /**
     * 根据字典类型查询字典数据信息
     *
     * @param name 字典名称
     * @return 参数键值
     **/
    public JSONObject getType(String name) {
        if (StringUtils.isEmpty(name)) {
            return new JSONObject();
        }
        //根据名称获取字典
        SysDictEntity dict = this.getOne(Wrappers.<SysDictEntity>lambdaQuery().eq(SysDictEntity::getName, name));
        if (dict == null || dict.getId() == null) {
            return new JSONObject();
        }
        //获取明细
        List<SysDictDetailEntity> list = sysDictDetailMapper.selectList(Wrappers.<SysDictDetailEntity>lambdaQuery().eq(SysDictDetailEntity::getDictId, dict.getId()));
        
        Map<String, String> map = list.stream()
        	    .collect(Collectors.toMap(
        	        SysDictDetailEntity::getValue,
        	        SysDictDetailEntity::getLabel
        	    ));
        
        return JSONObject.parseObject(JSON.toJSONString(map));
    }
    
    @Override
    public Map<String, JSONObject> getDictInfoByTypes(List<String> dictTypes) {
    	
    	Map<String, JSONObject> resultMap = new HashMap<>();
    	
    	dictTypes.forEach(typeArguments ->resultMap.put(typeArguments,getType(typeArguments)));
    	
    	return resultMap;
    }

}