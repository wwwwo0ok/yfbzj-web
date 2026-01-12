package com.company.project.util;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * 自定义算术验证码（不依赖JavaScript引擎）
 */
public class ArithmeticCaptchaWithoutJS {
    // 验证码的宽高
    private int width = 130;
    private int height = 48;
    
    // 算术表达式
    private String arithmeticExpression;
    // 计算结果
    private String result;
    // 操作数个数（默认2个数字）
    private int len = 2;
    
    private Random random = new Random();
    
    public ArithmeticCaptchaWithoutJS() {
        generateExpression();
    }
    
    public ArithmeticCaptchaWithoutJS(int width, int height) {
        this.width = width;
        this.height = height;
        generateExpression();
    }
    
    /**
     * 生成算术表达式
     */
    private void generateExpression() {
        // 生成两个数字和一个运算符
        int num1 = random.nextInt(10) + 1; // 1-10
        int num2 = random.nextInt(10) + 1; // 1-10
        
        // 运算符：+、-、×
        String[] operators = {"+", "-", "×"};
        String operator = operators[random.nextInt(operators.length)];
        
        // 构建表达式
        if (len == 2) {
            arithmeticExpression = num1 + " " + operator + " " + num2;
            result = calculate(arithmeticExpression);
        } else {
            // 三个数字的情况
            int num3 = random.nextInt(10) + 1;
            String operator2 = operators[random.nextInt(operators.length)];
            arithmeticExpression = num1 + " " + operator + " " + num2 + " " + operator2 + " " + num3;
            result = calculate(arithmeticExpression);
        }
    }
    
    /**
     * 使用 exp4j 计算表达式
     */
    private String calculate(String expression) {
        try {
            // 将"×"替换为"*"用于计算
            String mathExpression = expression.replace("×", "*");
            
            // 使用 exp4j 计算
            Expression exp = new ExpressionBuilder(mathExpression)
                    .build();
            double value = exp.evaluate();
            
            // 如果是整数，返回整数形式
            if (value == (int) value) {
                return String.valueOf((int) value);
            } else {
                // 处理小数，通常验证码只需要整数
                return String.valueOf((int) Math.round(value));
            }
        } catch (Exception e) {
            // 如果计算失败，返回一个默认值
            return "0";
        }
    }
    
    /**
     * 获取验证码文本（答案）
     */
    public String text() {
        return result;
    }
    
    /**
     * 获取算术表达式（不包含结果）
     */
    public String getArithmeticString() {
        return arithmeticExpression + " = ?";
    }
    
    /**
     * 输出验证码图片
     */
    public void out(OutputStream out) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 设置背景色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 设置字体
        Font font = new Font("Arial", Font.BOLD, 20);
        g2d.setFont(font);
        
        // 设置文本颜色
        g2d.setColor(Color.BLACK);
        
        // 绘制算术表达式
        String text = getArithmeticString();
        FontMetrics metrics = g2d.getFontMetrics(font);
        int x = (width - metrics.stringWidth(text)) / 2;
        int y = (height - metrics.getHeight()) / 2 + metrics.getAscent();
        
        g2d.drawString(text, x, y);
        
        // 添加干扰线
        drawInterferenceLine(g2d);
        
        // 添加噪点
        drawNoise(image);
        
        g2d.dispose();
        
        // 输出图片
        ImageIO.write(image, "jpg", out);
    }
    
    /**
     * 绘制干扰线
     */
    private void drawInterferenceLine(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 5; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
    
    /**
     * 添加噪点
     */
    private void drawNoise(BufferedImage image) {
        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int rgb = random.nextInt(256);
            image.setRGB(x, y, new Color(rgb, rgb, rgb).getRGB());
        }
    }
    
    // Getters and Setters
    public void setLen(int len) {
        this.len = len;
        generateExpression();
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
}