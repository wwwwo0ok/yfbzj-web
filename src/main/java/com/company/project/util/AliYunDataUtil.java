package com.company.project.util;

/**
 *  与阿里云相关的工具类
 */
public final class AliYunDataUtil {

	
	/**
	 * 将阿里云的数据转化为字符串
	 * @param code
	 * @return
	 */
	public static String aliCode2Str(String code) {
		
		 String input = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000800000000000000000000000000000000000000000000000404040000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001E006400C80C8009C4809E0064003C0C8003E8138787B00A026583050303CF7F7F87800000022B001600000000000000001F80000D80001F80000D000000000400750100000000000000000000000000000000000000000000000000000000000000000000000000006F5000003FFF";
	        
		 int length = input.length();
		 
	        // 1. 每2个字符分割
	        if (input.length() % 2 != 0) {
	            throw new IllegalArgumentException("输入字符串长度必须为偶数");
	        }

	        StringBuilder result = new StringBuilder();
	        for (int i = 0; i < input.length(); i += 2) {
	            // 2. 提取2个字符并解析为整数
	            String hexStr = input.substring(i, i + 2);
	            int charCode = Integer.parseInt(hexStr, 16); // 按16进制解析（如果是十进制，改用10）
	            
	            // 3. 转换为字符并追加到结果
	            result.append((char) charCode);
	        }

	        // 输出结果
	        System.out.println("解码后的字符串：");
	        System.out.println(result.toString());
	        
	        
	        return result.toString();
		
	}
	
	
}
