package com.company.project.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.company.project.mapper.SysAreaMapper;
import com.company.project.entity.SysAreaEntity;
import com.company.project.service.SysAreaService;


@Service("sysAreaService")
public class SysAreaServiceImpl extends ServiceImpl<SysAreaMapper, SysAreaEntity> implements SysAreaService {


}