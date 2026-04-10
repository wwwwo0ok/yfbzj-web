package com.company.project.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.company.project.common.utils.DataResult;
import com.company.project.entity.DataSaleEntity;
import com.company.project.entity.SysAreaEntity;
import com.company.project.service.DataSaleService;
import com.company.project.service.SysAreaService;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



/**
 * 销售信息表
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2026-04-01 10:19:02
 */
@Controller
@RequestMapping("/")
public class DataSaleController {
    @Autowired
    private DataSaleService dataSaleService;
    @Autowired
    private SysAreaService sysAreaService;
    
    /**
    * 跳转到页面
    */
    @GetMapping("/index/dataSale")
    public String dataSale() {
        return "datasale/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("dataSale/listByPage")
    @SaCheckPermission("dataSale:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody DataSaleEntity dataSale){
        LambdaQueryWrapper<DataSaleEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(dataSale.getId() != null, DataSaleEntity::getId, dataSale.getId());
        queryWrapper.orderByDesc(DataSaleEntity::getId);
        IPage<DataSaleEntity> iPage = dataSaleService.page(dataSale.getQueryPage(), queryWrapper);
        
        iPage.getRecords().forEach(li -> {
        	
        	String provinceId = li.getProvinceId();
        	SysAreaEntity province = sysAreaService.getById(provinceId);
        	if(province!=null) {
        		li.setProvinceName(province.getName());
        	}
        	String cityId = li.getCityId();
        	SysAreaEntity city = sysAreaService.getById(cityId);
        	if(city!=null) {
        		li.setCityName(city.getName());
        	}
        	String countryId = li.getCountryId();
        	SysAreaEntity country = sysAreaService.getById(countryId);
        	if(country!=null) {
        		li.setCountryName(country.getName());
        	}
        	
        });
        return DataResult.success(iPage);
    }


    @ApiOperation(value = "新增")
    @PostMapping("dataSale/add")
    @SaCheckPermission("dataSale:add")
    @ResponseBody
    public DataResult add(@RequestBody DataSaleEntity dataSale){
            dataSaleService.addNewSale(dataSale);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("dataSale/delete")
    @SaCheckPermission("dataSale:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            dataSaleService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("dataSale/update")
    @SaCheckPermission("dataSale:update")
    @ResponseBody
    public DataResult update(@RequestBody DataSaleEntity dataSale){
            dataSaleService.updateById(dataSale);
        return DataResult.success();
    }


//    @PostMapping("/import")
//    public DataResult importExcel(@RequestParam("file") MultipartFile file) throws IOException {
//        // 这里需要注入 yourService
//        EasyExcel.read(file.getInputStream(), DataSaleEntity.class, new DataSaleExcelListener(dataSaleService))
//                .sheet()
//                .doRead();
//        return DataResult.success("导入成功");
//    }

    @PostMapping("dataSale/export")
    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("销售信息", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        List<DataSaleEntity> dataList = dataSaleService.list(); // 查询要导出的数据
        EasyExcel.write(response.getOutputStream(), DataSaleEntity.class)
                .sheet("销售信息")
                .doWrite(dataList);
    }

}
