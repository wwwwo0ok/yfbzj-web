package com.company.project.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aliyun.sdk.service.iot20180120.AsyncClient;
import com.aliyun.sdk.service.iot20180120.models.QueryProductListRequest;
import com.aliyun.sdk.service.iot20180120.models.QueryProductListResponse;
import com.aliyun.sdk.service.iot20180120.models.QueryProductListResponseBody.ProductInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.project.entity.DataProductEntity;
import com.company.project.mapper.DataProductMapper;
import com.company.project.service.DataProductService;
import com.company.project.util.AliyunIotConstants;


@Service("dataProductService")
public class DataProductServiceImpl extends ServiceImpl<DataProductMapper, DataProductEntity> implements DataProductService {


	@Autowired
	AsyncClient client;
	
	
	@Override
	public void updateProduct() {
		
		List<ProductInfo> list = new ArrayList<>();
		
		int currentPage = 1;
        final int pageSize = 100;
        try {
			CompletableFuture<QueryProductListResponse> queryProductList = client.queryProductList(queryProductList(currentPage,pageSize));
			List<ProductInfo> productInfo = queryProductList.get().getBody().getData().getList().getProductInfo();
			list.addAll(productInfo);
			
			list.forEach(this::saveByAliYun);
			
			
        }catch (Exception e) {
        	e.printStackTrace();
		}
		
	}
	
	public void saveByAliYun(ProductInfo productInfo) {
		String productKey = productInfo.getProductKey();
		DataProductEntity entity = getByProductKey(productKey);
		
		
		if(entity == null) {
			entity = new DataProductEntity();
			entity.setCode(productKey);
			entity.setName(productInfo.getProductName());
			entity.setDescription(productInfo.getDescription());
			entity.setDeviceNum(productInfo.getDeviceCount());
			entity.setLevel(DataProductEntity.LEVEL_NO_UPDATE);
			entity.setCreateTime(new Date(productInfo.getGmtCreate()));
			save(entity);
		}else {
			entity.setName(productInfo.getProductName());
			entity.setDescription(productInfo.getDescription());
			updateById(entity);
		}
	}

	
	@Override
	public boolean isActive(String code) {
		LambdaQueryWrapper<DataProductEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
    	LambdaQueryWrapper<DataProductEntity> eq = queryWrapper
    			.eq(StringUtils.isNotBlank(code), DataProductEntity::getCode,code)
    			.gt( DataProductEntity::getLevel,0)
    			;
    	DataProductEntity one = getOne(eq);
		
		return one!=null;
	}
	
	
    private DataProductEntity getByProductKey(String productKey) {
    	LambdaQueryWrapper<DataProductEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
    	LambdaQueryWrapper<DataProductEntity> eq = queryWrapper.eq(StringUtils.isNotBlank(productKey), DataProductEntity::getCode,productKey);
        
    	DataProductEntity one = getOne(eq);
		return one;
	}
    @Override
    public List<DataProductEntity> getListByLevel(int level) {
    	LambdaQueryWrapper<DataProductEntity> queryWrapper = Wrappers.lambdaQuery();
    	//查询条件示例
    	LambdaQueryWrapper<DataProductEntity> eq = queryWrapper.eq(DataProductEntity::getLevel,level);
    	
    	List<DataProductEntity> list = list(eq);
    	
    	return list;
    }

	private QueryProductListRequest queryProductList(int currentPage, int pageSize) {
        return QueryProductListRequest.builder()
            .iotInstanceId(AliyunIotConstants.IOT_INSTANCE_ID)
            .pageSize(pageSize)       // 显式设置每页大小
            .currentPage(currentPage) // 设置当前页码
            .build();
    }

}