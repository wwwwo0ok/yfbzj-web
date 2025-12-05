package com.company.project.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.company.project.mapper.DataElectricSeederMessageLineMapper;
import com.company.project.entity.DataElectricSeederMessageLineEntity;
import com.company.project.service.DataElectricSeederMessageLineService;


@Service("dataElectricSeederMessageLineService")
public class DataElectricSeederMessageLineServiceImpl extends ServiceImpl<DataElectricSeederMessageLineMapper, DataElectricSeederMessageLineEntity> implements DataElectricSeederMessageLineService {


}