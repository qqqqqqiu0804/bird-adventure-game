package cn.edu.huse.game;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 障碍物皮肤类 - 飞翔小鸟冒险版
 */
public class ObstacleSkin {
    String name;
    String description;
    boolean unlocked;
    int price;

    // 颜色配置
    Color bodyColor;      // 主体颜色
    Color borderColor;    // 边框颜色
    Color lightColor;     // 灯光颜色
    Color windowColor;    // 窗户颜色
    String pattern;       // 图案类型

    /**
     * 构造方法
     */
    public ObstacleSkin(String name, String description, boolean unlocked, int price,
                        Color bodyColor, Color borderColor, Color lightColor, Color windowColor, String pattern) {
        this.name = name;
        this.description = description;
        this.unlocked = unlocked;
        this.price = price;
        this.bodyColor = bodyColor;
        this.borderColor = borderColor;
        this.lightColor = lightColor;
        this.windowColor = windowColor;
        this.pattern = pattern;
    }

    /**
     * 绘制障碍物
     */
    public void draw(Graphics g, int x, int y, int w, int h, float pulse, boolean isTop) {
        Graphics2D g2 = (Graphics2D) g;

        // 根据图案类型绘制
        switch (pattern) {
            case "spacestation":
                drawSpaceStation(g, x, y, w, h, pulse, isTop);
                break;
            case "bamboo":
                drawBamboo(g, x, y, w, h, pulse, isTop);
                break;
            case "ice":
                drawIce(g, x, y, w, h, pulse, isTop);
                break;
            case "crystal":
                drawCrystal(g, x, y, w, h, pulse, isTop);
                break;
            case "wood":
                drawWood(g, x, y, w, h, pulse, isTop);
                break;
            default:
                drawSpaceStation(g, x, y, w, h, pulse, isTop);
                break;
        }
    }

    /**
     * 绘制太空站风格
     */
    private void drawSpaceStation(Graphics g, int x, int y, int w, int h, float pulse, boolean isTop) {
        // 主体
        g.setColor(bodyColor);
        g.fillRect(x, y, w, h);

        // 边框高光
        g.setColor(borderColor);
        g.drawLine(x, y, x, y + h);
        g.drawLine(x + w, y, x + w, y + h);

        // 灯光条
        int lightY = isTop ? y + h - 12 : y;
        int glowG = (int)(lightColor.getGreen() * pulse);
        int glowB = (int)(lightColor.getBlue() * pulse);
        g.setColor(new Color(lightColor.getRed(), glowG, glowB));
        g.fillRect(x, lightY, w, 12);

        // 窗户
        for (int wy = y + 25; wy < y + h - 30; wy += 50) {
            g.setColor(new Color(20, 30, 50));
            g.fillRect(x + w / 4, wy, w / 2, 20);
            int glow = (int)(120 * pulse + 60);
            g.setColor(new Color(windowColor.getRed() * glow / 255, windowColor.getGreen() * glow / 255, windowColor.getBlue() * glow / 255));
            g.fillRect(x + w / 4 + 2, wy + 2, w / 2 - 4, 16);
        }

        // 天线
        drawAntenna(g, x, y, w, h, isTop);
    }

    /**
     * 绘制竹子风格
     */
    private void drawBamboo(Graphics g, int x, int y, int w, int h, float pulse, boolean isTop) {
        // 竹子主体 - 绿色渐变
        for (int i = 0; i < h; i++) {
            int green = 120 + (int)(30 * Math.sin(i * 0.1));
            g.setColor(new Color(50, green, 30));
            g.drawLine(x, y + i, x + w, y + i);
        }

        // 竹节
        g.setColor(new Color(40, 100, 25));
        for (int i = 0; i < h; i += 40) {
            g.fillRect(x, y + i, w, 6);
        }

        // 竹叶装饰
        g.setColor(new Color(30, 150, 20, 180));
        if (isTop) {
            g.fillOval(x - 10, y + h - 30, 30, 20);
            g.fillOval(x + w - 20, y + h - 25, 30, 20);
        } else {
            g.fillOval(x - 10, y + 10, 30, 20);
            g.fillOval(x + w - 20, y + 15, 30, 20);
        }
    }

    /**
     * 绘制冰块风格
     */
    private void drawIce(Graphics g, int x, int y, int w, int h, float pulse, boolean isTop) {
        // 冰块主体 - 蓝白色
        g.setColor(new Color(200, 230, 255));
        g.fillRect(x, y, w, h);

        // 冰块纹理
        g.setColor(new Color(150, 200, 240, 100));
        for (int i = 0; i < 5; i++) {
            int px = x + (int)(Math.random() * w);
            int py = y + (int)(Math.random() * h);
            g.fillOval(px, py, 20, 15);
        }

        // 冰霜效果
        g.setColor(new Color(255, 255, 255, 150));
        for (int i = 0; i < 8; i++) {
            int px = x + (int)(Math.random() * w);
            int py = y + (int)(Math.random() * h);
            g.fillOval(px, py, 5, 5);
        }

        // 边框
        g.setColor(new Color(100, 180, 230));
        g.drawRect(x, y, w, h);
    }

    /**
     * 绘制水晶风格
     */
    private void drawCrystal(Graphics g, int x, int y, int w, int h, float pulse, boolean isTop) {
        // 水晶主体 - 紫色渐变
        for (int i = 0; i < h; i++) {
            int purple = 150 + (int)(50 * Math.sin(i * 0.05 + pulse * 3));
            g.setColor(new Color(purple, 50, 200));
            g.drawLine(x, y + i, x + w, y + i);
        }

        // 水晶光泽
        g.setColor(new Color(255, 200, 255, 100));
        for (int i = 0; i < 3; i++) {
            int px = x + w / 4 + i * w / 6;
            g.drawLine(px, y, px + 10, y + h);
        }

        // 发光效果
        int glow = (int)(100 * pulse + 50);
        g.setColor(new Color(200, 100, 255, glow));
        g.fillRect(x + 2, y + 2, w - 4, h - 4);
    }

    /**
     * 绘制木头风格
     */
    private void drawWood(Graphics g, int x, int y, int w, int h, float pulse, boolean isTop) {
        // 木头主体 - 棕色
        g.setColor(new Color(139, 90, 43));
        g.fillRect(x, y, w, h);

        // 木纹
        g.setColor(new Color(120, 75, 35));
        for (int i = 0; i < h; i += 8) {
            g.drawLine(x, y + i, x + w, y + i);
        }

        // 木结
        g.setColor(new Color(100, 60, 25));
        for (int i = 0; i < 3; i++) {
            int px = x + 10 + i * w / 3;
            int py = y + 20 + i * h / 4;
            g.fillOval(px, py, 15, 12);
        }

        // 边框
        g.setColor(new Color(100, 65, 30));
        g.drawRect(x, y, w, h);
    }

    /**
     * 绘制天线
     */
    private void drawAntenna(Graphics g, int x, int y, int w, int h, boolean isTop) {
        int antennaX = x + w / 2;
        g.setColor(new Color(180, 180, 200));
        if (isTop) {
            g.drawLine(antennaX, y, antennaX, y - 18);
            g.setColor(new Color(255, 50, 50));
            g.fillOval(antennaX - 4, y - 22, 8, 8);
        } else {
            g.drawLine(antennaX, y + h, antennaX, y + h + 18);
            g.setColor(new Color(255, 255, 50));
            g.fillOval(antennaX - 3, y + h + 14, 6, 6);
        }
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isUnlocked() { return unlocked; }
    public int getPrice() { return price; }
    public void unlock() { this.unlocked = true; }
}
