package com.company.project.baidu;
// BaiduMapProperties.java
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "baidu.map")
public class BaiduMapProperties {
    private String ak;
    private Api api;

    @Data
    public static class Api {
        private String geocoder;
    }
}