package cn.edu.huse.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 教程步骤
 */
class TutorialStep {
    String text;           // 说明文字
    int highlightX;        // 高亮区域X
    int highlightY;        // 高亮区域Y
    int highlightWidth;    // 高亮区域宽度
    int highlightHeight;   // 高亮区域高度
    String animationType;  // 动画类型

    public TutorialStep(String text, int highlightX, int highlightY,
                        int highlightWidth, int highlightHeight, String animationType) {
        this.text = text;
        this.highlightX = highlightX;
        this.highlightY = highlightY;
        this.highlightWidth = highlightWidth;
        this.highlightHeight = highlightHeight;
        this.animationType = animationType;
    }
}

/**
 * 新手教程管理器 - 飞翔小鸟冒险版
 */
public class TutorialManager {
    private boolean completed;
    private int currentStep;
    private List<TutorialStep> steps;
    private long animationTime;

    /**
     * 构造方法
     */
    public TutorialManager() {
        completed = false;
        currentStep = 0;
        steps = new ArrayList<>();
        animationTime = 0;
        initSteps();
    }

    /**
     * 初始化教程步骤
     */
    private void initSteps() {
        // 步骤1：点击屏幕让小鸟飞翔
        steps.add(new TutorialStep(
            "点击屏幕或按空格键让小鸟飞翔",
            GameConfig.WIDTH / 2 - 50, GameConfig.HEIGHT / 2 - 50,
            100, 100, "tap"
        ));

        // 步骤2：收集金币获得分数
        steps.add(new TutorialStep(
            "收集金币获得分数",
            GameConfig.WIDTH / 2, GameConfig.HEIGHT / 2 - 100,
            50, 50, "coin"
        ));

        // 步骤3：躲避障碍物
        steps.add(new TutorialStep(
            "躲避障碍物，祝你好运！",
            GameConfig.WIDTH / 2 + 100, GameConfig.HEIGHT / 2 - 150,
            80, 300, "obstacle"
        ));
    }

    /**
     * 检查是否首次启动
     * @return 是否首次启动
     */
    public boolean isFirstLaunch() {
        return !completed;
    }

    /**
     * 显示教程
     * @param g Graphics对象
     */
    public void showTutorial(Graphics g) {
        if (completed || currentStep >= steps.size()) return;

        TutorialStep step = steps.get(currentStep);
        Graphics2D g2 = (Graphics2D) g;

        // 更新动画时间
        animationTime += 16; // 假设60FPS

        // 半透明遮罩
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        // 高亮区域（透明窗口）
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        g2.fillRect(step.highlightX, step.highlightY, step.highlightWidth, step.highlightHeight);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

        // 绘制高亮边框
        g2.setColor(GameConfig.COLOR_ACCENT);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(step.highlightX, step.highlightY, step.highlightWidth, step.highlightHeight);

        // 绘制动画效果
        drawAnimation(g2, step);

        // 绘制说明文字
        g2.setColor(GameConfig.COLOR_TEXT);
        g2.setFont(GameConfig.SUBTITLE_FONT);
        FontMetrics fm = g2.getFontMetrics();
        int textX = (GameConfig.WIDTH - fm.stringWidth(step.text)) / 2;
        int textY = GameConfig.HEIGHT / 2 + 100;
        g2.drawString(step.text, textX, textY);

        // 绘制步骤指示器
        g2.setColor(GameConfig.COLOR_TEXT_SECONDARY);
        g2.setFont(GameConfig.BODY_FONT);
        String stepText = (currentStep + 1) + " / " + steps.size();
        fm = g2.getFontMetrics();
        g2.drawString(stepText, (GameConfig.WIDTH - fm.stringWidth(stepText)) / 2, textY + 30);

        // 绘制跳过按钮
        g2.setColor(GameConfig.COLOR_WARNING);
        g2.setFont(GameConfig.BODY_FONT);
        String skipText = "跳过教程";
        fm = g2.getFontMetrics();
        g2.drawString(skipText, GameConfig.WIDTH - fm.stringWidth(skipText) - 20, GameConfig.HEIGHT - 20);

        // 绘制继续提示
        float alpha = (float)(Math.sin(animationTime * 0.005) * 0.5 + 0.5);
        g2.setColor(new Color(255, 255, 255, (int)(200 * alpha)));
        String continueText = "点击任意位置继续";
        fm = g2.getFontMetrics();
        g2.drawString(continueText, (GameConfig.WIDTH - fm.stringWidth(continueText)) / 2, textY + 60);
    }

    /**
     * 绘制动画效果
     * @param g2 Graphics2D对象
     * @param step 教程步骤
     */
    private void drawAnimation(Graphics2D g2, TutorialStep step) {
        switch (step.animationType) {
            case "tap":
                // 手指点击动画
                drawTapAnimation(g2, step.highlightX + step.highlightWidth / 2,
                                step.highlightY + step.highlightHeight / 2);
                break;
            case "coin":
                // 金币闪烁动画
                drawCoinAnimation(g2, step.highlightX, step.highlightY);
                break;
            case "obstacle":
                // 箭头指示动画
                drawObstacleAnimation(g2, step.highlightX, step.highlightY);
                break;
        }
    }

    /**
     * 绘制点击动画
     */
    private void drawTapAnimation(Graphics2D g2, int centerX, int centerY) {
        float scale = AnimationUtils.scalePulse(animationTime, 0.003f, 0.8f, 1.2f);
        int radius = (int)(30 * scale);

        g2.setColor(new Color(255, 255, 255, 150));
        g2.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        g2.setColor(GameConfig.COLOR_ACCENT);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    /**
     * 绘制金币动画
     */
    private void drawCoinAnimation(Graphics2D g2, int x, int y) {
        float alpha = AnimationUtils.pulse(animationTime, 0.005f);
        g2.setColor(new Color(255, 215, 0, (int)(200 * alpha)));
        g2.fillOval(x, y, 30, 30);

        g2.setColor(new Color(255, 255, 0, (int)(100 * alpha)));
        g2.fillOval(x + 5, y + 5, 20, 20);
    }

    /**
     * 绘制障碍物动画
     */
    private void drawObstacleAnimation(Graphics2D g2, int x, int y) {
        float offset = AnimationUtils.bounce(animationTime, 10);

        g2.setColor(GameConfig.COLOR_WARNING);
        int[] xPoints = {x + 40, x + 60, x + 40};
        int[] yPoints = {y + (int)offset, y + 50 + (int)offset, y + 100 + (int)offset};
        g2.fillPolygon(xPoints, yPoints, 3);
    }

    /**
     * 下一步
     */
    public void nextStep() {
        currentStep++;
        animationTime = 0;

        if (currentStep >= steps.size()) {
            completeTutorial();
        }
    }

    /**
     * 跳过教程
     */
    public void skipTutorial() {
        completeTutorial();
    }

    /**
     * 完成教程
     */
    public void completeTutorial() {
        completed = true;
        currentStep = steps.size();
    }

    /**
     * 检查教程是否完成
     * @return 是否完成
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * 设置教程完成状态
     * @param completed 完成状态
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed) {
            currentStep = steps.size();
        } else {
            currentStep = 0;
        }
    }

    /**
     * 获取当前步骤
     * @return 当前步骤索引
     */
    public int getCurrentStep() {
        return currentStep;
    }

    /**
     * 处理点击事件
     * @param mouseX 鼠标X坐标
     * @param mouseY 鼠标Y坐标
     * @return 是否处理了点击
     */
    public boolean handleClick(int mouseX, int mouseY) {
        if (completed) return false;

        // 检查是否点击了跳过按钮
        if (mouseX > GameConfig.WIDTH - 150 && mouseY > GameConfig.HEIGHT - 40) {
            skipTutorial();
            return true;
        }

        // 点击任意位置进入下一步
        nextStep();
        return true;
    }
}
