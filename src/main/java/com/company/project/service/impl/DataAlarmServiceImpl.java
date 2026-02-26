package com.company.project.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.company.project.mapper.DataAlarmMapper;
import com.company.project.entity.DataAlarmEntity;
import com.company.project.service.DataAlarmService;


@Service("dataAlarmService")
public class DataAlarmServiceImpl extends ServiceImpl<DataAlarmMapper, DataAlarmEntity> implements DataAlarmService {


}