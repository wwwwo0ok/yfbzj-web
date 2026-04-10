package com.company.project.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.project.entity.DataElectricSeederMessageLineEntity;

/**
 * 电驱播种机行数据表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-12-02 09:54:02
 */
public interface DataElectricSeederMessageLineService extends IService<DataElectricSeederMessageLineEntity> {

	List<DataElectricSeederMessageLineEntity> selectByMessageId(String id);

}

