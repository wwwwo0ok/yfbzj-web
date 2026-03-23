package com.company.project.strategy.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.springframework.stereotype.Component;

import com.company.project.entity.DataAlarmEntity;
import com.company.project.entity.DataElectricSeederMessageEntity;
import com.company.project.entity.DataElectricSeederMessageLineEntity;
import com.company.project.strategy.CodeReadStrategy;
import com.company.project.util.DigitalTrans;

@Component("h25yYLMqP93")
public class MachineReader2026First implements CodeReadStrategy{
	
	//从物联网和数据库获得
	private String code = "h25yYLMqP93";

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public DataElectricSeederMessageEntity readCode(String code) {
    	return analysisMachineHexStr(code);
	}
	
	public DataElectricSeederMessageEntity analysisMachineHexStr(String hexStr) {
		
		DataElectricSeederMessageEntity entity = new DataElectricSeederMessageEntity();
		
		
		
		
		
		// WLF[64]-（范围0-15）0=播种机；监控器默认
        // WLF[65]-（范围0-15）0=监控器；监控器默认
        // WLF[66]-（范围0-15）0=开机或重连传数；1=播种结束传数；3=设置传数；
        String hexStr2 = hexStr.substring(66-64, 67-64);
        String binaryStr2 = DigitalTrans.hexStringToBinary(hexStr2);
        int num2 = DigitalTrans.binaryToAlgorism(binaryStr2);
        entity.setUploadType(num2);
        // WLF[67]-（范围0-15）0=玉米,1=大豆,2=高粱,3=油菜,4=向日葵,5=棉花,6=甜菜,7=花生,8=其他(屏设置)
        String hexStr3 = hexStr.substring(67-64, 68-64);
        String binaryStr3 = DigitalTrans.hexStringToBinary(hexStr3);
        int num3 = DigitalTrans.binaryToAlgorism(binaryStr3);
        entity.setData1(Integer.toString(num3));
        
        // WLF[68]-（范围0-15）=0是单行播种，=1是大垄2行播种，=2是大垄3行播种/(屏设置)
        String hexStr4 = hexStr.substring(68-64, 69-64);
        String binaryStr4 = DigitalTrans.hexStringToBinary(hexStr4);
        int num4 = DigitalTrans.binaryToAlgorism(binaryStr4);
        entity.setSowType(num4);
        // WLF[69]-WLF[70]-（范围0-255）=播种行数(屏设置)
        String hexStr5 = hexStr.substring(69-64, 71-64);
        String binaryStr5 = DigitalTrans.hexStringToBinary(hexStr5);
        int num5 = DigitalTrans.binaryToAlgorism(binaryStr5);
        entity.setSowLine(num5);
        // 播种机肥设定 WLF[71]-WLF[72]-（范围0-255）=总使能开关位(屏设置)
        String hexStr6 = hexStr.substring(71-64, 73-64);
        String binaryStr6 = DigitalTrans.hexStringToBinary(hexStr6);
        int bits1 = Integer.parseInt(binaryStr6.substring(7));
        int bits2 = Integer.parseInt(binaryStr6.substring(6, 7));
        int bits3 = Integer.parseInt(binaryStr6.substring(5, 6));
        int bits4 = Integer.parseInt(binaryStr6.substring(4, 5));
        int bits5 = Integer.parseInt(binaryStr6.substring(3, 4));
        int bits6 = Integer.parseInt(binaryStr6.substring(2, 3));
        int bits7 = Integer.parseInt(binaryStr6.substring(1, 2));
        entity.setSeedFlag(bits1);
        entity.setMainFertilizerMonitoringFlag(bits2);
        entity.setMainFertilizerMotorFlag(bits3);
        entity.setDeputyFertilizerMotorFlag(bits4);
        entity.setSeedLocationFlag(bits5);
        entity.setMainFertilizerLocationFlag(bits6);
        entity.setDeputyFertilizerLocationFlag(bits7);
        
        
        // WLF[73]-WLF[75]-（范围0-1000）=播种株距;(屏设置)  
        String hexStr7 = hexStr.substring(73-64, 76-64);
        String binaryStr7 = DigitalTrans.hexStringToBinary(hexStr7);
        int num7 = DigitalTrans.binaryToAlgorism(binaryStr7);
        entity.setASowInterval(num7);
        
        // WLF[76]-WLF[79]-（范围0-65535）=播种幅宽；(20-16383mm);(屏设置)  
        String hexStr8 = hexStr.substring(76-64, 80-64);
        String binaryStr8 = DigitalTrans.hexStringToBinary(hexStr8);
        int num8 = DigitalTrans.binaryToAlgorism(binaryStr8);
        entity.setSowingWidth(num8);
        
        // WLF[80]-WLF[83]-（范围0-65535）=主肥设定量kg;(屏设置) 
        String hexStr9 = hexStr.substring(80-64, 84-64);
        String binaryStr9 = DigitalTrans.hexStringToBinary(hexStr9);
        int num9 = DigitalTrans.binaryToAlgorism(binaryStr9);
        entity.setSetMainFertilizerValue(num9);
        // WLF[84]-WLF[87]-（范围0-65535）=主肥反馈量g;(屏设置) 
        String hexStr10 = hexStr.substring(84-64, 88-64);
        String binaryStr10 = DigitalTrans.hexStringToBinary(hexStr10);
        int num10 = DigitalTrans.binaryToAlgorism(binaryStr10);
        entity.setRealMainFertilizerValue(num10);
        // WLF[88]-WLF[91]-（范围0-65535）=口肥设定量kg;(屏设置) 
        String hexStr11 = hexStr.substring(88-64, 92-64);
        String binaryStr11 = DigitalTrans.hexStringToBinary(hexStr11);
        int num11 = DigitalTrans.binaryToAlgorism(binaryStr11);
        entity.setSetDeputyFertilizerValue(num11);
        
        // WLF[92]-WLF[95]-（范围0-65535）=口肥反馈量g;(屏设置)
        String hexStr12 = hexStr.substring(92-64, 96-64);
        String binaryStr12 = DigitalTrans.hexStringToBinary(hexStr12);
        int num12 = DigitalTrans.binaryToAlgorism(binaryStr12);
        entity.setRealDeputyFertilizerValue(num12);


        // WLF[96]，WLF[97]-（范围0-255）=主肥比例，160是100%，(屏设置)
        String hexStr13 = hexStr.substring(96-64, 98-64);
        String binaryStr13 = DigitalTrans.hexStringToBinary(hexStr13);
        int num13 = DigitalTrans.binaryToAlgorism(binaryStr13);
        entity.setMainFertilizerRate(num13);
        // WLF[98]，WLF[99]-（范围0-255）=口肥比例，160是100%;(屏设置)
        String hexStr14 = hexStr.substring(98-64, 100-64);
        String binaryStr14 = DigitalTrans.hexStringToBinary(hexStr14);
        int num14 = DigitalTrans.binaryToAlgorism(binaryStr14);
        entity.setDeputyFertilizerRate(num14);


        // WLF[100]，WLF[101]-（范围0-255）=无主肥电机时，上位机给的主肥监控检测缺肥的速度，单位0.1km/h(屏设置)
        String hexStr15 = hexStr.substring(100-64, 102-64);
        String binaryStr15 = DigitalTrans.hexStringToBinary(hexStr15);
        int num15 = DigitalTrans.binaryToAlgorism(binaryStr15);
        entity.setData2(Integer.toString(num15));
        // WLF[102]，WLF[103]-（范围0-255）=主肥监控的缺肥报警灵敏度0-63,初始值=32，0是取消缺肥报警(屏设置)
        String hexStr102 = hexStr.substring(102-64, 104-64);
        String binaryStr102 = DigitalTrans.hexStringToBinary(hexStr102);
        int num102 = DigitalTrans.binaryToAlgorism(binaryStr102);
        entity.setLackFertilizerRate(num102);
        // WLF[104]-WLF[107]-（范围0-65535）=从开机后的播种趟数（计算）
        String hexStr104 = hexStr.substring(104-64, 108-64);
        String binaryStr104 = DigitalTrans.hexStringToBinary(hexStr104);
        int num104 = DigitalTrans.binaryToAlgorism(binaryStr104);
        entity.setData3(Integer.toString(num104));
        
        
        
        
        
        
        // WLF[108]-WLF[111]-（范围0-65535）=单趟的播种时间（单位0.6秒）（计算）
        String hexStr16 = hexStr.substring(108-64, 112-64);
        String binaryStr16 = DigitalTrans.hexStringToBinary(hexStr16);
        int num16 = DigitalTrans.binaryToAlgorism(binaryStr16);
        entity.setRunningTime(num16);
        // WLF[112]，WLF[113]-（范围0-255）=单趟的使能播种行数（计算）
        String hexStr17 = hexStr.substring(112-64, 114-64);
        String binaryStr17 = DigitalTrans.hexStringToBinary(hexStr17);
        int num17 = DigitalTrans.binaryToAlgorism(binaryStr17);
        // WLF[120]-WLF[123]-（范围0-65535）=单趟的平均播种速度（m/h）（计算）
        String hexStr18 = hexStr.substring(120-64, 124-64);
        String binaryStr18 = DigitalTrans.hexStringToBinary(hexStr18);
        int num18 = DigitalTrans.binaryToAlgorism(binaryStr18);
        entity.setData4(Integer.toString(num18));
        
        
        // WLF[124]-WLF[127]-（范围0-65535）=单趟的行走长度（m）（计算）
        String hexStr19 = hexStr.substring(124-64, 128-64);
        String binaryStr19 = DigitalTrans.hexStringToBinary(hexStr19);
        int num19 = DigitalTrans.binaryToAlgorism(binaryStr19);
        entity.setSowDistance(num19);
        
       
        

        //TODO WLF[266]-（范围0-15）=1位，=1是系统强制停机，2位，=1是主肥电机运行，3位，=1是口肥电机运行

 

        //TODO WLF[286]，WLF[287]-（范围0-255）=主肥监控报警次数，最多63个（计算）
        String hexStr47 = hexStr.substring(286-64, 288-64);
        String binaryStr47 = DigitalTrans.hexStringToBinary(hexStr47);
        int num47 = DigitalTrans.binaryToAlgorism(binaryStr47);
//        entity.setFertilizerAlarmCount(num47);


        //WLF[328]，WLF[329]-（范围0-255）=主肥电机报警次数，最多63个（计算） 
        String hexStr68 = hexStr.substring(328-64, 330-64);
        String binaryStr68 = DigitalTrans.hexStringToBinary(hexStr68);
        int num68 = DigitalTrans.binaryToAlgorism(binaryStr68);
//        entity.setFertilizerAlarmCount(num68);
        

        //WLF[338]，WLF[339]-（范围0-255）=口肥电机报警次数，最多63个（计算）
        String hexStr73 = hexStr.substring(338-64, 340-64);
        String binaryStr73 = DigitalTrans.hexStringToBinary(hexStr73);
        int num73 = DigitalTrans.binaryToAlgorism(binaryStr73);

        

        //WLF[348]-WLF[419]-（范围0-65535）=每一个主肥监控491ms的通道值.18通道
        String hexStr348 = hexStr.substring(348-64, 420-64);
        String binaryStr348 = DigitalTrans.hexStringToBinary(hexStr348);
        int num348 = DigitalTrans.binaryToAlgorism(binaryStr348);


        
        List<DataElectricSeederMessageLineEntity> lineEntities = new ArrayList<>();
		List<DataAlarmEntity> alarmEntities = new ArrayList<>();
		
		entity.setLines(lineEntities);
		entity.setAlarms(alarmEntities);
		
        for(int i = 0;i<entity.getSowLine();i++) {
			DataElectricSeederMessageLineEntity lineEntity = new DataElectricSeederMessageLineEntity(); 
			lineEntity.setLineNo(i+1);
			
			lineEntities.add(lineEntity);
		}
        //WLF[420]-WLF[425]-（范围0-0xffffff）=按位给主肥电机方向，18通道(屏设置)
        String hexStr420 = hexStr.substring(420-64, 426-64);
        String binaryStr420 = DigitalTrans.hexStringToBinary(hexStr420);
        setLineDataByByte(binaryStr420,lineEntities,DataElectricSeederMessageLineEntity::setDeputyFertilizerSwtich);
        
        //WLF[426]-WLF[431]-（范围0-0xffffff）=按位给口肥电机方向，18通道(屏设置)
        String hexStr426 = hexStr.substring(426-64, 432-64);
        String binaryStr426 = DigitalTrans.hexStringToBinary(hexStr426);
        setLineDataByByte(binaryStr426,lineEntities,DataElectricSeederMessageLineEntity::setDeputyFertilizerSwtich);
        //WLF[128]-WLF[217]-（范围0-1048575）=转换设定行数的每行种数共18行（每行种数播种开始时由种头清零）0-1048575
        String hexStr128 = hexStr.substring(128-64, 218-64);
        setLineData(hexStr128,lineEntities,DataElectricSeederMessageLineEntity::setSeedNum);

        //WLF[218]-WLF[223]-（范围0-0xffffff）=按位给种子监控使能，18通道(屏设置)
        String hexStr218 = hexStr.substring(218-64, 224-64);
		String binaryStr218 = DigitalTrans.hexStringToBinary(hexStr218);
        setLineDataByByte(binaryStr218,lineEntities,DataElectricSeederMessageLineEntity::setSeedSwtich);
        //WLF[267]-WLF[272]-（范围0-0xffffff）=按位给主肥监控使能，18通道(屏设置)
        String hexStr267 = hexStr.substring(267-64, 272-64);
		String binaryStr267 = DigitalTrans.hexStringToBinary(hexStr267);
        setLineDataByByte(binaryStr267,lineEntities,DataElectricSeederMessageLineEntity::setMainFertilizerMonitorSwtich);

        //WLF[272]-WLF[277]-（范围0-0xffffff）=按位给主肥电机使能，18通道(屏设置)
        String hexStr272 = hexStr.substring(272-64, 278-64);
		String binaryStr272 = DigitalTrans.hexStringToBinary(hexStr272);
        setLineDataByByte(binaryStr272,lineEntities,DataElectricSeederMessageLineEntity::setMainFertilizerSwtich);
        
        //WLF[278]-WLF[283]-（范围0-0xffffff）=按位给口肥电机使能，18通道(屏设置)
        String hexStr278 = hexStr.substring(278-64, 284-64);
		String binaryStr278 = DigitalTrans.hexStringToBinary(hexStr278);
        setLineDataByByte(binaryStr278,lineEntities,DataElectricSeederMessageLineEntity::setDeputyFertilizerSwtich);
        
      //WLF[288]-WLF[327]-（范围0-255）=转换主肥监控报警行号（高5位），原因（低3位）到物联平台，记录最近的20条
        String hexStr288 = hexStr.substring(288-64, 328-64);
        analysisAlarm(hexStr288,alarmEntities,20,DataAlarmEntity.FERT_MONITORING_TYPE_);
        //WLF[226]-WLF[265]-（范围0-255）=转换种报警行号（高5位），原因（低3位）到物联平台，记录最近的20条
        String hexStr226 = hexStr.substring(226-64, 266-64);
        analysisAlarm(hexStr226,alarmEntities,20,DataAlarmEntity.SEED_ALARM_TYPE);
        //WLF[330]-WLF[337]-（范围0-255）=转换主肥电机报警行号（高5位），原因（低3位）到物联平台，记录最近的4条
        String hexStr330 = hexStr.substring(330-64, 338-64);
        analysisAlarm(hexStr330,alarmEntities,4,DataAlarmEntity.MAIN_FERT_ALARM_TYPE);
        //WLF[340]-WLF[347]-（范围0-255）=转换口肥电机报警行号（高5位），原因（低3位）到物联平台，记录最近的4条
        String hexStr340 = hexStr.substring(340-64, 348-64);
        analysisAlarm(hexStr340,alarmEntities,4,DataAlarmEntity.DEPUTY_FERT_ALARM_TYPE);
        
        return entity;
	}
	
	
	

	/**
	 * 解析报警
	 * @param hexStr 串
	 * @param size 长度
	 * @param alarmType 
	 * @return
	 */
	private void analysisAlarm(String hexStr,List<DataAlarmEntity> list, int size, int alarmType) {

		String binaryStr = DigitalTrans.hexStringToBinary(hexStr);
		int length = binaryStr.length()/size;
		
		for(int i = 0;i<size;i++) {
			
			
			String substring = binaryStr.substring(i,i+length);
			
			String high5Bits22 = substring.substring(0, 5);//高5位 行号
	        String low3Bits22 = substring.substring(5);//低3位 编号
			
			int lineNo = DigitalTrans.binaryToAlgorism(high5Bits22);
			int no = DigitalTrans.binaryToAlgorism(low3Bits22);
			
			if(lineNo != 0) {
				DataAlarmEntity entity = new DataAlarmEntity();
				
				entity.setLineNo(lineNo);
				entity.setAlarmType(alarmType);
				entity.setCode(Integer.toString(no));
				
				list.add(entity);
			}
			
		}
	}

	private void setLineDataByByte(String hexStr, List<DataElectricSeederMessageLineEntity> lineEntities,
			BiConsumer<DataElectricSeederMessageLineEntity, Integer> setter) {
		int size = lineEntities.size();
		int length = hexStr.length()/size;
		
		for(int i = 0;i<size;i++) {
			String substring = hexStr.substring(i,i+length);
			int num = DigitalTrans.binaryToAlgorism(substring);
			setter.accept(lineEntities.get(i), num);
		}
	}

	private void setLineData(String hexStr, List<DataElectricSeederMessageLineEntity> lineEntities, 
			BiConsumer<DataElectricSeederMessageLineEntity, Integer> setter) {
		
		int size = lineEntities.size();
		int length = hexStr.length()/size;
		
		for(int i = 0;i<size;i++) {
			
			String substring = hexStr.substring(i,i+length);
			
			String binaryStr = DigitalTrans.hexStringToBinary(substring);
			int num = DigitalTrans.binaryToAlgorism(binaryStr);
			setter.accept(lineEntities.get(i), num);
		}
		
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
		
		String aaString = "0006005FF0C80CB201F42EE0004605DC2B301E2000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001F00000000000000000000000000000000000000000000000100001F00001F0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
		
		MachineReader2026First reader = new MachineReader2026First();
		
		DataElectricSeederMessageEntity analysisMachineHexStr = reader.analysisMachineHexStr(aaString);
		
		
	}

}
