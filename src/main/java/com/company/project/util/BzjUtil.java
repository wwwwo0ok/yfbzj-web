package com.company.project.util;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * 2025年上线并且之前的批次的解码方式。
 * 
 * @author bzj
 * @date 2025/4/6
 */
@Slf4j
public class BzjUtil {

    // ----------------------------- 24行列表解析方法 -----------------------------

    /**
     * 解析种使能状态（地址范围：384-389）
     * @param hexData 十六进制数据字符串（完整数据包）
     * @param line 检测线路编号（1-24）
     * @return "OK"表示启用，"取消"表示禁用
     */
    public static int parseSeedEnable(String hexData, int line) {
        // 1. 截取384-389的6字符（3字节）
        String enableHex = hexData.substring(384, 390);
        // 2. 十六进制字符串转字节数组
        byte[] bytes = hexStringToByteArray(enableHex);
        // 3. 校验行号范围
        if (line < 1 || line > 24) {
            throw new IllegalArgumentException("行号范围应为1-24");
        }
        // 4. 计算位索引（0-23）
        int bitIndex = line - 1;
        // 5. 调整字节索引计算（高位在前）
        int byteIndex = 2 - (bitIndex / 8); // 字节索引 2,1,0
        int bitOffset = bitIndex % 8;       // 位偏移 0-7

        // 6. 获取对应位的值（处理符号问题）
        int byteValue = bytes[byteIndex] & 0xFF; // 转为无符号
        int bitValue = (byteValue >> bitOffset) & 0x01;

//        return bitValue == 1 ? "OK" : "取消";
        return bitValue;
    }

    // public static void main(String[] args) {
    //     String str = "004200000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000FF0000000000000000000000000000000000000000000000FF00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000B332B231B13000000000000000000000000000000000000802800AA0078190007D0802800AA0078190007D01407D01E346464BB00060F3CFF32140000081D100474A5A5A5A5A5A500003F00003F00000000003F000F001F0900F35E0606D05D04D03D02D01B000000000000000000000000000000000000000000000000000000066F9000001FFF";
    //     String seedStatusStr = parseSeedEnable(str, 1);
    //     String seedQuantityStr = parseSeedQuantity(str, 1, seedStatusStr);
    //     String parseMultiPercentageStr = parseMultiPercentage(str, 1, seedStatusStr);
    //     String parseSowingPercentageStr = parseSowingPercentage(str, 1, seedStatusStr);
    //     log.info("seedStatusStr={}", seedStatusStr);
    //     log.info("seedQuantityStr={}", seedQuantityStr);
    //     log.info("parseMultiPercentageStr={}", parseMultiPercentageStr);
    //     log.info("parseSowingPercentageStr={}", parseSowingPercentageStr);
    // }

    /**
     * 解析AB通道选择状态（地址范围：358-363）
     * @param hexData 十六进制数据字符串
     * @param line 检测线路编号（1-24）
     * @return "A"表示A通道，"B"表示B通道
     */
    public static int parseABSelection(String hexData, int line) {
        String abHex = hexData.substring(358, 364);
        byte[] bytes = hexStringToByteArray(abHex);
        int bitIndex = line - 1;
        int byteIndex = 2 - (bitIndex / 8);
        int bitOffset = bitIndex % 8;
        int bitValue = (bytes[byteIndex] >> bitOffset) & 0x01;
        return bitValue;
    }

    /**
     * 解析全局AB播种模式（地址范围：358-363，24bit）
     * @param hexData 十六进制数据字符串
     * @return "单一播种"-全0或全1, "复合播种"-存在不同状态
     * @throws IllegalArgumentException 数据格式错误时抛出
     */
    public static String parseGlobalABSowingMode(String hexData) {
        // 截取6字符（3字节）
        String abHex = hexData.substring(358, 364);
        byte[] bytes = hexStringToByteArray(abHex);

        // 统计有效位数
        int zeroCount = 0;
        int oneCount = 0;

        // 遍历24个bit
        for (int i = 0; i < 3; i++) {
            byte b = bytes[i];
            // 检查每个bit
            for (int bit = 0; bit < 8; bit++) {
                int value = (b >> bit) & 0x01;
                if (value == 0) {
                    zeroCount++;
                } else {
                    oneCount++;
                }
            }
        }

        // 判断全局模式
        if (zeroCount == 24 || oneCount == 24) {
            return "单一播种";
        } else {
            return "复合播种";
        }
    }

    /**
     * 解析播种数量（地址范围：0-95）
     * @param hexData 十六进制数据字符串
     * @param line 检测线路编号（1-24）
     * @param seedEnableStatus 种子使能状态（用于状态验证）
     * @return 实际数量（单位：个）或"取消"
     */
    public static int parseSeedQuantity(String hexData, int line) {
        int startAddr = (line - 1) * 4;
        String valueHex = hexData.substring(startAddr, startAddr + 4);
        int value = Integer.parseInt(valueHex, 16);
        return value;
    }

    // 4. 多种百分比（地址96-143，单位0.1%）
    public static int parseMultiPercentage(String hexData, int line) {
        int startAddr = 96 + (line - 1) * 2;
        String valueHex = hexData.substring(startAddr, startAddr + 2);
//        double value = Integer.parseInt(valueHex, 16) * 0.1;
        // log.info("多种百分比: line={}, value={}, formatValue={}", line, value, formatValue);
        return Integer.parseInt(valueHex, 16);
//        return String.format("%.1f%%", value);
    }

    // 5. 播种百分比（地址144-191，显示值=1000-数值，单位0.1%）
    public static int parseSowingPercentage(String hexData, int line) {
        int startAddr = 144 + (line - 1) * 2;
        String valueHex = hexData.substring(startAddr, startAddr + 2);
        int rawValue = Integer.parseInt(valueHex, 16);
//        int value = 1000 - rawValue;
        // log.info("播种百分比: line={}, rawValue={}, value={}, formatValue={}", line, rawValue, value, formatValue);
        return rawValue;
//        return String.format("%.1f%%", value * 0.1);
    }

    /**
     * 解析主肥监控状态（地址范围：402-407）
     * @param hexData 完整的十六进制数据字符串
     * @param line 检测线路编号（1-24）
     * @return "OK"表示监控正常，"取消"表示监控异常
     */
    public static int parseMainFertMonitor(String hexData, int line) {
        String monitorHex = hexData.substring(402, 408);
        byte[] bytes = hexStringToByteArray(monitorHex);
        int bitIndex = line - 1;
        int byteIndex = 2 - (bitIndex / 8);
        int bitOffset = bitIndex % 8;
        int bitValue = (bytes[byteIndex] >> bitOffset) & 0x01;
        return bitValue;
    }

    /**
     * 解析主肥电机状态（地址范围：390-395）
     * @param hexData 十六进制数据字符串
     * @param line 检测线路编号（1-24）
     * @return "OK"表示电机正常，"取消"表示电机异常
     */
    public static int parseMainFertMotor(String hexData, int line) {
        String motorHex = hexData.substring(390, 396); // 390-395地址对应6字节
        byte[] bytes = hexStringToByteArray(motorHex);
        int bitIndex = line - 1;
        int byteIndex = 2 - (bitIndex / 8);
        int bitOffset = bitIndex % 8;
        int bitValue = (bytes[byteIndex] >> bitOffset) & 0x01;
        return bitValue ;
    }

    /**
     * 解析主肥电机方向（地址范围：372-377）
     * @param hexData 十六进制数据字符串
     * @param line 检测线路编号（1-24）
     * @return "正转"或"反转"
     */
    public static int parseMotorDirection(String hexData, int line) {
        String dirHex = hexData.substring(372, 378); // 372-377地址对应6字节
        byte[] bytes = hexStringToByteArray(dirHex);
        int bitIndex = line - 1;
        int byteIndex = 2 - (bitIndex / 8);
        int bitOffset = bitIndex % 8;
        int bitValue = (bytes[byteIndex] >> bitOffset) & 0x01;
        return bitValue;
    }

    /**
     * 解析主肥量速比（地址范围：192-239）
     * @param hexData 十六进制数据字符串
     * @param line 检测线路编号（1-24）
     * @param mainFertMonitor 主肥监控状态（需为"OK"）
     * @param mainFertMotor 主肥电机状态（需为"OK"）
     * @return 速比数值（0-255）或"取消"
     */
    public static String parseMainFertRatio(String hexData, int line, String mainFertMonitor, String mainFertMotor) {
        if (!"OK".equals(mainFertMonitor) || !"OK".equals(mainFertMotor)) {
            return "取消";
        }
        int startAddr = 192 + (line - 1) * 2;
        String valueHex = hexData.substring(startAddr, startAddr + 2);
        int value = Integer.parseInt(valueHex, 16);
        return String.valueOf(value);
    }

    /**
     * 计算断流线阈值（主肥系数地址：334-335）
     * 公式：(主肥系数 * 主肥量速比) / 43
     * @param hexData 十六进制数据字符串
     * @param mainFertRatio 主肥量速比数值
     * @return 断流线计算结果（保留整数）或"取消"
     */
    public static String parseCalcBreakLine(String hexData, int line, String mainFertRatio) {
        if ("取消".equals(mainFertRatio)) {
            return "取消";
        }
        String coeff = parseMainFertSowingCoeffA(hexData,line);
        if ("无".equals(coeff)) {
            return "取消";
        }
        int x = Integer.parseInt(mainFertRatio);
        double result = (Integer.parseInt(coeff) * x) / 43.0;

        // 四舍五入取整处理
        return String.format("%d", Math.round(result));
    }

    /**
     * 计算堵塞线阈值
     * 公式：26 * 主肥量速比
     * @param mainFertRatio 主肥量速比数值
     * @return 堵塞线计算结果（26倍速比）或"取消"
     */
    public static String parseCalcBlockLine(String mainFertRatio) {
        if ("取消".equals(mainFertRatio)) {
            return "取消";
        }
        int x = Integer.parseInt(mainFertRatio);
        return String.valueOf(26 * x);
    }

    /**
     * 解析口肥电机状态（地址范围：396-401）
     * @param hexData 十六进制数据字符串
     * @param line 检测线路编号（1-24）
     * @return "OK"表示电机正常，"取消"表示电机异常
     */
    public static int parseMouthFertMotor(String hexData, int line) {
        String motorHex = hexData.substring(396, 402);
        byte[] bytes = hexStringToByteArray(motorHex);
        int bitIndex = line - 1;
        int byteIndex = 2 - (bitIndex / 8);
        int bitOffset = bitIndex % 8;
        int bitValue = (bytes[byteIndex] >> bitOffset) & 0x01;
        return bitValue;
    }

    /**
     * 解析口肥电机方向（地址范围：378-383）
     * @param hexData 十六进制数据字符串
     * @param line 检测线路编号（1-24）
     * @return "正转"或"反转"
     */
    public static int parseMouthMotorDirection(String hexData, int line) {
        String dirHex = hexData.substring(378, 384);
        byte[] bytes = hexStringToByteArray(dirHex);
        int bitIndex = line - 1;
        int byteIndex = 2 - (bitIndex / 8);
        int bitOffset = bitIndex % 8;
        int bitValue = (bytes[byteIndex] >> bitOffset) & 0x01;
        return bitValue;
    }

    /**
     * 解析种报警记录（地址范围：240-287）
     * @param hexData 十六进制数据字符串
     * @param line 检测线路编号（1-24）
     * @return 报警原因组合字符串（格式：原因+序号）或"无报警"
     */
    public static String parseSeedAlarm(String hexData, int line) {
        // 总报警次数424-425
        int totalAlarms = Integer.parseInt(hexData.substring(424, 426), 16);
        if (totalAlarms == 0) return "无报警";
        // 每个报警占1字节（地址240-287）
        StringBuilder alarms = new StringBuilder();
        for (int i = 0; i < totalAlarms; i++) {
            int addr = 240 + i * 2;
            String alarmHex = hexData.substring(addr, addr + 2);
            int value = Integer.parseInt(alarmHex, 16);
            int lineNo = (value >> 3) & 0x1F; // 高5位为行号
            int reasonCode = value & 0x07;     // 低3位为原因
            if (lineNo == line) {
                String reason;
                switch (reasonCode) {
                    case 7:
                        reason = "未连接";
                        break;
                    case 6:
                        reason = "重复";
                        break;
                    case 5:
                        reason = "种头故障";
                        break;
                    case 4:
                        reason = "电机故障";
                        break;
                    case 3:
                        reason = "断种报警";
                        break;
                    case 2:
                        reason = "多粒报警";
                        break;
                    case 1:
                        reason = "缺种报警";
                        break;
                    default:
                        reason = "未知";
                        break;
                }
                alarms.append(reason).append(i + 1).append("; ");
            }
        }
        return alarms.length() == 0 ? "无报警" : alarms.toString();
    }

    /**
     * 解析肥料报警记录（地址范围：426-497）
     * @param hexData 十六进制数据字符串
     * @param line 检测线路编号（1-24）
     * @return 报警原因组合字符串（格式：原因+报警序号）或"无报警"
     */
    public static String parseFertAlarm(String hexData, int line) {
        // 总报警次数498-499
        int totalAlarms = Integer.parseInt(hexData.substring(498, 500), 16);
        if (totalAlarms == 0) return "无报警";
        // 每个报警占3字节（地址426-497）
        StringBuilder alarms = new StringBuilder();
        for (int i = 0; i < totalAlarms; i++) {
            int addr = 426 + i * 3;
            String alarmHex = hexData.substring(addr, addr + 3);
            int value = Integer.parseInt(alarmHex, 16);
            int lineNo = (value >> 4) & 0xFF;  // 高8位为行号
            int reasonCode = value & 0x0F;      // 低4位为原因
            if (lineNo == line) {
                String reason;
                switch (reasonCode) {
                    case 14:
                        reason = "全取消";
                        break;
                    case 13:
                        reason = "总未连接";
                        break;
                    case 12:
                        reason = "总重复";
                        break;
                    case 11:
                        reason = "主肥电机未连";
                        break;
                    case 10:
                        reason = "口肥电机未连";
                        break;
                    case 9:
                        reason = "主肥电机重复";
                        break;
                    case 8:
                        reason = "口肥电机重复";
                        break;
                    case 7:
                        reason = "主肥电机故障";
                        break;
                    case 6:
                        reason = "口肥电机故障";
                        break;
                    case 5:
                        reason = "肥监控故障";
                        break;
                    case 4:
                        reason = "擦灰";
                        break;
                    case 3:
                        reason = "堵塞";
                        break;
                    case 2:
                        reason = "断流";
                        break;
                    case 1:
                        reason = "缺肥";
                        break;
                    default:
                        reason = "正常";
                        break;
                }
                alarms.append(reason).append(i + 1).append("; ");
            }
        }
        return alarms.length() == 0 ? "无报警" : alarms.toString();
    }

    //-----------------------------B组----------------------------------

    /**
     * 解析电子齿轮比（地址范围：288-289）
     * @param hexData 十六进制数据字符串
     * @return 电子齿轮比值（1-255）
     */
    public static int parseGearRatio(String hexData) {
        String valueHex = hexData.substring(288, 290);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析孔盘数（地址范围：290-291）
     * @param hexData 十六进制数据字符串
     * @return 孔盘数量（20-255）
     */
    public static int parseHoleDiscCount(String hexData) {
        String valueHex = hexData.substring(290, 292);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析株距（地址范围：292-295）
     * @param hexData 十六进制数据字符串
     * @return 株距数值（单位：mm，30-1000）
     */
    public static int parsePlantSpacing(String hexData) {
        String valueHex = hexData.substring(292, 296);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析减速比（地址范围：296-299）
     * @param hexData 十六进制数据字符串
     * @return 减速比（单位：0.1）
     */
    public static int parseReductionRatio(String hexData) {
        String valueHex = hexData.substring(296, 300);
        int rawValue = Integer.parseInt(valueHex, 16);
        return rawValue;
    }

    /**
     * 解析电机脉冲需求（地址范围：300-303）
     * @param hexData 十六进制数据字符串
     * @return 每圈所需脉冲数
     */
    public static int parsePulsesPerRevolution(String hexData) {
        String valueHex = hexData.substring(300, 304);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析反馈脉冲数（地址范围：304-307）
     * @param hexData 十六进制数据字符串
     * @return 每圈反馈脉冲数
     */
    public static int parseFeedbackPulses(String hexData) {
        String valueHex = hexData.substring(304, 308);
        return Integer.parseInt(valueHex, 16);
    }

    //-----------------------------A组----------------------------------
    /**
     * 解析电子齿轮比A组（地址范围：308-309）
     * @param hexData 十六进制数据字符串
     * @return 电子齿轮比值（1-255）
     */
    public static int parseGearRatioA(String hexData) {
        String valueHex = hexData.substring(308, 310);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析孔盘数A组（地址范围：310-311）
     * @param hexData 十六进制数据字符串
     * @return 孔盘数量（20-255）
     */
    public static int parseHoleDiscCountA(String hexData) {
        String valueHex = hexData.substring(310, 312);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析株距A组（地址范围：312-315）
     * @param hexData 十六进制数据字符串
     * @return 株距数值（单位：mm，30-1000）
     */
    public static int parsePlantSpacingA(String hexData) {
        String valueHex = hexData.substring(312, 316);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析减速比A组（地址范围：316-319）
     * @param hexData 十六进制数据字符串
     * @return 减速比（单位：0.1）
     */
    public static int parseReductionRatioA(String hexData) {
        String valueHex = hexData.substring(316, 320);
        int rawValue = Integer.parseInt(valueHex, 16);
        return rawValue;
    }

    /**
     * 解析电机脉冲需求A组（地址范围：320-323）
     * @param hexData 十六进制数据字符串
     * @return 每圈所需脉冲数
     */
    public static int parsePulsesPerRevolutionA(String hexData) {
        String valueHex = hexData.substring(320, 324);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析反馈脉冲数A组（地址范围：324-327）
     * @param hexData 十六进制数据字符串
     * @return 每圈反馈脉冲数
     */
    public static int parseFeedbackPulsesA(String hexData) {
        String valueHex = hexData.substring(324, 328);
        return Integer.parseInt(valueHex, 16);
    }

    //----------------------------- 监控及系数解析方法 -----------------------------

    /**
     * 解析主肥监控断流线（地址范围：328-329）
     * @param hexData 十六进制数据字符串
     * @return 断流线数值（8位）
     */
    public static int parseMainFertBreakLine(String hexData) {
        String valueHex = hexData.substring(328, 330);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析主肥监控堵塞线（地址范围：330-333）
     * @param hexData 十六进制数据字符串
     * @return 堵塞线数值（16位）
     */
    public static int parseMainFertBlockLine(String hexData) {
        String valueHex = hexData.substring(330, 334);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析主肥播种A系数（地址范围：334-335）
     * @param hexData 十六进制数据字符串
     * @return A系数值（8位）或"无"（当主肥电机取消时）
     */
    public static int parseMainFertSowingCoeffA(String hexData) {
        String valueHex = hexData.substring(334, 336);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析主肥播种A系数（地址范围：334-335）
     * @param hexData 十六进制数据字符串
     * @param line 检测线路编号（1-24）
     * @return A系数值（8位）或"无"（当主肥电机取消时）
     */
    public static String parseMainFertSowingCoeffA(String hexData,int line) {
        String valueHex = hexData.substring(334, 336);
        return String.valueOf(Integer.parseInt(valueHex, 16));
    }

    /**
     * 解析口肥播种A系数（地址范围：336-337）
     * @param hexData 十六进制数据字符串
     * @return A系数值（8位）或"无"（当主肥电机取消时）
     */
    public static int parseMouthFertSowingCoeffA(String hexData) {
        String valueHex = hexData.substring(336, 338);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析B主肥系数与A系数比（地址范围：338-339）
     * @param hexData 十六进制数据字符串
     * @return 系数比值（8位）
     */
    public static int parseMainFertCoeffRatioBtoA(String hexData) {
        String valueHex = hexData.substring(338, 340);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析B口肥系数与A系数比（地址范围：340-341）
     * @param hexData 十六进制数据字符串
     * @return 系数比值（8位）
     */
    public static int parseMouthFertCoeffRatioBtoA(String hexData) {
        String valueHex = hexData.substring(340, 342);
        return Integer.parseInt(valueHex, 16);
    }

    //----------------------------- 播种配置解析方法 -----------------------------

    /**
     * 解析状态标志位（地址范围：342-343）
     * @param hexData 十六进制数据字符串
     * @return 包含各状态标志的Map集合
     *         Key定义：mainFertMonitor-主肥监控, mainFertMotor-主肥电机, mouthFertMotor-口肥电机
     *                 seedLevel-种物位, mainFertLevel-主肥物位, mouthFertLevel-口肥物位
     *                 soundAlarm-声音报警, parallelSowing-平行播种
     */
    public static Map<String, String> parseStatusFlags(String hexData) {
        String flagsHex = hexData.substring(342, 344);
        int value = Integer.parseInt(flagsHex, 16);

        Map<String, String> statusMap = new LinkedHashMap<>();
        statusMap.put("mainFertMonitor", (value & 0x80) == 0 ? "取消" : "启用");  // 第1位
        statusMap.put("mainFertMotor", (value & 0x40) == 0 ? "取消" : "启用");    // 第2位
        statusMap.put("mouthFertMotor", (value & 0x20) == 0 ? "取消" : "启用");   // 第3位
        statusMap.put("seedLevel", (value & 0x10) == 0 ? "取消" : "启用");       // 第4位
        statusMap.put("mainFertLevel", (value & 0x08) == 0 ? "取消" : "启用");   // 第5位
        statusMap.put("mouthFertLevel", (value & 0x04) == 0 ? "取消" : "启用");  // 第6位
        statusMap.put("soundAlarm", (value & 0x02) != 0 ? "报警" : "正常");      // 第7位
        statusMap.put("parallelSowing", (value & 0x01) != 0 ? "平行播种" : "常规"); // 第8位
        return statusMap;
    }

    /**
     * 解析播种苗带选择（地址范围：344-345）
     * @param hexData 十六进制数据字符串
     * @return 苗带类型描述（单苗带/双苗带/三苗带）
     */
    public static int parseSeedBeltSelection(String hexData) {
        String valueHex = hexData.substring(344, 346);
        int value = Integer.parseInt(valueHex, 16);

//        switch (value) {
//            case 0: return "单苗带";
//            case 1: return "双苗带";
//            case 2: return "三苗带";
//            default: return "未知类型";
//        }
        return value;
    }

    /**
     * 解析播种行数（地址范围：346-347）
     * @param hexData 十六进制数据字符串
     * @return 有效行数（1-24）
     */
    public static int parseSowingLines(String hexData) {
        String valueHex = hexData.substring(346, 348);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析总播种宽度（地址范围：348-351）
     * @param hexData 十六进制数据字符串
     * @return 播种宽度数值（单位：毫米）
     */
    public static int parseTotalSowingWidth(String hexData) {
        String valueHex = hexData.substring(348, 352);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析种预判时间（地址范围：352-353）
     * @param hexData 十六进制数据字符串
     * @return 预判时间（单位：毫秒）
     */
    public static int parseSeedPredictTime(String hexData) {
        String valueHex = hexData.substring(352, 354);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析急停阈值（地址范围：354-355）
     * @param hexData 十六进制数据字符串
     * @return 急停阈值数值
     */
    public static int parseEmergencyStopThreshold(String hexData) {
        String valueHex = hexData.substring(354, 356);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析主肥缺肥系数（地址范围：356-357）
     * @param hexData 十六进制数据字符串
     * @return 缺肥系数值
     */
    public static int parseMainFertDeficientCoeff(String hexData) {
        String valueHex = hexData.substring(356, 358);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析主肥测试肥量（地址范围：364-367）
     * @param hexData 十六进制数据字符串
     * @return 测试肥量数值（16位）
     */
    public static int parseMainFertTestQuantity(String hexData) {
        String valueHex = hexData.substring(364, 368);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析口肥测试肥量（地址范围：368-371）
     * @param hexData 十六进制数据字符串
     * @return 测试肥量数值（16位）
     */
    public static int parseMouthFertTestQuantity(String hexData) {
        String valueHex = hexData.substring(368, 372);
        return Integer.parseInt(valueHex, 16);
    }

    //----------------------------- 运行状态解析方法 -----------------------------

    /**
     * 解析播种运行时间（地址范围：408-411）
     * @param hexData 十六进制数据字符串
     * @return 运行时间（单位：秒）
     */
    public static int parseSowingRuntime(String hexData) {
        String valueHex = hexData.substring(408, 412);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析播种运行距离（地址范围：412-415）
     * @param hexData 十六进制数据字符串
     * @return 运行距离（单位：米）
     */
    public static int parseSowingDistance(String hexData) {
        String valueHex = hexData.substring(412, 416);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析雷达频率（地址范围：416-417）
     * @param hexData 十六进制数据字符串
     * @return 雷达频率值
     */
    public static int parseRadarFrequency(String hexData) {
        String valueHex = hexData.substring(416, 418);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析系统电压（地址范围：418-421）
     * @param hexData 十六进制数据字符串
     * @return 电压值（单位：0.1伏）
     */
    public static int parseSystemVoltage(String hexData) {
        String valueHex = hexData.substring(418, 422);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析系统状态（地址范围：422-423）
     * @param hexData 十六进制数据字符串
     * @return 包含8位状态标志的Map集合
     */
    public static Map<String, Integer> parseSystemStatus(String hexData) {
        String statusHex = hexData.substring(422, 424);
        int value = Integer.parseInt(statusHex, 16);

        Map<String, Integer> statusMap = new LinkedHashMap<>();
        for (int i = 0; i < 8; i++) {
            statusMap.put("bit" + (i+1), (value >> (7 - i)) & 0x01);
        }
        return statusMap;
    }

    //----------------------------- 风机及状态解析方法 -----------------------------

    /**
     * 解析风机压力（地址范围：500-503）
     * @param hexData 十六进制数据字符串
     * @return 压力值（单位：0.01千帕）
     */
    public static int parseFanPressure(String hexData) {
        String valueHex = hexData.substring(500, 504);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析风机转速（地址范围：504-507）
     * @param hexData 十六进制数据字符串
     * @return 转速值（单位：转/分钟）
     */
    public static int parseFanSpeed(String hexData) {
        String valueHex = hexData.substring(504, 508);
        return Integer.parseInt(valueHex, 16);
    }

    /**
     * 解析数据上传类型（地址范围：508）
     * @param hexData 十六进制数据字符串
     * @return 数据上传类型描述
     */
    public static int parseUploadType(String hexData) {
        String valueHex = hexData.substring(508, 509);
        int value = Integer.parseInt(valueHex, 16);

//        switch (value) {
//            case 0:
//                return "蓝牙连接";
//            case 1:
//                // 解析412-415地址段的单次播种距离，单位1米
//                int distance = parseSowingDistance(hexData);
//                return distance + "米";
//            case 2:
//                return "限制";
//            default:
//                return "未知类型";
//        }
        
        return value;
    }

    /**
     * 解析剩余停止里程（地址范围：509-511）
     * @param hexData 十六进制数据字符串
     * @return 剩余路程数值（单位：米）
     */
    public static int parseRemainingDistance(String hexData) {
        String valueHex = hexData.substring(509, 512);
        return Integer.parseInt(valueHex, 16);
    }


    //----------------------------- 工具方法 -----------------------------

    // 十六进制字符串转字节数组（兼容Java 8）
    private static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("十六进制长度必须为偶数");
        }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 处理每个字符
            int high = Character.digit(hex.charAt(i), 16);
            int low = Character.digit(hex.charAt(i+1), 16);

            if (high == -1 || low == -1) {
                throw new IllegalArgumentException("包含非法十六进制字符");
            }

            data[i/2] = (byte) ((high << 4) + low);
        }
        return data;
    }

    // 字节数组转十六进制字符串（用于日志）
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }

    /**
     * 十六进制字符串转字节数组
     * @param hex 有效的十六进制字符串（长度必须为偶数）
     * @return 转换后的字节数组
     * @throws NumberFormatException 当包含非十六进制字符时抛出
     */
    public static byte[] hexStringToByteArray1(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 计算平均播种速度。
     *
     * 该函数通过解析传入的十六进制数据，获取播种距离和运行时间，并计算平均速度。
     *
     * @param hexData 包含播种距离和运行时间的十六进制字符串数据
     * @return 格式化后的平均速度字符串，单位为km/h，保留一位小数
     */
    public static String averageSpeed(String hexData) {
        // 解析412-415地址段--单次播种距离，单位1米，并转换为厘米
        int distance = parseSowingDistance(hexData) * 36;

        // 解析408-411地址段--单次播种运行时记录的运行时间
        int runtime = parseSowingRuntime(hexData);

        // 检查runtime是否为0，避免除零异常
        if (runtime == 0) {
            return "0.0km/h";
        }

        // 使用浮点数除法以提高精度，并转换为km/h单位
        double v = (double) distance / runtime * 0.1;

        // 使用String.format格式化输出字符串，保留一位小数
        return String.format("%.1fkm/h", v);
    }

}
