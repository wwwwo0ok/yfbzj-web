package com.company.project.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.company.project.mapper.BusinessAlarmCommonMapper;
import com.company.project.entity.BusinessAlarmCommonEntity;
import com.company.project.service.BusinessAlarmCommonService;


@Service("businessAlarmCommonService")
public class BusinessAlarmCommonServiceImpl extends ServiceImpl<BusinessAlarmCommonMapper, BusinessAlarmCommonEntity> implements BusinessAlarmCommonService {


}