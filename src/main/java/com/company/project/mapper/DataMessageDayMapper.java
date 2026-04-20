package com.company.project.mapper;

import com.company.project.entity.DataMessageDayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 每日播种统计表
 * 
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-04-16 14:40:26
 */
public interface DataMessageDayMapper extends BaseMapper<DataMessageDayEntity> {

	List<DataMessageDayEntity> addDayData(@Param("startDate")String l,@Param("endDate")String m);
	
	/**
	 * 查询统计信息
	 * @param entity
	 * @return
	 */
	List<DataMessageDayEntity> queryDayData(@Param("query")DataMessageDayEntity entity);
	
}
