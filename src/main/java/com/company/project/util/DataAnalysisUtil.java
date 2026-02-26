package com.company.project.util;

/**
 * 2025年12月之后的解码方式。
 */
public class DataAnalysisUtil {
    
	//机械

	
//	public static void test() {
//		
//		String jxMessage = "0000010104830064051401F41D10004604800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001407D000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
//		//电驱
//		String dqMessage = "000500050005000A00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000808080800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000802800C80078190007D0092800C80078190007D0000A0F000000320700081450FFFF0F000008000A000A0000000000000000FF0000FF0000FF0000FF000000000900E9420000000000000000000000000000000000000000000000000000000000000000000000000000224000000FFF";
//		
//		DataElectricSeederMessageEntity entity = analysisElectricHexStr(dqMessage);
//		DataElectricSeederMessageEntity oldEntity = oldAnalysisElectricHexStr(dqMessage);
//		
//		Integer sowLine = entity.getSowLine();
//		Integer sowLine2 = oldEntity.getSowLine();
//		
//		
//		System.out.println(sowLine+","+sowLine2);
//		
//		
//	}
	
	
	/**
	 * 老电驱解析方法
	 * @param hexData
	 * @return
	 */
//	public static DataElectricSeederMessageEntity oldAnalysisElectricHexStr(String hexData) {
//		
//		DataElectricSeederMessageEntity entity = new DataElectricSeederMessageEntity();
//		
//		 // 上传状态
//        entity.setUploadType(BzjUtil.parseUploadType(hexData));
//
//        // 苗带数
//        entity.setSowMode(BzjUtil.parseSeedBeltSelection(hexData));
//
//        // 运行时间
//        entity.setRunningTime(BzjUtil.parseSowingRuntime(hexData));
//
//        // 平均速度（需要根据实际需求计算或调用相应方法）
//        //是用 播种距离/时间 算出来的
////        map.put("averageSpeed", BzjUtil.averageSpeed(hexData)); // 需要根据实际需求实现
//        
//        	
//        // 行数
//        entity.setSowLine(BzjUtil.parseSowingLines(hexData));
//        
//        // 复合播种
////        entity.setSowType(BzjUtil.parseGlobalABSowingMode(hexData));
//
//        // 雷达频率
////        map.put("radarFrequency", BzjUtil.parseRadarFrequency(hexData));
//        entity.setRadarFrequency(BzjUtil.parseRadarFrequency(hexData));
//
//        // 播种幅宽
//        entity.setSowingWidth(BzjUtil.parseTotalSowingWidth(hexData));
//
//        // A播种株距
//        entity.setASowInterval(BzjUtil.parsePlantSpacingA(hexData));
//        
//
//        // A减速比
//        entity.setAReductionRatio(BzjUtil.parseReductionRatioA(hexData));
//
//        // A孔盘数
//        entity.setAHolesNum(BzjUtil.parseHoleDiscCountA(hexData));
//
//        // A给脉冲
//        entity.setASendPulse(BzjUtil.parsePulsesPerRevolutionA(hexData));
//
//        // A反馈脉冲
//        entity.setARecievePulse(BzjUtil.parseFeedbackPulsesA(hexData));
//
//        // B播种株距
//        entity.setBSowInterval(BzjUtil.parsePlantSpacing(hexData));
//
//        // B减速比
//        entity.setBReductionRatio(BzjUtil.parseReductionRatio(hexData));
//
//        // B孔盘数
//        entity.setBHolesNum(BzjUtil.parseHoleDiscCount(hexData));
//
//        // B给脉冲
//        entity.setBSendPulse(BzjUtil.parsePulsesPerRevolution(hexData));
//
//        // B反馈脉冲
//        entity.setBRecievePulse(BzjUtil.parseFeedbackPulses(hexData));
//
//        // 急停阈值
//        entity.setDecelerationValue(BzjUtil.parseEmergencyStopThreshold(hexData));
//
//        // 预判时间
//        entity.setPredictedTime(BzjUtil.parseSeedPredictTime(hexData));
//
//        // 主肥断流
//        entity.setLackFertilizerLine(BzjUtil.parseMainFertBreakLine(hexData));
//
//        // 主肥堵塞
////        map.put("mainFertilizerBlockage", BzjUtil.parseMainFertBlockLine(hexData));
//
//        // 主肥系数
//        entity.setMainFertilizerRate(BzjUtil.parseMainFertSowingCoeffA(hexData));
//
//        // 主肥测试
//        entity.setRealMainFertilizerValue(BzjUtil.parseMainFertTestQuantity(hexData));
//
//        // 主肥设定（需要根据实际需求实现）
//        entity.setSetMainFertilizerValue(null);
//
//        // 口肥系数
//        entity.setDeputyFertilizerRate(BzjUtil.parseMouthFertSowingCoeffA(hexData));
//
//        // 口肥测试
//        entity.setRealDeputyFertilizerValue(BzjUtil.parseMouthFertTestQuantity(hexData));
//
//        // 系统电压
//        entity.setSystemVoltage(BzjUtil.parseSystemVoltage(hexData));
//
//        // 系统气压
//        entity.setFanPressure(BzjUtil.parseFanPressure(hexData));
//
//        // 风机转速
//        entity.setFanSpeed(BzjUtil.parseFanSpeed(hexData));
//
//        // 限制里程
//        entity.setRemainingMileage(BzjUtil.parseRemainingDistance(hexData));
//        
//        //TODO 播种形状(里边很多属性）
////        map.put("sowingShape", BzjUtil.parseStatusFlags(hexData));
//        
//        String flagsHex = hexData.substring(342, 344);
//        int value = Integer.parseInt(flagsHex, 16);
//
//        entity.setMainFertilizerMonitoringFlag(value & 0x80);
//        entity.setMainFertilizerMotorFlag(value & 0x40);
//        entity.setDeputyFertilizerMotorFlag(value & 0x20);
////        statusMap.put("seedLevel", (value & 0x10) == 0 ? "取消" : "启用");       // 第4位
////        statusMap.put("mainFertLevel", (value & 0x08) == 0 ? "取消" : "启用");   // 第5位
////        statusMap.put("mouthFertLevel", (value & 0x04) == 0 ? "取消" : "启用");  // 第6位
//        entity.setSoundFlag(value & 0x02);
//        entity.setSowType(value & 0x01);
//        
//
//        // 生成24行数据列表
//        List<DataElectricSeederMessageLineEntity> seedData = new ArrayList<>();
//        for (int line = 1; line <= entity.getSowLine(); line++) {
////            String mainFertRatio = BzjUtil.parseMainFertRatio(hexData, line, mainFertMonitor, mainFertMotor);
////            String calcBreakLine = BzjUtil.parseCalcBreakLine(hexData, line, mainFertRatio);
////            String calcBlockLine = BzjUtil.parseCalcBlockLine(mainFertRatio);
//
//            DataElectricSeederMessageLineEntity row = new DataElectricSeederMessageLineEntity();
//            row.setLineNo(line);
//            row.setSeedSwtich(BzjUtil.parseSeedEnable(hexData, line));
//            row.setSeedAbType(BzjUtil.parseABSelection(hexData, line));
//            row.setSeedNum(BzjUtil.parseSeedQuantity(hexData, line));
//            row.setExcessSeeds(BzjUtil.parseMultiPercentage(hexData, line));
//            row.setLackSeeds(BzjUtil.parseSowingPercentage(hexData, line));
//            row.setMainFertilizerMonitorSwtich(BzjUtil.parseMainFertMonitor(hexData, line));
//            row.setMainFertilizerSwtich(BzjUtil.parseMainFertMotor(hexData, line));
//            row.setMainFertilizerDirect(BzjUtil.parseMotorDirection(hexData, line));
//            row.setDeputyFertilizerSwtich(BzjUtil.parseMouthFertMotor(hexData, line));
//            row.setDeputyFertilizerDirect(BzjUtil.parseMouthMotorDirection(hexData, line));
//            //TODO 报警记录，待议
////            row.setSeedAlarmRatio(BzjUtil.parseSeedAlarm(hexData, line));
//            
//            //TODO 解析肥料报警记录
////            row.setFertilizerAlarmRatio(BzjUtil.parseFertAlarm(hexData, line));
//            
////            row.setMainFertRatio(mainFertRatio);
////            row.setCalcBreakLine(calcBreakLine);
////            row.setCalcBlockLine(calcBlockLine);
//            seedData.add(row);
//        }
//        entity.setLines(seedData);
//        
//        return entity;
//	}
	
	
	
	
	
	
	
	

	

	
	/**
	 * 判断播种机类型
	 * @param hexStr
	 * @return 1是机械，2是电驱
	 */
	public static String checkDeviceType(String hexStr) {
		
		
		
		return "0";
	}
	
	
	/**
	 * 简单截取
	 * @param hexStr
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	private static int subHexStr(String hexStr,int startIndex,int endIndex) {
		
		if(startIndex >= hexStr.length() ) {
			return 0;
		}
		
		return Integer.parseInt(hexStr.substring(startIndex,endIndex),16);
	}


//	public static DataElectricSeederMessageEntity transToEntity(String hexData) {
//		
//		//新规则解析的对象
//		DataElectricSeederMessageEntity entity = analysisElectricHexStr(hexData);
//		//老规则解析的对象
//		DataElectricSeederMessageEntity oldEntity = oldAnalysisElectricHexStr(hexData);
//		
//		//分辨数据的角度,检测新的，不满足就返回老的
//		//1、行数不可能为0
//		Integer sowLine = entity.getSowLine();
//		//2、播种宽度不可能为0
//		Integer sowingWidth = entity.getSowingWidth();
//		//3、给脉冲和反脉冲不可能是0
//		Integer aSendPulse = entity.getASendPulse();
//		Integer bSendPulse = entity.getBSendPulse();
//		Integer aRecievePulse = entity.getARecievePulse();
//		Integer bRecievePulse = entity.getBRecievePulse();
//		//4、
//		
//		
//		if(
//				 sowLine == 0 
//				 ||sowLine > 24
//				 ||sowingWidth == 0
//				 ||(aSendPulse==0&&bSendPulse==0)
//				 ||(aRecievePulse==0&&bRecievePulse==0)
//				 
//				) {
//			return oldEntity;
//		}
//		
//		
////		System.out.println(sowLine+","+sowLine2);
//		
//		return entity;
//	}

}