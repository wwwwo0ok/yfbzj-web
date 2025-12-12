package com.company.project.mapper;

import com.company.project.entity.AppVersionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * APP版本更新
 * 
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-12-08 13:50:31
 */
public interface AppVersionMapper extends BaseMapper<AppVersionEntity> {

	@Select("select * from app_version where release_Time = (select max(release_Time) from app_version)")
	AppVersionEntity selectLatestVersion();
	
}
