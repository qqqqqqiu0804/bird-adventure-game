package cn.edu.huse.game;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * 场景类 - 飞翔小鸟冒险版
 */
public class Scene {
    String name;
    String description;
    BufferedImage background;
    boolean unlocked;
    int price;
    String theme;  // 场景主题

    /**
     * 构造方法
     */
    public Scene(String name, String description, String backgroundFile, boolean unlocked, int price, String theme) throws Exception {
        this.name = name;
        this.description = description;
        this.background = ImageIO.read(getClass().getResource(backgroundFile));
        this.unlocked = unlocked;
        this.price = price;
        this.theme = theme;
        // 应用主题
        applyTheme();
    }

    /**
     * 应用主题到背景
     */
    private void applyTheme() {
        if (theme == null) return;

        BufferedImage themed = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = themed.createGraphics();

        // 绘制基础背景
        g.drawImage(background, 0, 0, null);

        // 根据主题添加图案
        switch (theme) {
            case "deepsea":
                drawDeepSea(g, themed.getWidth(), themed.getHeight());
                break;
            case "forest":
                drawForest(g, themed.getWidth(), themed.getHeight());
                break;
            case "desert":
                drawDesert(g, themed.getWidth(), themed.getHeight());
                break;
            case "volcano":
                drawVolcano(g, themed.getWidth(), themed.getHeight());
                break;
        }

        g.dispose();
        background = themed;
    }

    /**
     * 绘制深海主题
     */
    private void drawDeepSea(Graphics2D g, int w, int h) {
        // 蓝色叠加
        g.setColor(new Color(0, 50, 150, 100));
        g.fillRect(0, 0, w, h);

        // 气泡
        g.setColor(new Color(200, 230, 255, 80));
        for (int i = 0; i < 15; i++) {
            int x = (int)(Math.random() * w);
            int y = (int)(Math.random() * h);
            int size = 5 + (int)(Math.random() * 15);
            g.fillOval(x, y, size, size);
        }

        // 海草
        g.setColor(new Color(0, 150, 100, 120));
        for (int i = 0; i < 5; i++) {
            int x = 50 + i * 120;
            g.fillRect(x, h - 100, 8, 100);
            g.fillOval(x - 10, h - 120, 28, 30);
        }
    }

    /**
     * 绘制森林主题
     */
    private void drawForest(Graphics2D g, int w, int h) {
        // 绿色叠加
        g.setColor(new Color(0, 100, 0, 80));
        g.fillRect(0, 0, w, h);

        // 树木
        g.setColor(new Color(100, 60, 20, 150));
        for (int i = 0; i < 6; i++) {
            int x = 30 + i * 100;
            g.fillRect(x, h - 150, 20, 150);
        }

        // 树冠
        g.setColor(new Color(0, 120, 0, 150));
        for (int i = 0; i < 6; i++) {
            int x = 30 + i * 100;
            g.fillOval(x - 25, h - 200, 70, 70);
            g.fillOval(x - 15, h - 230, 50, 50);
        }
    }

    /**
     * 绘制沙漠主题
     */
    private void drawDesert(Graphics2D g, int w, int h) {
        // 黄色叠加
        g.setColor(new Color(200, 150, 50, 80));
        g.fillRect(0, 0, w, h);

        // 沙丘
        g.setColor(new Color(220, 180, 80, 120));
        for (int i = 0; i < 4; i++) {
            int x = i * 180;
            g.fillOval(x, h - 80, 200, 100);
        }

        // 仙人掌
        g.setColor(new Color(0, 150, 0, 150));
        for (int i = 0; i < 3; i++) {
            int x = 100 + i * 200;
            g.fillRect(x, h - 120, 12, 80);
            g.fillRect(x - 15, h - 100, 12, 30);
            g.fillRect(x + 15, h - 110, 12, 40);
        }
    }

    /**
     * 绘制火山主题
     */
    private void drawVolcano(Graphics2D g, int w, int h) {
        // 红色叠加
        g.setColor(new Color(200, 30, 0, 80));
        g.fillRect(0, 0, w, h);

        // 火山
        g.setColor(new Color(100, 50, 20, 180));
        int[] xPoints = {200, 300, 400};
        int[] yPoints = {h, h - 200, h};
        g.fillPolygon(xPoints, yPoints, 3);

        // 熔岩流
        g.setColor(new Color(255, 100, 0, 150));
        g.fillRect(280, h - 180, 40, 180);

        // 火星
        g.setColor(new Color(255, 200, 0, 180));
        for (int i = 0; i < 10; i++) {
            int x = 250 + (int)(Math.random() * 100);
            int y = h - 250 + (int)(Math.random() * 100);
            g.fillOval(x, y, 5, 5);
        }
    }

    /**
     * 获取场景名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取场景描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取背景图片
     */
    public BufferedImage getBackground() {
        return background;
    }

    /**
     * 检查是否已解锁
     */
    public boolean isUnlocked() {
        return unlocked;
    }

    /**
     * 解锁场景
     */
    public void unlock() {
        unlocked = true;
    }

    /**
     * 获取价格
     */
    public int getPrice() {
        return price;
    }
}
