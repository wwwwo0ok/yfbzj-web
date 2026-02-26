package com.company.project.util;
import java.util.Arrays;

public class SerialPortUtils {

    /**
     * 将表示十六进制数据的字符串转换为原始的字节数组。
     * 例如：输入 "48656C6C6F" -> 输出 byte[] {0x48, 0x65, 0x6C, 0x6C, 0x6F} (即 "Hello" 的ASCII码)
     *
     * @param hexString 包含十六进制字符的字符串（例如 "4A3F"）。不区分大小写。
     * @return 转换后的原始字节数组。
     * @throws IllegalArgumentException 如果输入为null、长度不是偶数、或包含非十六进制字符。
     */
    public static byte[] hexStringToByteArray(String hexString) {
        // 1. 输入验证
        if (hexString == null) {
            throw new IllegalArgumentException("输入字符串不能为 null");
        }
        // 去除可能存在的空格或换行符，使解析更健壮
        String cleanedHexString = hexString.replaceAll("\\s", "").toUpperCase();
        int length = cleanedHexString.length();

        if (length % 2 != 0) {
            throw new IllegalArgumentException("无效的十六进制字符串：长度必须是偶数。当前长度: " + length);
        }

        // 2. 初始化结果数组
        // 每两个十六进制字符对应一个字节，所以数组长度是字符串长度的一半
        byte[] byteArray = new byte[length / 2];

        // 3. 遍历字符串并转换
        for (int i = 0; i < length; i += 2) {
            // 每次取两个字符，例如 "48"
            String hexByte = cleanedHexString.substring(i, i + 2);
            try {
                // 将两个字符的十六进制字符串解析为一个整数（0-255）
                int intValue = Integer.parseInt(hexByte, 16);
                // 将整数强制转换为byte类型（会自动取低8位）
                byteArray[i / 2] = (byte) intValue;
            } catch (NumberFormatException e) {
                // 如果子字符串不是有效的十六进制（如 "GH"），则抛出异常
                throw new IllegalArgumentException("无效的十六进制字符: '" + hexByte + "'", e);
            }
        }

        return byteArray;
    }

    // --- 以下为测试代码 ---
    public static void main(String[] args) {
        // 模拟从串口读取到的十六进制字符串
        // 这个例子代表ASCII字符串 "Hello" 的十六进制表示
        String receivedHexData = "48656C6C6F"; 
        
        // 另一个例子，包含非ASCII字符和更大的数值
        String fanSpeedHex = "000009C4"; // 代表十进制 2500

        try {
            // 调用转换方法
            byte[] helloBytes = hexStringToByteArray(receivedHexData);
            byte[] speedBytes = hexStringToByteArray(fanSpeedHex);

            // 打印结果进行验证
            System.out.println("原始字符串: " + receivedHexData);
            System.out.println("转换后的字节数组 (十六进制表示): " + bytesToHexString(helloBytes));
            System.out.println("转换后的字节数组 (内容): " + Arrays.toString(helloBytes));
            // 将字节数组用默认字符集（如UTF-8）解码回字符串，验证是否正确
            System.out.println("解码回字符串: " + new String(helloBytes, "US-ASCII")); // 使用US-ASCII确保单字节解码

            System.out.println("\n原始转速数据: " + fanSpeedHex);
            System.out.println("转换后的字节数组: " + Arrays.toString(speedBytes));
            // 将两个字节组合成一个整数（大端序）
            int fanSpeed = ((speedBytes[0] & 0xFF) << 8) | (speedBytes[1] & 0xFF);
            fanSpeed = (fanSpeed << 16) | ((speedBytes[2] & 0xFF) << 8) | (speedBytes[3] & 0xFF);
            System.out.println("解析出的转速值: " + fanSpeed); // 输出 2500

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 辅助方法：将字节数组转换回十六进制字符串，用于打印调试
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
