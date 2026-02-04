package com.company.project.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.company.project.mapper.SysFarmMapper;
import com.company.project.entity.SysFarmEntity;
import com.company.project.service.SysFarmService;


@Service("sysFarmService")
public class SysFarmServiceImpl extends ServiceImpl<SysFarmMapper, SysFarmEntity> implements SysFarmService {


}