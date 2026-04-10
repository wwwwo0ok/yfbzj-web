package com.company.project.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.company.project.mapper.BusinessSeederLineCommonMapper;
import com.company.project.entity.BusinessSeederLineCommonEntity;
import com.company.project.service.BusinessSeederLineCommonService;


@Service("businessSeederLineCommonService")
public class BusinessSeederLineCommonServiceImpl extends ServiceImpl<BusinessSeederLineCommonMapper, BusinessSeederLineCommonEntity> implements BusinessSeederLineCommonService {


}