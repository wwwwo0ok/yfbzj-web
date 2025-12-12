package com.company.project.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.DecimalFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.project.common.exception.BusinessException;
import com.company.project.common.utils.DateUtils;
import com.company.project.common.utils.FileUtils;
import com.company.project.entity.AppVersionEntity;
import com.company.project.mapper.AppVersionMapper;
import com.company.project.service.AppVersionService;


@Service("appVersionService")
public class AppVersionServiceImpl extends ServiceImpl<AppVersionMapper, AppVersionEntity> implements AppVersionService {

	@Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${file.path}")
    private String filePath;
	
    @Autowired
    private AppVersionMapper appVersionMapper;
    
	@Override
	@Transactional
    public String saveFile(MultipartFile file, HttpServletRequest request) {
        //存储文件夹
        String createTime = DateUtils.format(new Date(), DateUtils.DATEPATTERN);
        
        AppVersionEntity byId = this.getById(request.getParameter("id"));
        
        String version = byId.getVersionCode();//版本号
        
        
     // 根据操作系统确定基础路径
        String os = System.getProperty("os.name").toLowerCase();
        String basePath = FileUtils.getVersionPath();
     // 构建保存路径
        String fileName = "yufeng_app_v" + version + ".apk";
        String savePath = basePath + fileName;
        
        try {
            // 确保目录存在
            File saveDir = new File(savePath).getParentFile();
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            
            // 保存文件（自动覆盖）
            File dest = new File(savePath);
            file.transferTo(dest);
            
            // 获取文件大小并保存到实体
            long fileSize = file.getSize();
            float fileSizeInMB = (float) fileSize / (1024 * 1024); // 转换为MB

            // 使用DecimalFormat格式化保留一位小数
            DecimalFormat df = new DecimalFormat("#.0");
            
            String formattedFileSize = df.format(fileSizeInMB);
            
            
            byId.setFileSize(Float.parseFloat(formattedFileSize));
            
            updateById(byId);
            
            // 生成访问URL
            
            return "success";
        } catch (Exception e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }
	
	
	 /**
     * 获取文件后缀名
     *
     * @param fileName 文件名
     * @return 后缀名
     */
    private String getFileType(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }

    /**
     * 获取跟路径
     *
     * @param request
     * @return
     */
    private String getRootDir(HttpServletRequest request) {
        // 获取协议 (http 或 https)
        String scheme = request.getScheme();

        // 获取域名
        String serverName = request.getServerName();

        // 获取端口号
        int serverPort = request.getServerPort();

        // 构建根路径
        StringBuilder rootURL = new StringBuilder();
        rootURL.append(scheme).append("://").append(serverName);

        // 仅当端口不是默认端口时，才包括端口号
        if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
            rootURL.append(":").append(serverPort);
        }

        return rootURL.toString();
    }


	@Override
	public AppVersionEntity selectLatestVersion() {
		return appVersionMapper.selectLatestVersion();
	}

}