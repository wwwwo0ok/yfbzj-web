package com.company.project.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.company.project.mapper.DataElectricSeederMessageLineMapper;
import com.company.project.entity.DataElectricSeederMessageLineEntity;
import com.company.project.service.DataElectricSeederMessageLineService;


@Service("dataElectricSeederMessageLineService")
public class DataElectricSeederMessageLineServiceImpl extends ServiceImpl<DataElectricSeederMessageLineMapper, DataElectricSeederMessageLineEntity> implements DataElectricSeederMessageLineService {

	@Override
	public List<DataElectricSeederMessageLineEntity> selectByMessageId(String id) {
		
		 LambdaQueryWrapper<DataElectricSeederMessageLineEntity> queryWrapper = Wrappers.lambdaQuery();
	        //查询条件示例
	        queryWrapper.eq(id != null, DataElectricSeederMessageLineEntity::getMessageId, id);
	     
	        List<DataElectricSeederMessageLineEntity> list = list(queryWrapper);
	        
		
		return list;
	}


}