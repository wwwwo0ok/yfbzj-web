package com.company.project;

import java.net.InetAddress;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.iot20180120.AsyncClient;
import com.company.project.util.AliyunIotConstants;

import darabonba.core.client.ClientOverrideConfiguration;
import lombok.extern.slf4j.Slf4j;

/**
 * 启动类
 *
 * @author wenbin
 */
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@MapperScan("com.company.project.mapper")
@Slf4j
@ServletComponentScan(basePackages = {"com.company.project.common.filter"}) //这一句完成了配置，Springboot的”懒理念“真的厉害。
@EnableScheduling  // 关键注解
public class CompanyProjectApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext application = SpringApplication.run(CompanyProjectApplication.class, args);

        Environment env = application.getEnvironment();
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Login: \thttp://{}:{}/manager\n\t" +
                        "Doc: \thttp://{}:{}/manager/doc.html\n" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));

    }
    
 // 在您的Spring Boot主应用类或任意@Configuration类中
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    
 // 在应用启动时初始化单例Client
    @Bean
    public AsyncClient asyncClient() {
    	StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(AliyunIotConstants.RAM_ACCESS_KEY)
                .accessKeySecret(AliyunIotConstants.RAM_ACCESS_KEY_SECRET)
                .build());
        return AsyncClient.builder()
                .region("cn-shanghai")
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("iot.cn-shanghai.aliyuncs.com")
                )
                .build();
    }

}
