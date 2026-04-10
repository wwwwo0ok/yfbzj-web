package com.company.project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.company.project.mapper.ParseRegularMapper;
import com.company.project.entity.ParseRegularEntity;
import com.company.project.service.ParseRegularService;


@Service("parseRegularService")
public class ParseRegularServiceImpl extends ServiceImpl<ParseRegularMapper, ParseRegularEntity> implements ParseRegularService {

	@Autowired
	DataProductConfigService productConfigService;
	
	
	@Override
	public void saveRegular(ParseRegularEntity parseRegular) {
		
		JSONObject regularBody = productConfigService.generate(null);
		
		parseRegular.setRegularBody(regularBody);
		
		save(parseRegular);
	}

}