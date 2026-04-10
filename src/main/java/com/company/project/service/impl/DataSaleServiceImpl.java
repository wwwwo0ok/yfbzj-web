package com.company.project.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.project.entity.DataSaleEntity;
import com.company.project.entity.SysUser;
import com.company.project.mapper.DataSaleMapper;
import com.company.project.service.DataSaleService;
import com.company.project.service.UserService;


@Service("dataSaleService")
public class DataSaleServiceImpl extends ServiceImpl<DataSaleMapper, DataSaleEntity> implements DataSaleService {

    @Resource
    private UserService userService;
	
	/**
	 * 新增
	 * @param newSale
	 */
    @Override
    @Transactional
	public void addNewSale(DataSaleEntity newSale) {
		
    	//保存
		save(newSale);
		String phone = newSale.getPhone();
		//检查新增用户
        if(phone!=null) {
        	
        	//检查是否包含这个user
        	LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(SysUser::getUsername, phone);
        	List<SysUser> list = userService.list(queryWrapper);
        	if(list==null || list.isEmpty()) {
        		SysUser user = new SysUser();
        		user.setUsername(phone);
        		user.setPassword("bzj"+phone);
        		user.setRealName(newSale.getBuyer());
        		user.setPhone(phone);
        		user.setStatus(0);
        		userService.addUser(user);
        	}
        	
        }
		
	}
	
    @Override
    public DataSaleEntity getByScreenCode(String screenCode) {
    	
    	LambdaQueryWrapper<DataSaleEntity> wrapper = Wrappers.lambdaQuery();
    	
    	wrapper.eq(DataSaleEntity::getProductCode, screenCode);
    	
    	DataSaleEntity one = getOne(wrapper);
    	
    	return one;
    }
    
	
	public void getByPhone(String phone) {
		
		LambdaQueryWrapper<DataSaleEntity> wrapper  = Wrappers.lambdaQuery();
		
		wrapper.eq(StringUtils.isNotBlank(phone),DataSaleEntity::getPhone, phone);
		
		List<DataSaleEntity> list = list(wrapper);
		
		
	}
	
}