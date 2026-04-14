package com.company.project.strategy.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.company.project.entity.DataAlarmEntity;
import com.company.project.entity.DataElectricSeederMessageEntity;
import com.company.project.entity.DataElectricSeederMessageLineEntity;
import com.company.project.strategy.CodeReadStrategy;
import com.company.project.util.DigitalTrans;

@Component("h25yejp0P5j")
public class ElectronReader2026First implements CodeReadStrategy{

	//从物联网和数据库获得
	private String code = "h25yejp0P5j";

	private static final Integer MAX_LINE_NUMBE_INTEGER = 24;
	
	
	

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void readCode(DataElectricSeederMessageEntity entity) {
		
		analysisElectricHexStr(entity);
	}

	
	/**
	 * 新电驱
	 * @param hexStr
	 * @return
	 */
	public void analysisElectricHexStr(DataElectricSeederMessageEntity entity) {
		
		String hexStr = entity.getAliyun();
		
		List<DataAlarmEntity> alarmEntities = new ArrayList<>();

		entity.setAlarms(alarmEntities);
		
//		==========设置数据============
//		电子齿轮比，单位。范围1-255;8位数****352-353
		entity.setElectronicGearRatio(subHexStr(hexStr,352-64,354-64));
//		孔盘数。单位个，范围20-255;8位数****354-355
		entity.setBHolesNum(subHexStr(hexStr,354-64,356-64));
//		株距。单位mm，范围30-1000;16位数 ****356-359
		entity.setBSowInterval(subHexStr(hexStr,356-64,360-64));
//		电机与主轴的减速比，单位0.1，16位数 ****360-363
		entity.setBReductionRatio(subHexStr(hexStr,360-64,364-64));
//		电机一圈需要的脉冲数，单位个，16位数****364-367
		entity.setBSendPulse(subHexStr(hexStr,364-64,368-64));
//		电机一圈反馈多少脉冲，单位个，16位数****368-371
		entity.setBRecievePulse(subHexStr(hexStr, 368-64, 372-64));
//		A组：
//		8个bit位，
//		8位=0有编码器
		entity.setNewHeadRepeatFlag(subHexStr(hexStr, 372-64, 373-64, 0));
//		7位=行程开关方向取反控制，
		entity.setDrivingDirectionFlag(subHexStr(hexStr, 372-64, 373-64, 1));
//		6位=编码器方向取反，
		entity.setEncoderModeFlag(subHexStr(hexStr, 372-64, 373-64, 2));
//		5位=1是种电机报警=0时；=0是种电机报警=1时；
		entity.setSeedMotorAlarmFlag(subHexStr(hexStr, 372-64, 373-64, 3));
//		4位=1是物联已连接****372-373       
//		3位=1是限制距离到强制停机；
		entity.setShutDownDistanceFlag(subHexStr(hexStr, 373-64, 374-64, 1));
//		2位=1是新头重复;
		entity.setNewHeadRepeatFlag(subHexStr(hexStr, 373-64, 374-64, 2));
//		1位=1多种的百分比=0缺种的百分比；
//		孔盘数。单位个，范围20-255;8位数****374-375
		entity.setAHolesNum(subHexStr(hexStr,374-64,376-64));
//		株距。单位mm，范围30-1000;16位数 ****376-379
		entity.setASowInterval(subHexStr(hexStr,376-64,380-64));
//		电机与主轴的减速比，单位0.1，16位数 ****380-383
		entity.setAReductionRatio(subHexStr(hexStr,380-64,384-64));
//		电机一圈需要的脉冲数，单位个，16位数****384-387
		entity.setASendPulse(subHexStr(hexStr,384-64,388-64));
//		电机一圈反馈多少脉冲，单位个，16位数****388-391
		entity.setARecievePulse(subHexStr(hexStr, 388-64, 392-64));
		
//		测试主肥量，16位数****392-395
		entity.setRealMainFertilizerValue(subHexStr(hexStr,392-64,396-64));
//		主肥监控的缺肥灵敏度****396-397
		entity.setLackFertilizerRate(subHexStr(hexStr,396-64,398-64));
//		主肥的系数，8位****398-399
		entity.setMainFertilizerRate(subHexStr(hexStr,398-64,400-64));
//		口肥的系数，8位****400-401
		entity.setDeputyFertilizerRate(subHexStr(hexStr,400-64,402-64));
//		设定主肥量，16位数****402-405
		entity.setSetMainFertilizerValue(subHexStr(hexStr,402-64,406-64));
//
//		bit设置,
//		8位数=1-平行播种;
		entity.setSowType(subHexStr(hexStr,406-64,407-64,0));
//		7=1报声音；
		entity.setSoundFlag(subHexStr(hexStr,406-64,407-64,1));
//		6位=0口肥物位取消；
		entity.setDeputyFertilizerLocationFlag(subHexStr(hexStr,406-64,407-64,2));
//		5位=0主肥物位取消；
		entity.setMainFertilizerLocationFlag(subHexStr(hexStr,406-64,407-64,3));
//		4位=0种物位取消；
		entity.setSeedLocationFlag(subHexStr(hexStr,407-64,408-64,0));
//		3位=0口肥电机取消****406-407
		entity.setDeputyFertilizerMotorFlag(subHexStr(hexStr,407-64,408-64,1));
//		2位=0主肥电机取消；
		entity.setMainFertilizerMotorFlag(subHexStr(hexStr,407-64,408-64,2));
//		1位=0主肥监控取消；
		entity.setMainFertilizerMonitoringFlag(subHexStr(hexStr,407-64,408-64,3));
//		播种苗带选择；=0是单苗带，=1是双苗带，=2是三苗带；****408-409
		entity.setSowMode(subHexStr(hexStr,408-64,410-64));
//		播种行数，范围1-24;，8位****410-411
		entity.setSowLine(subHexStr(hexStr,410-64,412-64));
//		总播种宽度，单位mm，范围300-30000;16位数****412-415
		entity.setSowingWidth(subHexStr(hexStr,412-64,416-64));
//		种预判时间，单位ms，,8位;****416-417
		entity.setPredictedTime(subHexStr(hexStr,416-64,418-64));
//		急停阈值,8位;****418-419
		entity.setDecelerationValue(subHexStr(hexStr,418-64,420-64));
//		无电机主肥监控的缺肥底线****420-421
		entity.setLackFertilizerLine(subHexStr(hexStr,420-64,422-64));
//
//		口肥测试肥量，16位****428-431
		entity.setRealDeputyFertilizerValue(subHexStr(hexStr,428-64,432-64));
//		口肥设定肥量，16位****432-435
		entity.setSetDeputyFertilizerValue(subHexStr(hexStr,432-64,436-64));
//
//		单次播种运行时记录的运行时间，启动清零，运行++，单位1秒；，16位****472-475
		entity.setRunningTime(subHexStr(hexStr,472-64,476-64));
//		单次播种的距离显示，单位1m；最大42.9km；，16位****476-479
		entity.setSowDistance(subHexStr(hexStr,476-64,480-64));
//		雷达频率；偏移256,8位;****480-481
		entity.setRadarFrequency(subHexStr(hexStr,480-64,482-64));
//		运行8秒时系统电压,单位0.1v，16位;****482-485
		entity.setSystemVoltage(subHexStr(hexStr,482-64,486-64));
//		系统8bit的状态显示,8位;****486-487
//		8#=1雷达报警；
		entity.setSystemRadarWarnFlag(subHexStr(hexStr,486-64,487-64,0));
//		7#=1-屏连接；
		entity.setPadConnectFlag(subHexStr(hexStr,486-64,487-64,1));
//		6#=1-播种中；
		entity.setSowingFlag(subHexStr(hexStr,486-64,487-64,2));
//		5#=1-是有编码器时，编码器不好使报警；
		entity.setEncoderAlarmFlag(subHexStr(hexStr,486-64,487-64,3));
//		4#=1-主肥测试手动中；
		entity.setSystemMainFertilizerTestingFlag(subHexStr(hexStr,487-64,488-64,0));
//		3#=1-种手动中；
		entity.setSystemSeedManuallyFlag(subHexStr(hexStr,487-64,488-64,1));
//		2#=1-压行程；
		entity.setSystemCompressionStrokeFlag(subHexStr(hexStr,487-64,488-64,2));
//		1#=1-有新头；
		entity.setSystemNewHeadFlag(subHexStr(hexStr,487-64,488-64,3));
//
//		单次播种，种子报警次数,8位;****488-489
		entity.setSeedAlarmCount(subHexStr(hexStr,488-64,490-64));
//		单次播种，肥报警次数，最大200次****562-563
		entity.setFertilizerAlarmCount(subHexStr(hexStr,562-64,564-64));
//		运行8秒时风机压力,单位0.01kpa；16位	****564-567
		entity.setFanPressure(subHexStr(hexStr,564-64,568-64));
//		运行8秒时风机转速，单位r/min；16位****568-571
		entity.setFanSpeed(subHexStr(hexStr,568-64,572-64));
//		物联上传数据类型，****572
//		=0是蓝牙连接时上传数据，=1是运行停止时的上传数据,=2是物联控制后上传数据=3是设置改变上传数据	
		entity.setUploadType(subHexStr(hexStr,572-64,573-64));
//		当前到限里程停止时的剩余路程，4095是无限里程，单位是512米****573-575
		entity.setRemainingMileage(subHexStr(hexStr,573-64,576-64));
	

		//TODO			统计肥报警的通道号（1-24高5-9位）和报警号（0-14低4位）共24个记录，12位数,****490-561

        String hexStr490 = hexStr.substring(490-64, 562-64);
        analysisAlarm(hexStr490,alarmEntities,24,DataAlarmEntity.FERT_ALARM_TYPE);
		//TODO			统计种报警的通道号（1-24高5位）和报警号（0-7低3位）共24个记录,8位数,****304-351
        String hexStr304 = hexStr.substring(304-64, 352-64);
        analysisAlarm(hexStr304,alarmEntities,24,DataAlarmEntity.SEED_ALARM_TYPE);
		
		
		List<DataElectricSeederMessageLineEntity> lines =  analysisLineData(hexStr,entity.getSowLine());
		
		entity.setLines(lines);
		
	}
	
	/**
	 * 解析报警
	 * @param hexStr 串
	 * @param size 长度
	 * @param alarmType 
	 * @return
	 */
	private void analysisAlarm(String hexStr, List<DataAlarmEntity> list, int size, int alarmType) {
	    String binaryStr = DigitalTrans.hexStringToBinary(hexStr);
	    int totalLength = binaryStr.length();
	    int length = totalLength / size; // 每个报警块的长度

	    // 检查 totalLength 是否能被 size 整除，否则调整 length 或处理错误
	    if (totalLength % size != 0) {
	        // 可以根据业务需求处理，例如抛出异常或调整 size
	        // 这里简单起见，使用整数除法，但可能丢失数据，建议根据实际情况处理
//	        log.warn("二进制字符串长度 {} 不能被 size {} 整除，可能数据不完整", totalLength, size);
	        // 可选择返回或调整 length
	        // length = totalLength / size; // 整数除法，但可能不准确
	    }

	    for (int i = 0; i < size; i++) {
	        int start = i * length;
	        int end = start + length;
	        // 防止越界
	        if (end > totalLength) {
	            end = totalLength;
	        }
	        String substring = binaryStr.substring(start, end);

	        int lineNo = 0;
	        int no = 0;

	        if (alarmType == DataAlarmEntity.FERT_ALARM_TYPE) {
	            // 确保子串长度足够（至少12位：8位行号 + 4位原因）
	            if (substring.length() < 12) {
//	                log.warn("FERT_ALARM_TYPE 子串长度不足12位，跳过。子串: {}", substring);
	                continue;
	            }
	            String high8Bits = substring.substring(0, 8); // 高8位为行号
	            String low4Bits = substring.substring(8, 12); // 低4位为原因，明确取4位
	            lineNo = DigitalTrans.binaryToAlgorism(high8Bits);
	            no = DigitalTrans.binaryToAlgorism(low4Bits);
	        } else if (alarmType == DataAlarmEntity.SEED_ALARM_TYPE) {
	            // 确保子串长度足够（至少8位：5位行号 + 3位编号）
	            if (substring.length() < 8) {
//	                log.warn("SEED_ALARM_TYPE 子串长度不足8位，跳过。子串: {}", substring);
	                continue;
	            }
	            String high5Bits = substring.substring(0, 5); // 高5位为行号
	            String low3Bits = substring.substring(5, 8); // 低3位为编号，明确取3位
	            lineNo = DigitalTrans.binaryToAlgorism(high5Bits);
	            no = DigitalTrans.binaryToAlgorism(low3Bits);
	        }

	        // 只有行号不为0时才添加报警
	        if (lineNo != 0 && lineNo<= MAX_LINE_NUMBE_INTEGER) {
	            DataAlarmEntity entity = new DataAlarmEntity();
	            entity.setLineNo(lineNo);
	            entity.setAlarmType(alarmType);
	            entity.setCode(no);
	            list.add(entity);
	        }
	    }
	}


	
	/**
	 * 种报警 ,前三行为行数，后边是数字
	 * @param subHexStr
	 * @return
	 */
	private List<DataAlarmEntity> parseSeedAlarm(String hexData) {
		List<DataAlarmEntity> list = new ArrayList<>();
	
		// 总报警次数424-425
	    int totalAlarms = Integer.parseInt(hexData.substring(424, 426), 16);
	    if (totalAlarms == 0) return list;
	    // 每个报警占1字节（地址240-287）
	    for (int i = 0; i < totalAlarms; i++) {
	    	DataAlarmEntity dataAlarmEntity = new DataAlarmEntity();
	        int addr = 240 + i * 2;
	        String alarmHex = hexData.substring(addr, addr + 2);
	        int value = Integer.parseInt(alarmHex, 16);
	        int lineNo = (value >> 3) & 0x1F; // 高5位为行号
	        int reasonCode = value & 0x07;     // 低3位为原因
	        
	        dataAlarmEntity.setLineNo(lineNo);
	        dataAlarmEntity.setAlarmType(DataAlarmEntity.SEED_ALARM_TYPE);
	        dataAlarmEntity.setCode(reasonCode);
	        
	    }
		
		return list;
	}

	/**
	 * 解析肥料报警记录（地址范围：426-497）
	 * @param hexData 十六进制数据字符串
	 * @param line 检测线路编号（1-24）
	 * @return 报警原因组合字符串（格式：原因+报警序号）或"无报警"
	 */
	private static List<DataAlarmEntity>  parseFertAlarm(String hexData) {
		List<DataAlarmEntity> list = new ArrayList<>();
	    // 总报警次数498-499
	    int totalAlarms = Integer.parseInt(hexData.substring(498, 500), 16);
	    if (totalAlarms == 0) return list;
	    // 每个报警占3字节（地址426-497）
	    for (int i = 0; i < totalAlarms; i++) {
	    	DataAlarmEntity dataAlarmEntity = new DataAlarmEntity();
	        int addr = 426 + i * 3;
	        String alarmHex = hexData.substring(addr, addr + 3);
	        int value = Integer.parseInt(alarmHex, 16);
	        int lineNo = (value >> 4) & 0xFF;  // 高8位为行号
	        int reasonCode = value & 0x0F;      // 低4位为原因
	        
	        dataAlarmEntity.setLineNo(lineNo);
//	        dataAlarmEntity.setAlarmType(DataAlarmEntity.FERT_ALARM_TYPE);
	        dataAlarmEntity.setCode(reasonCode);
	    }
	    
	    return list;
	}

	/**
	 * 简单截取
	 * @param hexStr
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	private int subHexStr(String hexStr,int startIndex,int endIndex) {
		
		if(startIndex >= hexStr.length() ) {
			return 0;
		}
		
		return Integer.parseInt(hexStr.substring(startIndex,endIndex),16);
	}
	
	/**
	 * 分析每一行
	 * @param hexStr
	 * @param sowLine
	 * @return
	 */
	private List<DataElectricSeederMessageLineEntity> analysisLineData(String hexStr, Integer sowLine) {
		
		List<DataElectricSeederMessageLineEntity> lines = new ArrayList<>();
		
		for(int i = 1;i<=sowLine;i++) {
			
			DataElectricSeederMessageLineEntity entity = new DataElectricSeederMessageLineEntity();
			
			
//			每通道16位数, 单趟种数，****64-159;
			entity.setSeedNum(subHexStr(hexStr, 64-64+(i-1)*4, 68-64+(i-1)*4));
//			每通道8位数,单趟多种百分比****160-207;
			entity.setExcessSeeds(subHexStr(hexStr, 160-64+(i-1)*2, 162-64+(i-1)*2));
//			每通道8位数,单趟缺种百分比****208-255
			entity.setLackSeeds(subHexStr(hexStr, 208-64+(i-1)*2, 210-64+(i-1)*2));
//			每通道8位数,上传的主肥检测转速反馈比****256-303
			entity.setFertilizerSpeedRatio(subHexStr(hexStr, 256-64+(i-1)*2, 258-64+(i-1)*2));
//			A,B选择,24位****422-427
			entity.setSeedAbType(subHexStr(hexStr, 422-64, 428-64, i-1));
//			主肥电机方向,24位****436-441
			entity.setMainFertilizerDirect(subHexStr(hexStr, 436-64, 442-64, i-1));
//			口肥电机方向,24位****442-447
			entity.setDeputyFertilizerDirect(subHexStr(hexStr, 442-64, 448-64, i-1));
//			种使能,24位****448-453
			entity.setSeedSwtich(subHexStr(hexStr, 448-64, 454-64, i-1));
//			主肥电机使能,24位****454-459
			entity.setMainFertilizerSwtich(subHexStr(hexStr, 454-64, 460-64, i-1));
//			口肥电机使能,24位****460-465
			entity.setDeputyFertilizerSwtich(subHexStr(hexStr, 460-64, 466-64, i-1));
//			主肥监控使能,24位****466-471
			entity.setMainFertilizerMonitorSwtich(subHexStr(hexStr, 466-64, 472-64, i-1));

			entity.setLineNo(i);
			
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
	private Integer subHexStr(String hexStr, int startIndex, int endIndex, int bitIndex) {
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
	
	
	public static void main(String[] args) {
		
		String aaString = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000433B332B231B130000000000000000000000000000000000802800C80078190007D0092800C80078190007D0000A0F000000323F00081450FFFF0FFE0000000A000AFE0000FE00000000FF0000FF0000FF0000FF000900003500EB460708D07D06D05D04D03D02D01D00000000000000000000000000000000000000000000070708070800001FFF";
		
		ElectronReader2026First reader = new ElectronReader2026First();
		
		DataElectricSeederMessageEntity analysisMachineHexStr = new DataElectricSeederMessageEntity();
		
		reader.readCode(analysisMachineHexStr);
		
		
	}
}
