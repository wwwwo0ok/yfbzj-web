package com.company.project.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.company.project.common.exception.BusinessException;
import com.company.project.common.utils.DataResult;
import com.company.project.common.utils.FileUtils;
import com.company.project.entity.AppVersionEntity;
import com.company.project.service.AppVersionService;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



/**
 * APP版本更新
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-12-08 13:50:31
 */
@Controller
@RequestMapping("/")
public class AppVersionController {
    @Autowired
    private AppVersionService appVersionService;

    /**
    * 跳转到页面
    */
    @GetMapping("/index/appVersion")
    public String appVersion() {
        return "appversion/list";
    }


    @ApiOperation(value = "查询分页数据")
    @PostMapping("appVersion/listByPage")
    @SaCheckPermission("appVersion:list")
    @ResponseBody
    public DataResult findListByPage(@RequestBody AppVersionEntity appVersion){
        LambdaQueryWrapper<AppVersionEntity> queryWrapper = Wrappers.lambdaQuery();
        //查询条件示例
        queryWrapper.eq(appVersion.getId() != null, AppVersionEntity::getId, appVersion.getId());
        queryWrapper.orderByDesc(AppVersionEntity::getId);
        IPage<AppVersionEntity> iPage = appVersionService.page(appVersion.getQueryPage(), queryWrapper);
        return DataResult.success(iPage);
    }

    @ApiOperation(value = "上传APP")
    @PostMapping("appVersion/uploadApp")
    @SaCheckPermission("appVersion:update")
    @ResponseBody
    public DataResult add(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request) {
        //判断文件是否空
        if (file == null || file.getOriginalFilename() == null || "".equalsIgnoreCase(file.getOriginalFilename().trim())) {
            throw new BusinessException("文件为空");
        }
        appVersionService.saveFile(file, request);
        
        return DataResult.success();
    }
    


    

    @ApiOperation(value = "新增")
    @PostMapping("appVersion/add")
    @SaCheckPermission("appVersion:add")
    @ResponseBody
    public DataResult add(@RequestBody AppVersionEntity appVersion){
            appVersionService.save(appVersion);
        return DataResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("appVersion/delete")
    @SaCheckPermission("appVersion:delete")
    @ResponseBody
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids){
            appVersionService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("appVersion/update")
    @SaCheckPermission("appVersion:update")
    @ResponseBody
    public DataResult update(@RequestBody AppVersionEntity appVersion){
            appVersionService.updateById(appVersion);
        return DataResult.success();
    }
    

    @ApiOperation(value = "查询更新")
    @GetMapping("appVersion/checkUpdate")
    @ResponseBody
    public DataResult checkUpdate() {
    	AppVersionEntity appVersionEntity = appVersionService.selectLatestVersion();
    	return DataResult.success(appVersionEntity);
    }
    @ApiOperation(value = "下载文件")
    @GetMapping("appVersion/downloadApp")
    @ResponseBody
	public void downloadApp(HttpServletResponse response) {
	    try {
	        // 根据版本号查找应用版本信息
	        AppVersionEntity appVersion = appVersionService.selectLatestVersion();
	        if (appVersion == null) {
	            response.setStatus(HttpStatus.NOT_FOUND.value());
	            response.getWriter().write("Version not found");
	            return;
	        }
	        
	        // 获取文件路径（这里假设文件存储在服务器指定目录）
	        
	        String basePath = FileUtils.getVersionPath();
	        // 构建保存路径
	           String fileName = "yufeng_app_v" + appVersion.getVersionCode() + ".apk";
	           String filePath = basePath + fileName;
	        
	        File file = new File(filePath);
	        
	        if (!file.exists()) {
	            response.setStatus(HttpStatus.NOT_FOUND.value());
	            response.getWriter().write("File not found");
	            return;
	        }
	        
	        // 设置响应头
	        response.setContentType("application/vnd.android.package-archive");
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
	        response.setHeader("Content-Length", String.valueOf(file.length()));
	        
	        // 将文件写入响应输出流
	        try (InputStream inputStream = new FileInputStream(file);
	             OutputStream outputStream = response.getOutputStream()) {
	            
	            byte[] buffer = new byte[1024];
	            int bytesRead;
	            while ((bytesRead = inputStream.read(buffer)) != -1) {
	                outputStream.write(buffer, 0, bytesRead);
	            }
	            outputStream.flush();
	        }
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	    }
	}




}
