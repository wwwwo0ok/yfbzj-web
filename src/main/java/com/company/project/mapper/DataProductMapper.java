package com.company.project.mapper;

import com.company.project.entity.DataProductEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 批次表（对应阿里云的产品表）
 * 
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-02-23 13:22:43
 */
public interface DataProductMapper extends BaseMapper<DataProductEntity> {

	DataProductEntity getByProductKey(String code);
	
}
