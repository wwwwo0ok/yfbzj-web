package com.company.project.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.company.project.entity.SysDictEntity;

/**
 * 数据字典 服务类
 *
 * @author wenbin
 * @version V1.0
 * @date 2020年3月18日
 */
public interface SysDictService extends IService<SysDictEntity> {

	/**
	 * 页面查询固定字典
	 * @param dictTypes
	 * @return
	 */
	Map<String, JSONObject> getDictInfoByTypes(List<String> dictTypes);

}

