package com.company.project.baidu;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

// BaiduMapService.java
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BaiduMapService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BaiduMapProperties baiduMapProperties;

    /**
     * 根据城市名称获取其下一级的行政区划（例如街道）
     * @param cityName 城市名称，如 "杭州市"
     * @return 返回一个Map，包含ECharts所需的GeoJSON数据和其他信息
     * @throws Exception 
     */
    public Map<String, Object> getSubDistricts(String cityName) throws Exception {
        // 1. 构建请求URL
        String url = UriComponentsBuilder.fromHttpUrl(baiduMapProperties.getApi().getGeocoder())
                .queryParam("address", cityName)
                .queryParam("output", "json")
                .queryParam("ak", baiduMapProperties.getAk())
                .build()
                .encode()
                .toUriString();

        // 2. 发起HTTP GET请求，获取返回的JSON字符串
        String result = sendGetRequest(url);

        System.out.println(result);
//        // 3. 使用Jackson的ObjectMapper解析JSON
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode rootNode = objectMapper.readTree(responseJson);
//
//        // 4. 检查API调用是否成功
//        if (!"0".equals(rootNode.get("status").asText())) {
//            throw new RuntimeException("调用百度地图API失败: " + rootNode.get("message").asText());
//        }
//
//        // 5. 解析并提取所需的地理数据（这里需要根据百度API的实际返回结构进行解析）
//        // 假设返回结果中包含一个名为 "result" 的对象，里面有一个 "subdistricts" 数组
//        JsonNode resultNode = rootNode.get("result");
//        JsonNode districtsNode = resultNode.get("subdistricts"); // 这通常是一个数组，包含下级行政区划
//
//        // 6. 将百度返回的JSON数据转换为ECharts能识别的GeoJSON格式
//        // 这是一个复杂的过程，通常需要遍历百度返回的坐标点数组，并按照GeoJSON标准构建FeatureCollection
//        // 由于百度返回的坐标是BD09坐标系，而GeoJSON通常使用WGS84，可能还需要坐标转换（此处略过）
//        // 以下是一个简化的示例，实际结构会更复杂
//        Map<String, Object> geoJson = new HashMap<>();
//        geoJson.put("type", "FeatureCollection");
//        geoJson.put("features", districtsNode); // 实际需要更复杂的转换逻辑
//
//        // 7. 将处理好的GeoJSON和其他数据放入返回Map中
//        Map<String, Object> response = new HashMap<>();
//        response.put("geoJSON", geoJson);
//        response.put("cityName", cityName);
        // ... 可以添加其他元数据

//        return response;
        return null;
    }
    
    
    private static String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        return response.toString();
    }
    
    /**
     * 获取地理编码
     */
    
    
    
    /**
     * 获取逆地理编码（播种机所在的IP位置）
     */
    
    
    
}