package com.company.project.util;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class BaiduCoordConverterHttp {

    private static final String API_KEY = "3P8xyD2iYXPnT1uFzUfdzJiMt5x9HQPt"; // 替换为您的AK
    private static final String CONVERT_URL = "https://api.map.baidu.com/geoconv/v1/";

    public static void main(String[] args) {
        double wgs84Lng = 130.392393;
        double wgs84Lat = 46.810805;
        
        Map<String, Double> map = new HashMap<>();
        map.put("y", wgs84Lat);
        map.put("x", wgs84Lng);
        try {
            Map<String, Double> wgs84ToBd09 = wgs84ToBd09(map);
            System.out.println("转换后的百度坐标: " + wgs84ToBd09.get("x")+","+wgs84ToBd09.get("y"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Double> wgs84ToBd09(Map<String, Double> map) throws IOException {
        // 1. 构建请求URL
        // from=1 表示WGS84坐标系 (GPS)
        // to=5 表示BD09坐标系 (百度)
    	
    	if(map.get("x")==null || map.get("y")==null) {
    		return null;
    	}
    	
    	double lng = map.get("x");
    	double lat = map.get("y");
    	
    	Map<String, Double> resultMap = new HashMap<>();
    	
        String url = String.format("%s?coords=%s,%s&from=1&to=5&ak=%s",
                CONVERT_URL,
                lng, lat,
                API_KEY);

        // 2. 创建HttpClient并执行请求
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "UTF-8");

                // 3. 解析JSON响应
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(result);

                if (rootNode.get("status").asInt() == 0) {
                    JsonNode resultNode = rootNode.get("result").get(0);
                    double bdLng = resultNode.get("x").asDouble();
                    double bdLat = resultNode.get("y").asDouble();
                    resultMap.put("x",bdLng);
                    resultMap.put("y",bdLat);
                } else {
                    throw new RuntimeException("坐标转换失败: " + rootNode.get("message").asText());
                }
            }
        }
        return resultMap;
    }
}
