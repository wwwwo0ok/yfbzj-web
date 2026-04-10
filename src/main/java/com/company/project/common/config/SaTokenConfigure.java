package com.company.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.dev33.satoken.thymeleaf.dialect.SaTokenDialect;

@Configuration
public class SaTokenConfigure {

    // Sa-Token 标签方言 (Thymeleaf版) – 如果你仍需要 Thymeleaf 中的标签支持
    @Bean
    public SaTokenDialect saTokenDialect() {
        return new SaTokenDialect();
    }
}