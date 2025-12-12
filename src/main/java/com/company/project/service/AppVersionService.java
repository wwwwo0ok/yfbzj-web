package com.company.project.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.project.entity.AppVersionEntity;

/**
 * APP版本更新
 *
 * @author wenbin
 * @email *****@mail.com
 * @date 2025-12-08 13:50:31
 */
public interface AppVersionService extends IService<AppVersionEntity> {

	String saveFile(MultipartFile file, HttpServletRequest request);

	AppVersionEntity selectLatestVersion();

}

