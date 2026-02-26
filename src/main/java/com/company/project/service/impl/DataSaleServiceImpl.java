package com.company.project.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.company.project.mapper.DataSaleMapper;
import com.company.project.entity.DataSaleEntity;
import com.company.project.service.DataSaleService;


@Service("dataSaleService")
public class DataSaleServiceImpl extends ServiceImpl<DataSaleMapper, DataSaleEntity> implements DataSaleService {


}