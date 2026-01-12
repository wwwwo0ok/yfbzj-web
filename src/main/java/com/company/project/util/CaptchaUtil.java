package com.company.project.util;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 验证码工具类
 */
public class CaptchaUtil {
    
    /**
     * 输出验证码图片到响应流
     */
    public static void out(ArithmeticCaptchaWithoutJS captcha, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应头
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        
        // 将验证码文本存入session
        HttpSession session = request.getSession();
        session.setAttribute("captcha", captcha.text());
        
        // 输出图片
        captcha.out(response.getOutputStream());
    }
    
    /**
     * 验证验证码
     */
    public static boolean verify(String code, HttpServletRequest request) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        
        HttpSession session = request.getSession();
        String captcha = (String) session.getAttribute("captcha");
        
        if (captcha == null) {
            return false;
        }
        
        // 清除session中的验证码（一次性使用）
        session.removeAttribute("captcha");
        
        return code.equalsIgnoreCase(captcha);
    }
}