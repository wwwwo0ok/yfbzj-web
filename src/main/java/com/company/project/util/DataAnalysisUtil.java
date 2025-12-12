package com.company.project.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.company.project.entity.DataElectricSeederMessageEntity;
import com.company.project.entity.DataElectricSeederMessageLineEntity;

/**
 * 2025年12月之后的解码方式。
 */
public class DataAnalysisUtil {
    
	//机械

	
	public static void test() {
		
		 String jxMessage = "0000010104830064051401F41D10004604800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001407D000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
		//电驱
		String dqMessage = "000500050005000A00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000808080800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000802800C80078190007D0092800C80078190007D0000A0F000000320700081450FFFF0F000008000A000A0000000000000000FF0000FF0000FF0000FF000000000900E9420000000000000000000000000000000000000000000000000000000000000000000000000000224000000FFF";
		
			DataElectricSeederMessageEntity entity = analysisElectricHexStr(dqMessage);
			DataElectricSeederMessageEntity oldEntity = oldAnalysisElectricHexStr(dqMessage);
			
			Integer sowLine = entity.getSowLine();
			Integer sowLine2 = oldEntity.getSowLine();
			
			
			System.out.println(sowLine+","+sowLine2);
		
		
	}
	
	
	/**
	 * 老电驱解析方法
	 * @param hexData
	 * @return
	 */
	public static DataElectricSeederMessageEntity oldAnalysisElectricHexStr(String hexData) {
		
		DataElectricSeederMessageEntity entity = new DataElectricSeederMessageEntity();
		
		 // 上传状态
        entity.setUploadType(BzjUtil.parseUploadType(hexData));

        // 苗带数
        entity.setSowMode(BzjUtil.parseSeedBeltSelection(hexData));

        // 运行时间
        entity.setRunningTime(BzjUtil.parseSowingRuntime(hexData));

        // 平均速度（需要根据实际需求计算或调用相应方法）
        //是用 播种距离/时间 算出来的
//        map.put("averageSpeed", BzjUtil.averageSpeed(hexData)); // 需要根据实际需求实现
        
        	
        // 行数
        entity.setSowLine(BzjUtil.parseSowingLines(hexData));
        
        // 复合播种
//        entity.setSowType(BzjUtil.parseGlobalABSowingMode(hexData));

        // 雷达频率
//        map.put("radarFrequency", BzjUtil.parseRadarFrequency(hexData));
        entity.setRadarFrequency(BzjUtil.parseRadarFrequency(hexData));

        // 播种幅宽
        entity.setSowingWidth(BzjUtil.parseTotalSowingWidth(hexData));

        // A播种株距
        entity.setASowInterval(BzjUtil.parsePlantSpacingA(hexData));
        

        // A减速比
        entity.setAReductionRatio(BzjUtil.parseReductionRatioA(hexData));

        // A孔盘数
        entity.setAHolesNum(BzjUtil.parseHoleDiscCountA(hexData));

        // A给脉冲
        entity.setASendPulse(BzjUtil.parsePulsesPerRevolutionA(hexData));

        // A反馈脉冲
        entity.setARecievePulse(BzjUtil.parseFeedbackPulsesA(hexData));

        // B播种株距
        entity.setBSowInterval(BzjUtil.parsePlantSpacing(hexData));

        // B减速比
        entity.setBReductionRatio(BzjUtil.parseReductionRatio(hexData));

        // B孔盘数
        entity.setBHolesNum(BzjUtil.parseHoleDiscCount(hexData));

        // B给脉冲
        entity.setBSendPulse(BzjUtil.parsePulsesPerRevolution(hexData));

        // B反馈脉冲
        entity.setBRecievePulse(BzjUtil.parseFeedbackPulses(hexData));

        // 急停阈值
        entity.setDecelerationValue(BzjUtil.parseEmergencyStopThreshold(hexData));

        // 预判时间
        entity.setPredictedTime(BzjUtil.parseSeedPredictTime(hexData));

        // 主肥断流
        entity.setLackFertilizerLine(BzjUtil.parseMainFertBreakLine(hexData));

        // 主肥堵塞
//        map.put("mainFertilizerBlockage", BzjUtil.parseMainFertBlockLine(hexData));

        // 主肥系数
        entity.setMainFertilizerRate(BzjUtil.parseMainFertSowingCoeffA(hexData));

        // 主肥测试
        entity.setRealMainFertilizerValue(BzjUtil.parseMainFertTestQuantity(hexData));

        // 主肥设定（需要根据实际需求实现）
        entity.setSetMainFertilizerValue(null);

        // 口肥系数
        entity.setDeputyFertilizerRate(BzjUtil.parseMouthFertSowingCoeffA(hexData));

        // 口肥测试
        entity.setRealDeputyFertilizerValue(BzjUtil.parseMouthFertTestQuantity(hexData));

        // 系统电压
        entity.setSystemVoltage(BzjUtil.parseSystemVoltage(hexData));

        // 系统气压
        entity.setFanPressure(BzjUtil.parseFanPressure(hexData));

        // 风机转速
        entity.setFanSpeed(BzjUtil.parseFanSpeed(hexData));

        // 限制里程
        entity.setRemainingMileage(BzjUtil.parseRemainingDistance(hexData));
        
        //TODO 播种形状(里边很多属性）
//        map.put("sowingShape", BzjUtil.parseStatusFlags(hexData));
        
        String flagsHex = hexData.substring(342, 344);
        int value = Integer.parseInt(flagsHex, 16);

        entity.setMainFertilizerMonitoringFlag(value & 0x80);
        entity.setMainFertilizerMotorFlag(value & 0x40);
        entity.setDeputyFertilizerMotorFlag(value & 0x20);
//        statusMap.put("seedLevel", (value & 0x10) == 0 ? "取消" : "启用");       // 第4位
//        statusMap.put("mainFertLevel", (value & 0x08) == 0 ? "取消" : "启用");   // 第5位
//        statusMap.put("mouthFertLevel", (value & 0x04) == 0 ? "取消" : "启用");  // 第6位
        entity.setSoundFlag(value & 0x02);
        entity.setSowType(value & 0x01);
        

        // 生成24行数据列表
        List<DataElectricSeederMessageLineEntity> seedData = new ArrayList<>();
        for (int line = 1; line <= entity.getSowLine(); line++) {
//            String mainFertRatio = BzjUtil.parseMainFertRatio(hexData, line, mainFertMonitor, mainFertMotor);
//            String calcBreakLine = BzjUtil.parseCalcBreakLine(hexData, line, mainFertRatio);
//            String calcBlockLine = BzjUtil.parseCalcBlockLine(mainFertRatio);

            DataElectricSeederMessageLineEntity row = new DataElectricSeederMessageLineEntity();
            row.setLineNo(line);
            row.setSeedSwtich(BzjUtil.parseSeedEnable(hexData, line));
            row.setSeedAbType(BzjUtil.parseABSelection(hexData, line));
            row.setSeedNum(BzjUtil.parseSeedQuantity(hexData, line));
            row.setExcessSeeds(BzjUtil.parseMultiPercentage(hexData, line));
            row.setLackSeeds(BzjUtil.parseSowingPercentage(hexData, line));
            row.setMainFertilizerMonitorSwtich(BzjUtil.parseMainFertMonitor(hexData, line));
            row.setMainFertilizerSwtich(BzjUtil.parseMainFertMotor(hexData, line));
            row.setMainFertilizerDirect(BzjUtil.parseMotorDirection(hexData, line));
            row.setFertilizerAlarmRatio(null);
            row.setDeputyFertilizerSwtich(BzjUtil.parseMouthFertMotor(hexData, line));
            row.setDeputyFertilizerDirect(BzjUtil.parseMouthMotorDirection(hexData, line));
            //TODO 报警记录，待议
//            row.setSeedAlarmRatio(BzjUtil.parseSeedAlarm(hexData, line));
            
            //TODO 解析肥料报警记录
//            row.setFertilizerAlarmRatio(BzjUtil.parseFertAlarm(hexData, line));
            
//            row.setMainFertRatio(mainFertRatio);
//            row.setCalcBreakLine(calcBreakLine);
//            row.setCalcBlockLine(calcBlockLine);
            seedData.add(row);
        }
        entity.setLines(seedData);
        
        return entity;
	}
	
	
	
	
	
	/**
	 * 新电驱
	 * @param hexStr
	 * @return
	 */
	public static DataElectricSeederMessageEntity analysisElectricHexStr(String hexStr) {
		
		
		DataElectricSeederMessageEntity entity = new DataElectricSeederMessageEntity();
		
		
//		==========设置数据============
//		电子齿轮比，单位。范围1-255;8位数****352-353
		entity.setElectronicGearRatio(subHexStr(hexStr,352,354));
//		孔盘数。单位个，范围20-255;8位数****354-355
		entity.setBHolesNum(subHexStr(hexStr,354,356));
//		株距。单位mm，范围30-1000;16位数 ****356-359
		entity.setBSowInterval(subHexStr(hexStr,356,360));
//		电机与主轴的减速比，单位0.1，16位数 ****360-363
		entity.setBReductionRatio(subHexStr(hexStr,360,364));
//		电机一圈需要的脉冲数，单位个，16位数****364-367
		entity.setBSendPulse(subHexStr(hexStr,364,368));
//		电机一圈反馈多少脉冲，单位个，16位数****368-371
		entity.setBRecievePulse(subHexStr(hexStr, 368, 372));
//		A组：
//		8个bit位，
//		8位=0有编码器
		entity.setNewHeadRepeatFlag(subHexStr(hexStr, 372, 373, 0));
//		7位=行程开关方向取反控制，
		entity.setDrivingDirectionFlag(subHexStr(hexStr, 372, 373, 1));
//		6位=编码器方向取反，
		entity.setEncoderModeFlag(subHexStr(hexStr, 372, 373, 2));
//		5位=1是种电机报警=0时；=0是种电机报警=1时；
		entity.setSeedMotorAlarmFlag(subHexStr(hexStr, 372, 373, 3));
//		4位=1是物联已连接****372-373       
//		3位=1是限制距离到强制停机；
		entity.setShutDownDistanceFlag(subHexStr(hexStr, 373, 374, 1));
//		2位=1是新头重复;
		entity.setNewHeadRepeatFlag(subHexStr(hexStr, 373, 374, 2));
//		1位=1多种的百分比=0缺种的百分比；
//		孔盘数。单位个，范围20-255;8位数****374-375
		entity.setAHolesNum(subHexStr(hexStr,374,376));
//		株距。单位mm，范围30-1000;16位数 ****376-379
		entity.setASowInterval(subHexStr(hexStr,376,380));
//		电机与主轴的减速比，单位0.1，16位数 ****380-383
		entity.setAReductionRatio(subHexStr(hexStr,380,384));
//		电机一圈需要的脉冲数，单位个，16位数****384-387
		entity.setASendPulse(subHexStr(hexStr,384,388));
//		电机一圈反馈多少脉冲，单位个，16位数****388-391
		entity.setARecievePulse(subHexStr(hexStr, 388, 392));
		
//		测试主肥量，16位数****392-395
		entity.setRealMainFertilizerValue(subHexStr(hexStr,392,396));
//		主肥监控的缺肥灵敏度****396-397
		entity.setLackFertilizerRate(subHexStr(hexStr,396,398));
//		主肥的系数，8位****398-399
		entity.setMainFertilizerRate(subHexStr(hexStr,398,400));
//		口肥的系数，8位****400-401
		entity.setDeputyFertilizerRate(subHexStr(hexStr,400,402));
//		设定主肥量，16位数****402-405
		entity.setSetMainFertilizerValue(subHexStr(hexStr,402,406));
//
//		bit设置,
//		8位数=1-平行播种;
		entity.setSowType(subHexStr(hexStr,406,407,0));
//		7=1报声音；
		entity.setSoundFlag(subHexStr(hexStr,406,407,1));
//		6位=0口肥物位取消；
		entity.setDeputyFertilizerLocationFlag(subHexStr(hexStr,406,407,2));
//		5位=0主肥物位取消；
		entity.setMainFertilizerLocationFlag(subHexStr(hexStr,406,407,3));
//		4位=0种物位取消；
		entity.setSeedLocationFlag(subHexStr(hexStr,407,408,0));
//		3位=0口肥电机取消****406-407
		entity.setDeputyFertilizerMotorFlag(subHexStr(hexStr,407,408,1));
//		2位=0主肥电机取消；
		entity.setMainFertilizerMotorFlag(subHexStr(hexStr,407,408,2));
//		1位=0主肥监控取消；
		entity.setMainFertilizerMonitoringFlag(subHexStr(hexStr,407,408,3));
//		播种苗带选择；=0是单苗带，=1是双苗带，=2是三苗带；****408-409
		entity.setSowMode(subHexStr(hexStr,408,410));
//		播种行数，范围1-24;，8位****410-411
		entity.setSowLine(subHexStr(hexStr,410,412));
//		总播种宽度，单位mm，范围300-30000;16位数****412-415
		entity.setSowingWidth(subHexStr(hexStr,412,416));
//		种预判时间，单位ms，,8位;****416-417
		entity.setPredictedTime(subHexStr(hexStr,416,418));
//		急停阈值,8位;****418-419
		entity.setDecelerationValue(subHexStr(hexStr,418,420));
//		无电机主肥监控的缺肥底线****420-421
		entity.setLackFertilizerLine(subHexStr(hexStr,420,422));
//
//		口肥测试肥量，16位****428-431
		entity.setRealDeputyFertilizerValue(subHexStr(hexStr,428,432));
//		口肥设定肥量，16位****432-435
		entity.setSetDeputyFertilizerValue(subHexStr(hexStr,432,436));
//
//		单次播种运行时记录的运行时间，启动清零，运行++，单位1秒；，16位****472-475
		entity.setRunningTime(subHexStr(hexStr,472,476));
//		单次播种的距离显示，单位1m；最大42.9km；，16位****476-479
		entity.setSowDistance(subHexStr(hexStr,476,480));
//		雷达频率；偏移256,8位;****480-481
		entity.setRadarFrequency(subHexStr(hexStr,480,482));
//		运行8秒时系统电压,单位0.1v，16位;****482-485
		entity.setSystemVoltage(subHexStr(hexStr,482,486));
//		系统8bit的状态显示,8位;****486-487
//		8#=1雷达报警；
		entity.setSystemRadarWarnFlag(subHexStr(hexStr,486,487,0));
//		7#=1-屏连接；
		entity.setPadConnectFlag(subHexStr(hexStr,486,487,1));
//		6#=1-播种中；
		entity.setSowingFlag(subHexStr(hexStr,486,487,2));
//		5#=1-是有编码器时，编码器不好使报警；
		entity.setEncoderAlarmFlag(subHexStr(hexStr,486,487,3));
//		4#=1-主肥测试手动中；
		entity.setSystemMainFertilizerTestingFlag(subHexStr(hexStr,487,488,0));
//		3#=1-种手动中；
		entity.setSystemSeedManuallyFlag(subHexStr(hexStr,487,488,1));
//		2#=1-压行程；
		entity.setSystemCompressionStrokeFlag(subHexStr(hexStr,487,488,2));
//		1#=1-有新头；
		entity.setSystemNewHeadFlag(subHexStr(hexStr,487,488,3));
//
//		单次播种，种子报警次数,8位;****488-489
		entity.setSeedAlarmCount(subHexStr(hexStr,488,490));
//		单次播种，肥报警次数，最大200次****562-563
		entity.setFertilizerAlarmCount(subHexStr(hexStr,562,564));
//		运行8秒时风机压力,单位0.01kpa；16位	****564-567
		entity.setFanPressure(subHexStr(hexStr,564,568));
//		运行8秒时风机转速，单位r/min；16位****568-571
		entity.setFanSpeed(subHexStr(hexStr,568,572));
//		物联上传数据类型，****572
//		=0是蓝牙连接时上传数据，=1是运行停止时的上传数据,=2是物联控制后上传数据=3是设置改变上传数据	
		entity.setUploadType(subHexStr(hexStr,572,573));
//		当前到限里程停止时的剩余路程，4095是无限里程，单位是512米****573-575
		entity.setRemainingMileage(subHexStr(hexStr,573,576));
	
		
		
		List<DataElectricSeederMessageLineEntity> lines =  analysisHexStr(hexStr,entity.getSowLine());
		
		entity.setLines(lines);
		
		
		return entity;
	}
	
	/**
	 * 分析
	 * @param hexStr
	 * @param sowLine
	 * @return
	 */
	private static List<DataElectricSeederMessageLineEntity> analysisHexStr(String hexStr, Integer sowLine) {
		
		List<DataElectricSeederMessageLineEntity> lines = new ArrayList<>();
		
		for(int i = 1;i<=sowLine;i++) {
			
			DataElectricSeederMessageLineEntity entity = new DataElectricSeederMessageLineEntity();
			
			
//			每通道16位数, 单趟种数，****64-159;
			entity.setSeedNum(subHexStr(hexStr, 64+(i-1)*4, 68+(i-1)*4));
//			每通道8位数,单趟多种百分比****160-207;
			entity.setExcessSeeds(subHexStr(hexStr, 160+(i-1)*2, 162+(i-1)*2));
//			每通道8位数,单趟缺种百分比****208-255
			entity.setExcessSeeds(subHexStr(hexStr, 208+(i-1)*2, 210+(i-1)*2));
//			每通道8位数,上传的主肥检测转速反馈比****256-303
			entity.setExcessSeeds(subHexStr(hexStr, 256+(i-1)*2, 258+(i-1)*2));
//TODO			统计种报警的通道号（1-24高5位）和报警号（0-7低3位）共24个记录,8位数,****304-351
//			A,B选择,24位****422-427
			entity.setSeedAbType(subHexStr(hexStr, 422, 428, i-1));
//			主肥电机方向,24位****436-441
			entity.setSeedAbType(subHexStr(hexStr, 436, 442, i-1));
//			口肥电机方向,24位****442-447
			entity.setSeedAbType(subHexStr(hexStr, 442, 448, i-1));
//			种使能,24位****448-453
			entity.setSeedAbType(subHexStr(hexStr, 448, 454, i-1));
//			主肥电机使能,24位****454-459
			entity.setSeedAbType(subHexStr(hexStr, 454, 460, i-1));
//			口肥电机使能,24位****460-465
			entity.setSeedAbType(subHexStr(hexStr, 460, 466, i-1));
//			主肥监控使能,24位****466-471
			entity.setSeedAbType(subHexStr(hexStr, 466, 472, i-1));
//TODO			统计肥报警的通道号（1-24高5-9位）和报警号（0-14低4位）共24个记录，12位数,****490-561

			
			lines.add(entity);
		}
		return lines;
	}

	/**
	 * 单位截取
	 * @param hexStr 被截取的16位字符串
	 * @param startIndex 起始下标
	 * @param endIndex 结束下标
	 * @param bitIndex 位下标
	 * @return
	 */
	private static Integer subHexStr(String hexStr, int startIndex, int endIndex, int bitIndex) {
		// 参数校验
	    if (hexStr == null || hexStr.isEmpty()) return null;
	    if (startIndex < 0 || endIndex >= hexStr.length() || startIndex > endIndex) return null;
	    if (bitIndex < 0 || bitIndex > 3) return null;  // 每个HEX字符只有4位(0-3)
	    
	    // 截取十六进制子串
	    String subHex = hexStr.substring(startIndex, endIndex + 1);
	    if (subHex.isEmpty()) return null;
	    
	    // 转换为二进制字符串（自动补零）
	    StringBuilder binaryBuilder = new StringBuilder();
	    for (char hexChar : subHex.toCharArray()) {
	        // 转换单个HEX字符为4位二进制
	        int decimal = Character.toUpperCase(hexChar) - 'A' >= 0 && Character.toUpperCase(hexChar) - 'A' <= 5 
	                ? hexChar - '7'  // A-F
	                : hexChar - '0'; // 0-9
	        
	        // 转换为4位二进制并补零
	        String bin = Integer.toBinaryString(decimal);
	        binaryBuilder.append(String.format("%4s", bin).replace(' ', '0'));
	    }
	    
	    // 验证位索引
	    String fullBinary = binaryBuilder.toString();
	    if (bitIndex >= fullBinary.length()) return null;
	    
	    // 提取指定位
	    return (fullBinary.charAt(bitIndex) & 1) == 1 ? 1 : 0;
	}

	
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


	public static DataElectricSeederMessageEntity transToEntity(String hexData) {
		
		//新规则解析的对象
		DataElectricSeederMessageEntity entity = analysisElectricHexStr(hexData);
		//老规则解析的对象
		DataElectricSeederMessageEntity oldEntity = oldAnalysisElectricHexStr(hexData);
		
		//分辨数据的角度,检测新的，不满足就返回老的
		//1、行数不可能为0
		Integer sowLine = entity.getSowLine();
		//2、播种宽度不可能为0
		Integer sowingWidth = entity.getSowingWidth();
		//3、给脉冲和反脉冲不可能是0
		Integer aSendPulse = entity.getASendPulse();
		Integer bSendPulse = entity.getBSendPulse();
		Integer aRecievePulse = entity.getARecievePulse();
		Integer bRecievePulse = entity.getBRecievePulse();
		//4、
		
		
		if(
				 sowLine == 0 
				 ||sowLine > 24
				 ||sowingWidth == 0
				 ||(aSendPulse==0&&bSendPulse==0)
				 ||(aRecievePulse==0&&bRecievePulse==0)
				 
				) {
			return oldEntity;
		}
		
		
//		System.out.println(sowLine+","+sowLine2);
		
		return entity;
	}

}