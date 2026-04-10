package com.company.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.project.entity.ParseRegularEntity;

/**
 * 数据转换类（输入为元消息、输出为json）
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-03-24 14:05:37
 */
public interface ParseRegularService extends IService<ParseRegularEntity> {

	void saveRegular(ParseRegularEntity parseRegular);

}

