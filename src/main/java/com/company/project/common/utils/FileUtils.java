package com.company.project.common.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;

import com.company.project.common.exception.BusinessException;

public class FileUtils {

	
	public static String getVersionPath() {
		
		String os = System.getProperty("os.name").toLowerCase();
        String basePath;
        if (os.contains("win")) {
            basePath = "C:/apk_storage/";
        } else {
            basePath = "/opt/apk_storage/";
            // 确保Linux目录存在且有适当权限
            File linuxDir = new File(basePath);
            if (!linuxDir.exists()) {
                linuxDir.mkdirs();
                // 设置目录权限 (rwxr-xr-x)
                try {
                    Files.setPosixFilePermissions(linuxDir.toPath(), 
                        PosixFilePermissions.fromString("rwxr-xr-x"));
                } catch (IOException e) {
                    throw new BusinessException("设置目录权限失败");
                }
            }
        }
		
        return basePath;
	}
	
}
