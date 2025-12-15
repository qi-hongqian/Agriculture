package com.rfeng.agri.util;

/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/12/15 17:29
 */
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;
import javax.imageio.ImageIO;

public class CaptchaUtil {

    private static final int WIDTH = 100;
    private static final int HEIGHT = 40;
    private static final int FONT_SIZE = 30;
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final Random RANDOM = new Random();

    public static class CaptchaResult {
        private String code;
        private String imageBase64;

        public CaptchaResult(String code, String imageBase64) {
            this.code = code;
            this.imageBase64 = imageBase64;
        }

        public String getCode() {
            return code;
        }

        public String getImageBase64() {
            return imageBase64;
        }
    }

    public static CaptchaResult generateCaptcha() {
        String code = generateCode(6);
        String imageBase64 = generateImage(code);
        return new CaptchaResult(code, imageBase64);
    }

    private static String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private static String generateImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        addNoiseLines(g2d);

        g2d.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
        drawCode(g2d, code);

        g2d.dispose();

        return imageToBase64(image);
    }

    private static void addNoiseLines(Graphics2D g2d) {
        g2d.setColor(new Color(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255)));
        for (int i = 0; i < 3; i++) {
            int x1 = RANDOM.nextInt(WIDTH);
            int y1 = RANDOM.nextInt(HEIGHT);
            int x2 = RANDOM.nextInt(WIDTH);
            int y2 = RANDOM.nextInt(HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    private static void drawCode(Graphics2D g2d, String code) {
        int x = 5;
        for (char c : code.toCharArray()) {
            int randomY = 5 + RANDOM.nextInt(10);
            g2d.setColor(new Color(RANDOM.nextInt(100), RANDOM.nextInt(100), RANDOM.nextInt(100)));
            g2d.drawString(String.valueOf(c), x, HEIGHT - randomY);
            x += 15;
        }
    }

    private static String imageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
