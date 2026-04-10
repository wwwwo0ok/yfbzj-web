package com.company.project.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.company.project.mapper.BusinessSeederCommonMapper;
import com.company.project.entity.BusinessSeederCommonEntity;
import com.company.project.service.BusinessSeederCommonService;


@Service("businessSeederCommonService")
public class BusinessSeederCommonServiceImpl extends ServiceImpl<BusinessSeederCommonMapper, BusinessSeederCommonEntity> implements BusinessSeederCommonService {


}