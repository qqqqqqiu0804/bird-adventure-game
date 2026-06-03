package cn.edu.huse.game;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * 皮肤类 - 飞翔小鸟冒险版
 */
public class Skin {
    String name;
    String description;
    BufferedImage image;
    BufferedImage[] animationFrames;  // 动画帧
    boolean unlocked;
    int price;

    /**
     * 构造方法
     */
    public Skin(String name, String description, String imageFile, boolean unlocked, int price) throws Exception {
        this.name = name;
        this.description = description;
        this.image = ImageIO.read(getClass().getResource(imageFile));
        this.unlocked = unlocked;
        this.price = price;

        // 加载动画帧
        this.animationFrames = new BufferedImage[3];
        // 根据图片文件名推断基础名称
        String baseName = imageFile.replaceAll("[0-9]\\.png$", "");
        for (int i = 0; i < 3; i++) {
            try {
                String frameFile = baseName + i + ".png";
                animationFrames[i] = ImageIO.read(getClass().getResource(frameFile));
            } catch (Exception e) {
                animationFrames[i] = this.image;  // 如果找不到，使用静态图片
            }
        }
    }

    /**
     * 获取皮肤名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取皮肤描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取皮肤图片
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * 获取动画帧
     */
    public BufferedImage[] getAnimationFrames() {
        return animationFrames;
    }

    /**
     * 检查是否已解锁
     */
    public boolean isUnlocked() {
        return unlocked;
    }

    /**
     * 解锁皮肤
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
