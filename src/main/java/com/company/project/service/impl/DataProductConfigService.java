package com.company.project.service.impl;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Component
public class DataProductConfigService {

	public JSONObject generate(String configId) {
		
		
		JSONObject resultJsonObject = new JSONObject();
		JSONArray mainJson = new JSONArray();
		JSONArray lineJson = new JSONArray();
		JSONArray alarmJson = new JSONArray();
		
		resultJsonObject.put("main",mainJson);
		resultJsonObject.put("line",lineJson);
		resultJsonObject.put("alarm",alarmJson);
		
		createMain(mainJson);
		createLine(lineJson);
		createAlarm(alarmJson);
		
		return resultJsonObject;
	}

	private void createLine(JSONArray mainJson) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", "seedNum");
		jsonObject.put("name", "每行种数");
		mainJson.add(jsonObject);
		jsonObject = new JSONObject();
		jsonObject.put("code", "seedSwtich");
		jsonObject.put("name", "种子监控使能");
		mainJson.add(jsonObject);
		jsonObject = new JSONObject();
		jsonObject.put("code", "mainFertilizerSwtich");
		jsonObject.put("name", "主肥电机使能");
		mainJson.add(jsonObject);
		jsonObject = new JSONObject();
		jsonObject.put("code", "deputyFertilizerSwtich");
		jsonObject.put("name", "口肥电机使能");
		mainJson.add(jsonObject);
		jsonObject = new JSONObject();
		jsonObject.put("code", "mainFertilizerMonitorSwtich");
		jsonObject.put("name", "主肥监控使能");
		mainJson.add(jsonObject);
	}
	
	private void createAlarm(JSONArray alarmJson) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", "lineNo");
		jsonObject.put("name", "行号");
		alarmJson.add(jsonObject);
		jsonObject = new JSONObject();
		jsonObject.put("code", "code");
		jsonObject.put("name", "报警编码");
		alarmJson.add(jsonObject);
	}

	

	/**
	 * 创建主数据
	 * @param mainJson
	 */
	private void createMain(JSONArray mainJson) {
		JSONObject mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "sowLine");
		mainJsonObject.put("name", "播种行数");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "uploadType");
		mainJsonObject.put("name", "物联上传数据类型");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "sowType");
		mainJsonObject.put("name", "播种类型");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "mainFertilizerMonitoringFlag");
		mainJsonObject.put("name", "主肥监控标志");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "mainFertilizerMotorFlag");
		mainJsonObject.put("name", "主肥电机标志");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "deputyFertilizerMotorFlag");
		mainJsonObject.put("name", "口肥电机标志");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "seedLocationFlag");
		mainJsonObject.put("name", "种物位标志");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "mainFertilizerLocationFlag");
		mainJsonObject.put("name", "主肥物位标志");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "deputyFertilizerLocationFlag");
		mainJsonObject.put("name", "口肥物位标志");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "sowInterval");
		mainJsonObject.put("name", "株距（单位mm）");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "sowingWidth");
		mainJsonObject.put("name", "总播种宽度（单位mm）");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "setMainFertilizerValue");
		mainJsonObject.put("name", "设定主肥量（单位kg）");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "realMainFertilizerValue");
		mainJsonObject.put("name", "实测主肥量（单位g）");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "setDeputyFertilizerValue");
		mainJsonObject.put("name", "设定口肥量（单位kg）");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "realDeputyFertilizerValue");
		mainJsonObject.put("name", "实测口肥量（单位g）");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "mainFertilizerRate");
		mainJsonObject.put("name", "主肥系数");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "deputyFertilizerRate");
		mainJsonObject.put("name", "口肥系数");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "lackFertilizerRate");
		mainJsonObject.put("name", "主肥监控缺肥灵敏度");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "runningTime");
		mainJsonObject.put("name", "单次播种运行时间（单位秒）");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "sowDistance");
		mainJsonObject.put("name", "单次播种距离（单位m）");
		mainJson.add(mainJsonObject);
		mainJsonObject = new JSONObject();
		mainJsonObject.put("code", "seedFlag");
		mainJsonObject.put("name", "种子使能（是否开启播种）");
		mainJson.add(mainJsonObject);
	}

}
