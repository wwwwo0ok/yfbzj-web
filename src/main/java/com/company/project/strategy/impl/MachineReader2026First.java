package com.company.project.strategy.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.company.project.entity.DataAlarmEntity;
import com.company.project.entity.DataElectricSeederMessageEntity;
import com.company.project.entity.DataElectricSeederMessageLineEntity;
import com.company.project.strategy.CodeReadStrategy;
import com.company.project.util.DataAnalysisUtil;
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
		
		
		// 电驱，机械
        String hexStr1 = hexStr.substring(0, 2);
        String binaryStr1 = DigitalTrans.hexStringToBinary(hexStr1);
        int num1 = DigitalTrans.binaryToAlgorism(binaryStr1);
        // 播种命令
        String hexStr2 = hexStr.substring(2, 4);
        String binaryStr2 = DigitalTrans.hexStringToBinary(hexStr2);
        int num2 = DigitalTrans.binaryToAlgorism(binaryStr2);
        entity.setUploadType(num2);
        // 作物名称
        String hexStr3 = hexStr.substring(4, 6);
        String binaryStr3 = DigitalTrans.hexStringToBinary(hexStr3);
        int num3 = DigitalTrans.binaryToAlgorism(binaryStr3);
        // 播种方式
        String hexStr4 = hexStr.substring(6, 8);
        String binaryStr4 = DigitalTrans.hexStringToBinary(hexStr4);
        int num4 = DigitalTrans.binaryToAlgorism(binaryStr4);
        entity.setSowType(num4);
        // 机器播种行数
        String hexStr5 = hexStr.substring(8, 10);
        String binaryStr5 = DigitalTrans.hexStringToBinary(hexStr5);
        int num5 = DigitalTrans.binaryToAlgorism(binaryStr5);
        entity.setSowLine(num5);
        // 播种机肥设定
        String hexStr6 = hexStr.substring(10, 12);
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
        
        
        // 播种株距
        String hexStr7 = hexStr.substring(12, 16);
        String binaryStr7 = DigitalTrans.hexStringToBinary(hexStr7);
        int num7 = DigitalTrans.binaryToAlgorism(binaryStr7);
        entity.setASowInterval(num7);
        
        // 播种幅宽
        String hexStr8 = hexStr.substring(16, 20);
        String binaryStr8 = DigitalTrans.hexStringToBinary(hexStr8);
        int num8 = DigitalTrans.binaryToAlgorism(binaryStr8);
        entity.setSowingWidth(num8);
        
        // 主肥设定
        String hexStr9 = hexStr.substring(20, 24);
        String binaryStr9 = DigitalTrans.hexStringToBinary(hexStr9);
        int num9 = DigitalTrans.binaryToAlgorism(binaryStr9);
        entity.setSetMainFertilizerValue(num9);
        // 主肥反馈
        String hexStr10 = hexStr.substring(24, 28);
        String binaryStr10 = DigitalTrans.hexStringToBinary(hexStr10);
        int num10 = DigitalTrans.binaryToAlgorism(binaryStr10);
        entity.setRealMainFertilizerValue(num10);
        // 口肥设定
        String hexStr11 = hexStr.substring(28, 32);
        String binaryStr11 = DigitalTrans.hexStringToBinary(hexStr11);
        int num11 = DigitalTrans.binaryToAlgorism(binaryStr11);
        entity.setSetDeputyFertilizerValue(num11);
        
        // 口肥反馈
        String hexStr12 = hexStr.substring(32, 36);
        String binaryStr12 = DigitalTrans.hexStringToBinary(hexStr12);
        int num12 = DigitalTrans.binaryToAlgorism(binaryStr12);
        entity.setRealDeputyFertilizerValue(num12);


        // 主肥系数
        String hexStr13 = hexStr.substring(36, 38);
        String binaryStr13 = DigitalTrans.hexStringToBinary(hexStr13);
        int num13 = DigitalTrans.binaryToAlgorism(binaryStr13);
        entity.setMainFertilizerRate(num13);
        // 口肥系数
        String hexStr14 = hexStr.substring(38, 40);
        String binaryStr14 = DigitalTrans.hexStringToBinary(hexStr14);
        int num14 = DigitalTrans.binaryToAlgorism(binaryStr14);
        entity.setDeputyFertilizerRate(num14);


        // 播种趟数(TODO  update:【无主肥电机时，上位机给的主肥监控检测缺肥的速度，单位0.1km/h(屏设置)】
        String hexStr15 = hexStr.substring(40, 44);
        String binaryStr15 = DigitalTrans.hexStringToBinary(hexStr15);
        int num15 = DigitalTrans.binaryToAlgorism(binaryStr15);
        
        
        // 单趟运行时间
        String hexStr16 = hexStr.substring(44, 48);
        String binaryStr16 = DigitalTrans.hexStringToBinary(hexStr16);
        int num16 = DigitalTrans.binaryToAlgorism(binaryStr16);
        entity.setRunningTime(num16);
        // 单趟播种面积
        String hexStr17 = hexStr.substring(48, 56);
        String binaryStr17 = DigitalTrans.hexStringToBinary(hexStr17);
        int num17 = DigitalTrans.binaryToAlgorism(binaryStr17);
        // 平均速度
        String hexStr18 = hexStr.substring(56, 60);
        String binaryStr18 = DigitalTrans.hexStringToBinary(hexStr18);
        int num18 = DigitalTrans.binaryToAlgorism(binaryStr18);
        
        
        // 单趟长度
        String hexStr19 = hexStr.substring(60, 64);
        String binaryStr19 = DigitalTrans.hexStringToBinary(hexStr19);
        int num19 = DigitalTrans.binaryToAlgorism(binaryStr19);
        entity.setSowDistance(num19);
        
        


        // 种报警次数
        String hexStr21 = hexStr.substring(160, 162);
        String binaryStr21 = DigitalTrans.hexStringToBinary(hexStr21);
        int num21 = DigitalTrans.binaryToAlgorism(binaryStr21);
        entity.setSeedAlarmCount(num21);
        //TODO 种报警原因
//        String hexStr22 = hexStr.substring(162, 164);
//        String binaryStr22 = DigitalTrans.hexStringToBinary(hexStr22);
//        if (binaryStr22.length() != 8) {
//            continue;
//        }
//        String high5Bits22 = binaryStr22.substring(0, 5);
//        String low3Bits22 = binaryStr22.substring(5, 8);
//        int numHigh5_22 = DigitalTrans.binaryToAlgorism(high5Bits22);
//        int numLow3_22 = DigitalTrans.binaryToAlgorism(low3Bits22);
//        int[] num22 = new int[2];
//        num22[0] = numHigh5_22;
//        num22[1] = numLow3_22;
//        map.put("num22", num22);
//        String hexStr23 = hexStr.substring(164, 166);
//        String binaryStr23 = DigitalTrans.hexStringToBinary(hexStr23);
//        if (binaryStr23.length() != 8) {
//            continue;
//        }
//        String high5Bits23 = binaryStr23.substring(0, 5);
//        String low3Bits23 = binaryStr23.substring(5, 8);
//        int numHigh5_23 = DigitalTrans.binaryToAlgorism(high5Bits23);
//        int numLow3_23 = DigitalTrans.binaryToAlgorism(low3Bits23);
//        int[] num23 = new int[2];
//        num23[0] = numHigh5_23;
//        num23[1] = numLow3_23;
//        map.put("num23", num23);
//        String hexStr24 = hexStr.substring(166, 168);
//        String binaryStr24 = DigitalTrans.hexStringToBinary(hexStr24);
//        if (binaryStr24.length() != 8) {
//            continue;
//        }
//        String high5Bits24 = binaryStr24.substring(0, 5);
//        String low3Bits24 = binaryStr24.substring(5, 8);
//        int numHigh5_24 = DigitalTrans.binaryToAlgorism(high5Bits24);
//        int numLow3_24 = DigitalTrans.binaryToAlgorism(low3Bits24);
//        int[] num24 = new int[2];
//        num24[0] = numHigh5_24;
//        num24[1] = numLow3_24;
//        map.put("num24", num24);
//        String hexStr25 = hexStr.substring(168, 170);
//        String binaryStr25 = DigitalTrans.hexStringToBinary(hexStr25);
//        if (binaryStr25.length() != 8) {
//            continue;
//        }
//        String high5Bits25 = binaryStr25.substring(0, 5);
//        String low3Bits25 = binaryStr25.substring(5, 8);
//        int numHigh5_25 = DigitalTrans.binaryToAlgorism(high5Bits25);
//        int numLow3_25 = DigitalTrans.binaryToAlgorism(low3Bits25);
//        int[] num25 = new int[2];
//        num25[0] = numHigh5_25;
//        num25[1] = numLow3_25;
//        map.put("num25", num25);
//        String hexStr26 = hexStr.substring(170, 172);
//        String binaryStr26 = DigitalTrans.hexStringToBinary(hexStr26);
//        if (binaryStr26.length() != 8) {
//            continue;
//        }
//        String high5Bits26 = binaryStr26.substring(0, 5);
//        String low3Bits26 = binaryStr26.substring(5, 8);
//        int numHigh5_26 = DigitalTrans.binaryToAlgorism(high5Bits26);
//        int numLow3_26 = DigitalTrans.binaryToAlgorism(low3Bits26);
//        int[] num26 = new int[2];
//        num26[0] = numHigh5_26;
//        num26[1] = numLow3_26;
//        map.put("num26", num26);
//        String hexStr27 = hexStr.substring(172, 174);
//        String binaryStr27 = DigitalTrans.hexStringToBinary(hexStr27);
//        if (binaryStr27.length() != 8) {
//            continue;
//        }
//        String high5Bits27 = binaryStr27.substring(0, 5);
//        String low3Bits27 = binaryStr27.substring(5, 8);
//        int numHigh5_27 = DigitalTrans.binaryToAlgorism(high5Bits27);
//        int numLow3_27 = DigitalTrans.binaryToAlgorism(low3Bits27);
//        int[] num27 = new int[2];
//        num27[0] = numHigh5_27;
//        num27[1] = numLow3_27;
//        map.put("num27", num27);
//        String hexStr28 = hexStr.substring(174, 176);
//        String binaryStr28 = DigitalTrans.hexStringToBinary(hexStr28);
//        if (binaryStr28.length() != 8) {
//            continue;
//        }
//        String high5Bits28 = binaryStr28.substring(0, 5);
//        String low3Bits28 = binaryStr28.substring(5, 8);
//        int numHigh5_28 = DigitalTrans.binaryToAlgorism(high5Bits28);
//        int numLow3_28 = DigitalTrans.binaryToAlgorism(low3Bits28);
//        int[] num28 = new int[2];
//        num28[0] = numHigh5_28;
//        num28[1] = numLow3_28;
//        map.put("num28", num28);
//        String hexStr29 = hexStr.substring(176, 178);
//        String binaryStr29 = DigitalTrans.hexStringToBinary(hexStr29);
//        if (binaryStr29.length() != 8) {
//            continue;
//        }
//        String high5Bits29 = binaryStr29.substring(0, 5);
//        String low3Bits29 = binaryStr29.substring(5, 8);
//        int numHigh5_29 = DigitalTrans.binaryToAlgorism(high5Bits29);
//        int numLow3_29 = DigitalTrans.binaryToAlgorism(low3Bits29);
//        int[] num29 = new int[2];
//        num29[0] = numHigh5_29;
//        num29[1] = numLow3_29;
//        map.put("num29", num29);
//        String hexStr30 = hexStr.substring(178, 180);
//        String binaryStr30 = DigitalTrans.hexStringToBinary(hexStr30);
//        if (binaryStr30.length() != 8) {
//            continue;
//        }
//        String high5Bits30 = binaryStr30.substring(0, 5);
//        String low3Bits30 = binaryStr30.substring(5, 8);
//        int numHigh5_30 = DigitalTrans.binaryToAlgorism(high5Bits30);
//        int numLow3_30 = DigitalTrans.binaryToAlgorism(low3Bits30);
//        int[] num30 = new int[2];
//        num30[0] = numHigh5_30;
//        num30[1] = numLow3_30;
//        map.put("num30", num30);
//        String hexStr31 = hexStr.substring(180, 182);
//        String binaryStr31 = DigitalTrans.hexStringToBinary(hexStr31);
//        if (binaryStr31.length() != 8) {
//            continue;
//        }
//        String high5Bits31 = binaryStr31.substring(0, 5);
//        String low3Bits31 = binaryStr31.substring(5, 8);
//        int numHigh5_31 = DigitalTrans.binaryToAlgorism(high5Bits31);
//        int numLow3_31 = DigitalTrans.binaryToAlgorism(low3Bits31);
//        int[] num31 = new int[2];
//        num31[0] = numHigh5_31;
//        num31[1] = numLow3_31;
//        map.put("num31", num31);
//        String hexStr32 = hexStr.substring(182, 184);
//        String binaryStr32 = DigitalTrans.hexStringToBinary(hexStr32);
//        if (binaryStr32.length() != 8) {
//            continue;
//        }
//        String high5Bits32 = binaryStr32.substring(0, 5);
//        String low3Bits32 = binaryStr32.substring(5, 8);
//        int numHigh5_32 = DigitalTrans.binaryToAlgorism(high5Bits32);
//        int numLow3_32 = DigitalTrans.binaryToAlgorism(low3Bits32);
//        int[] num32 = new int[2];
//        num32[0] = numHigh5_32;
//        num32[1] = numLow3_32;
//        map.put("num32", num32);
//        String hexStr33 = hexStr.substring(184, 186);
//        String binaryStr33 = DigitalTrans.hexStringToBinary(hexStr33);
//        if (binaryStr33.length() != 8) {
//            continue;
//        }
//        String high5Bits33 = binaryStr33.substring(0, 5);
//        String low3Bits33 = binaryStr33.substring(5, 8);
//        int numHigh5_33 = DigitalTrans.binaryToAlgorism(high5Bits33);
//        int numLow3_33 = DigitalTrans.binaryToAlgorism(low3Bits33);
//        int[] num33 = new int[2];
//        num33[0] = numHigh5_33;
//        num33[1] = numLow3_33;
//        map.put("num33", num33);
//        String hexStr34 = hexStr.substring(186, 188);
//        String binaryStr34 = DigitalTrans.hexStringToBinary(hexStr34);
//        if (binaryStr34.length() != 8) {
//            continue;
//        }
//        String high5Bits34 = binaryStr34.substring(0, 5);
//        String low3Bits34 = binaryStr34.substring(5, 8);
//        int numHigh5_34 = DigitalTrans.binaryToAlgorism(high5Bits34);
//        int numLow3_34 = DigitalTrans.binaryToAlgorism(low3Bits34);
//        int[] num34 = new int[2];
//        num34[0] = numHigh5_34;
//        num34[1] = numLow3_34;
//        map.put("num34", num34);
//        String hexStr35 = hexStr.substring(188, 190);
//        String binaryStr35 = DigitalTrans.hexStringToBinary(hexStr35);
//        if (binaryStr35.length() != 8) {
//            continue;
//        }
//        String high5Bits35 = binaryStr35.substring(0, 5);
//        String low3Bits35 = binaryStr35.substring(5, 8);
//        int numHigh5_35 = DigitalTrans.binaryToAlgorism(high5Bits35);
//        int numLow3_35 = DigitalTrans.binaryToAlgorism(low3Bits35);
//        int[] num35 = new int[2];
//        num35[0] = numHigh5_35;
//        num35[1] = numLow3_35;
//        map.put("num35", num35);
//        String hexStr36 = hexStr.substring(190, 192);
//        String binaryStr36 = DigitalTrans.hexStringToBinary(hexStr36);
//        if (binaryStr36.length() != 8) {
//            continue;
//        }
//        String high5Bits36 = binaryStr36.substring(0, 5);
//        String low3Bits36 = binaryStr36.substring(5, 8);
//        int numHigh5_36 = DigitalTrans.binaryToAlgorism(high5Bits36);
//        int numLow3_36 = DigitalTrans.binaryToAlgorism(low3Bits36);
//        int[] num36 = new int[2];
//        num36[0] = numHigh5_36;
//        num36[1] = numLow3_36;
//        map.put("num36", num36);
//        String hexStr37 = hexStr.substring(192, 194);
//        String binaryStr37 = DigitalTrans.hexStringToBinary(hexStr37);
//        if (binaryStr37.length() != 8) {
//            continue;
//        }
//        String high5Bits37 = binaryStr37.substring(0, 5);
//        String low3Bits37 = binaryStr37.substring(5, 8);
//        int numHigh5_37 = DigitalTrans.binaryToAlgorism(high5Bits37);
//        int numLow3_37 = DigitalTrans.binaryToAlgorism(low3Bits37);
//        int[] num37 = new int[2];
//        num37[0] = numHigh5_37;
//        num37[1] = numLow3_37;
//        map.put("num37", num37);
//        String hexStr38 = hexStr.substring(194, 196);
//        String binaryStr38 = DigitalTrans.hexStringToBinary(hexStr38);
//        if (binaryStr38.length() != 8) {
//            continue;
//        }
//        String high5Bits38 = binaryStr38.substring(0, 5);
//        String low3Bits38 = binaryStr38.substring(5, 8);
//        int numHigh5_38 = DigitalTrans.binaryToAlgorism(high5Bits38);
//        int numLow3_38 = DigitalTrans.binaryToAlgorism(low3Bits38);
//        int[] num38 = new int[2];
//        num38[0] = numHigh5_38;
//        num38[1] = numLow3_38;
//        map.put("num38", num38);
//        String hexStr39 = hexStr.substring(196, 198);
//        String binaryStr39 = DigitalTrans.hexStringToBinary(hexStr39);
//        if (binaryStr39.length() != 8) {
//            continue;
//        }
//        String high5Bits39 = binaryStr39.substring(0, 5);
//        String low3Bits39 = binaryStr39.substring(5, 8);
//        int numHigh5_39 = DigitalTrans.binaryToAlgorism(high5Bits39);
//        int numLow3_39 = DigitalTrans.binaryToAlgorism(low3Bits39);
//        int[] num39 = new int[2];
//        num39[0] = numHigh5_39;
//        num39[1] = numLow3_39;
//        map.put("num39", num39);
//        String hexStr40 = hexStr.substring(198, 200);
//        String binaryStr40 = DigitalTrans.hexStringToBinary(hexStr40);
//        if (binaryStr40.length() != 8) {
//            continue;
//        }
//        String high5Bits40 = binaryStr40.substring(0, 5);
//        String low3Bits40 = binaryStr40.substring(5, 8);
//        int numHigh5_40 = DigitalTrans.binaryToAlgorism(high5Bits40);
//        int numLow3_40 = DigitalTrans.binaryToAlgorism(low3Bits40);
//        int[] num40 = new int[2];
//        num40[0] = numHigh5_40;
//        num40[1] = numLow3_40;
//        map.put("num40", num40);
//        String hexStr41 = hexStr.substring(200, 202);
//        String binaryStr41 = DigitalTrans.hexStringToBinary(hexStr41);
//        if (binaryStr41.length() != 8) {
//            continue;
//        }
//        String high5Bits41 = binaryStr41.substring(0, 5);
//        String low3Bits41 = binaryStr41.substring(5, 8);
//        int numHigh5_41 = DigitalTrans.binaryToAlgorism(high5Bits41);
//        int numLow3_41 = DigitalTrans.binaryToAlgorism(low3Bits41);
//        int[] num41 = new int[2];
//        num41[0] = numHigh5_41;
//        num41[1] = numLow3_41;
//        map.put("num41", num41);

        // 主肥监控断流
        String hexStr42 = hexStr.substring(202, 206);
        String binaryStr42 = DigitalTrans.hexStringToBinary(hexStr42);
        int num42 = DigitalTrans.binaryToAlgorism(binaryStr42);
        entity.setLackFertilizerRate(num42);
        // 主肥监控堵塞
        String hexStr43 = hexStr.substring(206, 210);
        String binaryStr43 = DigitalTrans.hexStringToBinary(hexStr43);
        int num43 = DigitalTrans.binaryToAlgorism(binaryStr43);
        // 1#主肥监反馈
        String hexStr44 = hexStr.substring(210, 214);
        String binaryStr44 = DigitalTrans.hexStringToBinary(hexStr44);
        int num44 = DigitalTrans.binaryToAlgorism(binaryStr44);
        // 2#主肥监反馈
        String hexStr45 = hexStr.substring(214, 218);
        String binaryStr45 = DigitalTrans.hexStringToBinary(hexStr45);
        int num45 = DigitalTrans.binaryToAlgorism(binaryStr45);
        // 3#主肥监反馈
        String hexStr46 = hexStr.substring(218, 222);
        String binaryStr46 = DigitalTrans.hexStringToBinary(hexStr46);
        int num46 = DigitalTrans.binaryToAlgorism(binaryStr46);
        // 主肥监控报警次数
        String hexStr47 = hexStr.substring(222, 224);
        String binaryStr47 = DigitalTrans.hexStringToBinary(hexStr47);
        int num47 = DigitalTrans.binaryToAlgorism(binaryStr47);
        entity.setFertilizerAlarmCount(num47);


        // 主肥监控报警原因
//        String hexStr48 = hexStr.substring(224, 226);
//        String binaryStr48 = DigitalTrans.hexStringToBinary(hexStr48);
//        if (binaryStr48.length() != 8) {
//            continue;
//        }
//        String high5Bits48 = binaryStr48.substring(0, 5);
//        String low3Bits48 = binaryStr48.substring(5, 8);
//        int numHigh5_48 = DigitalTrans.binaryToAlgorism(high5Bits48);
//        int numLow3_48 = DigitalTrans.binaryToAlgorism(low3Bits48);
//        int[] num48 = new int[2];
//        num48[0] = numHigh5_48;
//        num48[1] = numLow3_48;
//        map.put("num48", num48);
//        String hexStr49 = hexStr.substring(226, 228);
//        String binaryStr49 = DigitalTrans.hexStringToBinary(hexStr49);
//        if (binaryStr49.length() != 8) {
//            continue;
//        }
//        String high5Bits49 = binaryStr49.substring(0, 5);
//        String low3Bits49 = binaryStr49.substring(5, 8);
//        int numHigh5_49 = DigitalTrans.binaryToAlgorism(high5Bits49);
//        int numLow3_49 = DigitalTrans.binaryToAlgorism(low3Bits49);
//        int[] num49 = new int[2];
//        num49[0] = numHigh5_49;
//        num49[1] = numLow3_49;
//        map.put("num49", num49);
//        String hexStr50 = hexStr.substring(228, 230);
//        String binaryStr50 = DigitalTrans.hexStringToBinary(hexStr50);
//        if (binaryStr50.length() != 8) {
//            continue;
//        }
//        String high5Bits50 = binaryStr50.substring(0, 5);
//        String low3Bits50 = binaryStr50.substring(5, 8);
//        int numHigh5_50 = DigitalTrans.binaryToAlgorism(high5Bits50);
//        int numLow3_50 = DigitalTrans.binaryToAlgorism(low3Bits50);
//        int[] num50 = new int[2];
//        num50[0] = numHigh5_50;
//        num50[1] = numLow3_50;
//        map.put("num50", num50);
//        String hexStr51 = hexStr.substring(230, 232);
//        String binaryStr51 = DigitalTrans.hexStringToBinary(hexStr51);
//        if (binaryStr51.length() != 8) {
//            continue;
//        }
//        String high5Bits51 = binaryStr51.substring(0, 5);
//        String low3Bits51 = binaryStr51.substring(5, 8);
//        int numHigh5_51 = DigitalTrans.binaryToAlgorism(high5Bits51);
//        int numLow3_51 = DigitalTrans.binaryToAlgorism(low3Bits51);
//        int[] num51 = new int[2];
//        num51[0] = numHigh5_51;
//        num51[1] = numLow3_51;
//        map.put("num51", num51);
//        String hexStr52 = hexStr.substring(232, 234);
//        String binaryStr52 = DigitalTrans.hexStringToBinary(hexStr52);
//        if (binaryStr52.length() != 8) {
//            continue;
//        }
//        String high5Bits52 = binaryStr52.substring(0, 5);
//        String low3Bits52 = binaryStr52.substring(5, 8);
//        int numHigh5_52 = DigitalTrans.binaryToAlgorism(high5Bits52);
//        int numLow3_52 = DigitalTrans.binaryToAlgorism(low3Bits52);
//        int[] num52 = new int[2];
//        num52[0] = numHigh5_52;
//        num52[1] = numLow3_52;
//        map.put("num52", num52);
//        String hexStr53 = hexStr.substring(234, 236);
//        String binaryStr53 = DigitalTrans.hexStringToBinary(hexStr53);
//        if (binaryStr53.length() != 8) {
//            continue;
//        }
//        String high5Bits53 = binaryStr53.substring(0, 5);
//        String low3Bits53 = binaryStr53.substring(5, 8);
//        int numHigh5_53 = DigitalTrans.binaryToAlgorism(high5Bits53);
//        int numLow3_53 = DigitalTrans.binaryToAlgorism(low3Bits53);
//        int[] num53 = new int[2];
//        num53[0] = numHigh5_53;
//        num53[1] = numLow3_53;
//        map.put("num53", num53);
//        String hexStr54 = hexStr.substring(236, 238);
//        String binaryStr54 = DigitalTrans.hexStringToBinary(hexStr54);
//        if (binaryStr54.length() != 8) {
//            continue;
//        }
//        String high5Bits54 = binaryStr54.substring(0, 5);
//        String low3Bits54 = binaryStr54.substring(5, 8);
//        int numHigh5_54 = DigitalTrans.binaryToAlgorism(high5Bits54);
//        int numLow3_54 = DigitalTrans.binaryToAlgorism(low3Bits54);
//        int[] num54 = new int[2];
//        num54[0] = numHigh5_54;
//        num54[1] = numLow3_54;
//        map.put("num54", num54);
//        String hexStr55 = hexStr.substring(238, 240);
//        String binaryStr55 = DigitalTrans.hexStringToBinary(hexStr55);
//        if (binaryStr55.length() != 8) {
//            continue;
//        }
//        String high5Bits55 = binaryStr55.substring(0, 5);
//        String low3Bits55 = binaryStr55.substring(5, 8);
//        int numHigh5_55 = DigitalTrans.binaryToAlgorism(high5Bits55);
//        int numLow3_55 = DigitalTrans.binaryToAlgorism(low3Bits55);
//        int[] num55 = new int[2];
//        num55[0] = numHigh5_55;
//        num55[1] = numLow3_55;
//        map.put("num55", num55);
//        String hexStr56 = hexStr.substring(240, 242);
//        String binaryStr56 = DigitalTrans.hexStringToBinary(hexStr56);
//        if (binaryStr56.length() != 8) {
//            continue;
//        }
//        String high5Bits56 = binaryStr56.substring(0, 5);
//        String low3Bits56 = binaryStr56.substring(5, 8);
//        int numHigh5_56 = DigitalTrans.binaryToAlgorism(high5Bits56);
//        int numLow3_56 = DigitalTrans.binaryToAlgorism(low3Bits56);
//        int[] num56 = new int[2];
//        num56[0] = numHigh5_56;
//        num56[1] = numLow3_56;
//        map.put("num56", num56);
//        String hexStr57 = hexStr.substring(242, 244);
//        String binaryStr57 = DigitalTrans.hexStringToBinary(hexStr57);
//        if (binaryStr57.length() != 8) {
//            continue;
//        }
//        String high5Bits57 = binaryStr57.substring(0, 5);
//        String low3Bits57 = binaryStr57.substring(5, 8);
//        int numHigh5_57 = DigitalTrans.binaryToAlgorism(high5Bits57);
//        int numLow3_57 = DigitalTrans.binaryToAlgorism(low3Bits57);
//        int[] num57 = new int[2];
//        num57[0] = numHigh5_57;
//        num57[1] = numLow3_57;
//        map.put("num57", num57);
//        String hexStr58 = hexStr.substring(244, 246);
//        String binaryStr58 = DigitalTrans.hexStringToBinary(hexStr58);
//        if (binaryStr58.length() != 8) {
//            continue;
//        }
//        String high5Bits58 = binaryStr58.substring(0, 5);
//        String low3Bits58 = binaryStr58.substring(5, 8);
//        int numHigh5_58 = DigitalTrans.binaryToAlgorism(high5Bits58);
//        int numLow3_58 = DigitalTrans.binaryToAlgorism(low3Bits58);
//        int[] num58 = new int[2];
//        num58[0] = numHigh5_58;
//        num58[1] = numLow3_58;
//        map.put("num58", num58);
//        String hexStr59 = hexStr.substring(246, 248);
//        String binaryStr59 = DigitalTrans.hexStringToBinary(hexStr59);
//        if (binaryStr59.length() != 8) {
//            continue;
//        }
//        String high5Bits59 = binaryStr59.substring(0, 5);
//        String low3Bits59 = binaryStr59.substring(5, 8);
//        int numHigh5_59 = DigitalTrans.binaryToAlgorism(high5Bits59);
//        int numLow3_59 = DigitalTrans.binaryToAlgorism(low3Bits59);
//        int[] num59 = new int[2];
//        num59[0] = numHigh5_59;
//        num59[1] = numLow3_59;
//        map.put("num59", num59);
//        String hexStr60 = hexStr.substring(248, 250);
//        String binaryStr60 = DigitalTrans.hexStringToBinary(hexStr60);
//        if (binaryStr60.length() != 8) {
//            continue;
//        }
//        String high5Bits60 = binaryStr60.substring(0, 5);
//        String low3Bits60 = binaryStr60.substring(5, 8);
//        int numHigh5_60 = DigitalTrans.binaryToAlgorism(high5Bits60);
//        int numLow3_60 = DigitalTrans.binaryToAlgorism(low3Bits60);
//        int[] num60 = new int[2];
//        num60[0] = numHigh5_60;
//        num60[1] = numLow3_60;
//        map.put("num60", num60);
//        String hexStr61 = hexStr.substring(250, 252);
//        String binaryStr61 = DigitalTrans.hexStringToBinary(hexStr61);
//        if (binaryStr61.length() != 8) {
//            continue;
//        }
//        String high5Bits61 = binaryStr61.substring(0, 5);
//        String low3Bits61 = binaryStr61.substring(5, 8);
//        int numHigh5_61 = DigitalTrans.binaryToAlgorism(high5Bits61);
//        int numLow3_61 = DigitalTrans.binaryToAlgorism(low3Bits61);
//        int[] num61 = new int[2];
//        num61[0] = numHigh5_61;
//        num61[1] = numLow3_61;
//        map.put("num61", num61);
//        String hexStr62 = hexStr.substring(252, 254);
//        String binaryStr62 = DigitalTrans.hexStringToBinary(hexStr62);
//        if (binaryStr62.length() != 8) {
//            continue;
//        }
//        String high5Bits62 = binaryStr62.substring(0, 5);
//        String low3Bits62 = binaryStr62.substring(5, 8);
//        int numHigh5_62 = DigitalTrans.binaryToAlgorism(high5Bits62);
//        int numLow3_62 = DigitalTrans.binaryToAlgorism(low3Bits62);
//        int[] num62 = new int[2];
//        num62[0] = numHigh5_62;
//        num62[1] = numLow3_62;
//        map.put("num62", num62);
//        String hexStr63 = hexStr.substring(254, 256);
//        String binaryStr63 = DigitalTrans.hexStringToBinary(hexStr63);
//        if (binaryStr63.length() != 8) {
//            continue;
//        }
//        String high5Bits63 = binaryStr63.substring(0, 5);
//        String low3Bits63 = binaryStr63.substring(5, 8);
//        int numHigh5_63 = DigitalTrans.binaryToAlgorism(high5Bits63);
//        int numLow3_63 = DigitalTrans.binaryToAlgorism(low3Bits63);
//        int[] num63 = new int[2];
//        num63[0] = numHigh5_63;
//        num63[1] = numLow3_63;
//        map.put("num63", num63);
//        String hexStr64 = hexStr.substring(256, 258);
//        String binaryStr64 = DigitalTrans.hexStringToBinary(hexStr64);
//        if (binaryStr64.length() != 8) {
//            continue;
//        }
//        String high5Bits64 = binaryStr64.substring(0, 5);
//        String low3Bits64 = binaryStr64.substring(5, 8);
//        int numHigh5_64 = DigitalTrans.binaryToAlgorism(high5Bits64);
//        int numLow3_64 = DigitalTrans.binaryToAlgorism(low3Bits64);
//        int[] num64 = new int[2];
//        num64[0] = numHigh5_64;
//        num64[1] = numLow3_64;
//        map.put("num64", num64);
//        String hexStr65 = hexStr.substring(258, 260);
//        String binaryStr65 = DigitalTrans.hexStringToBinary(hexStr65);
//        if (binaryStr65.length() != 8) {
//            continue;
//        }
//        String high5Bits65 = binaryStr65.substring(0, 5);
//        String low3Bits65 = binaryStr65.substring(5, 8);
//        int numHigh5_65 = DigitalTrans.binaryToAlgorism(high5Bits65);
//        int numLow3_65 = DigitalTrans.binaryToAlgorism(low3Bits65);
//        int[] num65 = new int[2];
//        num65[0] = numHigh5_65;
//        num65[1] = numLow3_65;
//        map.put("num65", num65);
//        String hexStr66 = hexStr.substring(260, 262);
//        String binaryStr66 = DigitalTrans.hexStringToBinary(hexStr66);
//        if (binaryStr66.length() != 8) {
//            continue;
//        }
//        String high5Bits66 = binaryStr66.substring(0, 5);
//        String low3Bits66 = binaryStr66.substring(5, 8);
//        int numHigh5_66 = DigitalTrans.binaryToAlgorism(high5Bits66);
//        int numLow3_66 = DigitalTrans.binaryToAlgorism(low3Bits66);
//        int[] num66 = new int[2];
//        num66[0] = numHigh5_66;
//        num66[1] = numLow3_66;
//        map.put("num66", num66);
//        String hexStr67 = hexStr.substring(262, 264);
//        String binaryStr67 = DigitalTrans.hexStringToBinary(hexStr67);
//        if (binaryStr67.length() != 8) {
//            continue;
//        }
//        String high5Bits67 = binaryStr67.substring(0, 5);
//        String low3Bits67 = binaryStr67.substring(5, 8);
//        int numHigh5_67 = DigitalTrans.binaryToAlgorism(high5Bits67);
//        int numLow3_67 = DigitalTrans.binaryToAlgorism(low3Bits67);
//        int[] num67 = new int[2];
//        num67[0] = numHigh5_67;
//        num67[1] = numLow3_67;
//        map.put("num67", num67);

        // 主肥电机报警次数
        String hexStr68 = hexStr.substring(264, 266);
        String binaryStr68 = DigitalTrans.hexStringToBinary(hexStr68);
        int num68 = DigitalTrans.binaryToAlgorism(binaryStr68);
        entity.setFertilizerAlarmCount(num68);

        // 主肥电机报警原因
//        String hexStr69 = hexStr.substring(266, 268);
//        String binaryStr69 = DigitalTrans.hexStringToBinary(hexStr69);
//        if (binaryStr69.length() != 8) {
//            continue;
//        }
//        String high5Bits69 = binaryStr69.substring(0, 5);
//        String low3Bits69 = binaryStr69.substring(5, 8);
//        int numHigh5_69 = DigitalTrans.binaryToAlgorism(high5Bits69);
//        int numLow3_69 = DigitalTrans.binaryToAlgorism(low3Bits69);
//        int[] num69 = new int[2];
//        num69[0] = numHigh5_69;
//        num69[1] = numLow3_69;
//        map.put("num69", num69);
//        String hexStr70 = hexStr.substring(268, 270);
//        String binaryStr70 = DigitalTrans.hexStringToBinary(hexStr70);
//        if (binaryStr70.length() != 8) {
//            continue;
//        }
//        String high5Bits70 = binaryStr70.substring(0, 5);
//        String low3Bits70 = binaryStr70.substring(5, 8);
//        int numHigh5_70 = DigitalTrans.binaryToAlgorism(high5Bits70);
//        int numLow3_70 = DigitalTrans.binaryToAlgorism(low3Bits70);
//        int[] num70 = new int[2];
//        num70[0] = numHigh5_70;
//        num70[1] = numLow3_70;
//        map.put("num70", num70);
//        String hexStr71 = hexStr.substring(270, 272);
//        String binaryStr71 = DigitalTrans.hexStringToBinary(hexStr71);
//        if (binaryStr71.length() != 8) {
//            continue;
//        }
//        String high5Bits71 = binaryStr71.substring(0, 5);
//        String low3Bits71 = binaryStr71.substring(5, 8);
//        int numHigh5_71 = DigitalTrans.binaryToAlgorism(high5Bits71);
//        int numLow3_71 = DigitalTrans.binaryToAlgorism(low3Bits71);
//        int[] num71 = new int[2];
//        num71[0] = numHigh5_71;
//        num71[1] = numLow3_71;
//        map.put("num71", num71);
//        String hexStr72 = hexStr.substring(272, 274);
//        String binaryStr72 = DigitalTrans.hexStringToBinary(hexStr72);
//        if (binaryStr72.length() != 8) {
//            continue;
//        }
//        String high5Bits72 = binaryStr72.substring(0, 5);
//        String low3Bits72 = binaryStr72.substring(5, 8);
//        int numHigh5_72 = DigitalTrans.binaryToAlgorism(high5Bits72);
//        int numLow3_72 = DigitalTrans.binaryToAlgorism(low3Bits72);
//        int[] num72 = new int[2];
//        num72[0] = numHigh5_72;
//        num72[1] = numLow3_72;
//        map.put("num72", num72);

        // 口肥电机报警次数
        String hexStr73 = hexStr.substring(274, 276);
        String binaryStr73 = DigitalTrans.hexStringToBinary(hexStr73);
        int num73 = DigitalTrans.binaryToAlgorism(binaryStr73);
        

        // 口肥电机报警原因
//        String hexStr74 = hexStr.substring(276, 278);
//        String binaryStr74 = DigitalTrans.hexStringToBinary(hexStr74);
//        if (binaryStr74.length() != 8) {
//            continue;
//        }
//        String high5Bits74 = binaryStr74.substring(0, 5);
//        String low3Bits74 = binaryStr74.substring(5, 8);
//        int numHigh5_74 = DigitalTrans.binaryToAlgorism(high5Bits74);
//        int numLow3_74 = DigitalTrans.binaryToAlgorism(low3Bits74);
//        int[] num74 = new int[2];
//        num74[0] = numHigh5_74;
//        num74[1] = numLow3_74;
//        map.put("num74", num74);
//        String hexStr75 = hexStr.substring(278, 280);
//        String binaryStr75 = DigitalTrans.hexStringToBinary(hexStr75);
//        if (binaryStr75.length() != 8) {
//            continue;
//        }
//        String high5Bits75 = binaryStr75.substring(0, 5);
//        String low3Bits75 = binaryStr75.substring(5, 8);
//        int numHigh5_75 = DigitalTrans.binaryToAlgorism(high5Bits75);
//        int numLow3_75 = DigitalTrans.binaryToAlgorism(low3Bits75);
//        int[] num75 = new int[2];
//        num75[0] = numHigh5_75;
//        num75[1] = numLow3_75;
//        map.put("num75", num75);
//        String hexStr76 = hexStr.substring(280, 282);
//        String binaryStr76 = DigitalTrans.hexStringToBinary(hexStr76);
//        if (binaryStr76.length() != 8) {
//            continue;
//        }
//        String high5Bits76 = binaryStr76.substring(0, 5);
//        String low3Bits76 = binaryStr76.substring(5, 8);
//        int numHigh5_76 = DigitalTrans.binaryToAlgorism(high5Bits76);
//        int numLow3_76 = DigitalTrans.binaryToAlgorism(low3Bits76);
//        int[] num76 = new int[2];
//        num76[0] = numHigh5_76;
//        num76[1] = numLow3_76;
//        map.put("num76", num76);
//        String hexStr77 = hexStr.substring(282, 284);
//        String binaryStr77 = DigitalTrans.hexStringToBinary(hexStr77);
//        if (binaryStr77.length() != 8) {
//            continue;
//        }
//        String high5Bits77 = binaryStr77.substring(0, 5);
//        String low3Bits77 = binaryStr77.substring(5, 8);
//        int numHigh5_77 = DigitalTrans.binaryToAlgorism(high5Bits77);
//        int numLow3_77 = DigitalTrans.binaryToAlgorism(low3Bits77);
//        int[] num77 = new int[2];
//        num77[0] = numHigh5_77;
//        num77[1] = numLow3_77;
//        map.put("num77", num77);
		
        
        List<DataElectricSeederMessageLineEntity> lineList = anylisisMachineLineList(hexStr,entity.getSowLine());
        
        entity.setLines(lineList);
   
  		
        
        return entity;
	}
	
	
	

	private List<DataElectricSeederMessageLineEntity> anylisisMachineLineList(String hexStr,int lineCount) {
		
		List<DataElectricSeederMessageLineEntity> list = new ArrayList<>();
		
		
		// 循环处理20_1到20_24共24个变量
		for (int i = 1; i <= lineCount; i++) {
			DataElectricSeederMessageLineEntity entity = new DataElectricSeederMessageLineEntity();
			entity.setLineNo(i);
			
			
		    int start = 64 + (i - 1) * 4;  // 计算起始位置（64,68,72...156）
		    String hexSub = hexStr.substring(start, start + 4);  // 截取4字节子串
		    
		    // 十六进制转二进制再转十进制
		    String binaryStr = DigitalTrans.hexStringToBinary(hexSub);
		    int numValue = DigitalTrans.binaryToAlgorism(binaryStr);
		    
		    // 动态生成键名并存入Map
		    String key = "num20_" + String.format("%02d", i);
		    
		    entity.setSeedNum(numValue);
		    
		    
		    list.add(entity);
		}
		
		return list;
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

}
