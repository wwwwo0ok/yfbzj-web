package com.company.project.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.company.project.mapper.DataMessageTypeMapper;
import com.company.project.entity.DataMessageTypeEntity;
import com.company.project.service.DataMessageTypeService;


@Service("dataMessageTypeService")
public class DataMessageTypeServiceImpl extends ServiceImpl<DataMessageTypeMapper, DataMessageTypeEntity> implements DataMessageTypeService {


}