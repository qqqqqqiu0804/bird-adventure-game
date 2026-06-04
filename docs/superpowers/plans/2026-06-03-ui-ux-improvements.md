# UI/UX 改进实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为飞翔小鸟冒险版游戏添加完整的UI/UX改进，包括背景音乐、新手教程、界面动画、UI美化和音效增强。

**Architecture:** 混合方案 - 背景音乐和音效增强作为独立模块，界面动画和UI美化统一到现有渲染系统，新手教程作为独立模块。

**Tech Stack:** Java Swing, Java Sound API, 自定义动画工具类

---

## 文件结构

### 新增文件
| 文件 | 职责 |
|------|------|
| `cn/edu/huse/game/MusicManager.java` | 背景音乐管理 |
| `cn/edu/huse/game/TutorialManager.java` | 新手教程管理 |
| `cn/edu/huse/game/AnimationUtils.java` | 动画工具类 |

### 修改文件
| 文件 | 修改内容 |
|------|----------|
| `cn/edu/huse/game/GameConfig.java` | 添加配色方案和字体配置 |
| `cn/edu/huse/game/SoundManager.java` | 添加音效变体和随机播放 |
| `cn/edu/huse/game/BirdGame.java` | 集成所有新功能 |
| `cn/edu/huse/game/SaveManager.java` | 添加教程完成状态 |

---

## Task 1: UI美化 - 配色方案和字体

**Files:**
- Modify: `cn/edu/huse/game/GameConfig.java`

- [ ] **Step 1: 添加配色常量**

```java
// 在 GameConfig.java 中添加以下常量
public static final Color COLOR_PRIMARY = new Color(26, 26, 46);      // 深蓝
public static final Color COLOR_ACCENT = new Color(0, 212, 255);     // 青色
public static final Color COLOR_WARNING = new Color(255, 107, 53);   // 橙色
public static final Color COLOR_SUCCESS = new Color(0, 255, 136);    // 绿色
public static final Color COLOR_TEXT = new Color(255, 255, 255);     // 白色
public static final Color COLOR_TEXT_SECONDARY = new Color(180, 180, 200); // 浅灰
public static final Color COLOR_BUTTON_HOVER = new Color(255, 255, 255, 50); // 悬停效果
public static final Color COLOR_SHADOW = new Color(0, 0, 0, 80);    // 阴影
```

- [ ] **Step 2: 添加字体配置**

```java
// 在 GameConfig.java 中添加以下字体
public static Font TITLE_FONT = new Font("Microsoft YaHei", Font.BOLD, 48);
public static Font SUBTITLE_FONT = new Font("Microsoft YaHei", Font.BOLD, 24);
public static Font BODY_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);
public static Font NUMBER_FONT = new Font("Consolas", Font.BOLD, 20);
```

- [ ] **Step 3: 编译验证**

Run: `javac -encoding UTF-8 cn/edu/huse/game/*.java`
Expected: 编译成功，无错误

- [ ] **Step 4: 提交**

```bash
git add cn/edu/huse/game/GameConfig.java
git commit -m "feat(ui): 添加配色方案和字体配置"
```

---

## Task 2: UI美化 - 样式化按钮方法

**Files:**
- Modify: `cn/edu/huse/game/BirdGame.java`

- [ ] **Step 1: 添加样式化按钮绘制方法**

```java
// 在 BirdGame.java 中添加以下方法
/**
 * 绘制样式化按钮（带阴影、渐变、悬停效果）
 */
private void drawStyledButton(Graphics g, String text, int x, int y,
                               int width, int height, Color color,
                               boolean hovered) {
    Graphics2D g2 = (Graphics2D) g;

    // 阴影
    g2.setColor(GameConfig.COLOR_SHADOW);
    g2.fillRoundRect(x + 3, y + 3, width, height, 15, 15);

    // 渐变背景
    GradientPaint gradient = new GradientPaint(
        x, y, color.brighter(),
        x, y + height, color
    );
    g2.setPaint(gradient);
    g2.fillRoundRect(x, y, width, height, 15, 15);

    // 悬停效果
    if (hovered) {
        g2.setColor(GameConfig.COLOR_BUTTON_HOVER);
        g2.fillRoundRect(x, y, width, height, 15, 15);
    }

    // 边框
    g2.setColor(Color.WHITE);
    g2.setStroke(new BasicStroke(2));
    g2.drawRoundRect(x, y, width, height, 15, 15);

    // 文字（居中，带阴影）
    g2.setFont(GameConfig.SUBTITLE_FONT);
    FontMetrics fm = g2.getFontMetrics();
    int textX = x + (width - fm.stringWidth(text)) / 2;
    int textY = y + (height + fm.getHeight()) / 2 - 3;

    // 文字阴影
    g2.setColor(new Color(0, 0, 0, 100));
    g2.drawString(text, textX + 1, textY + 1);

    // 文字
    g2.setColor(Color.WHITE);
    g2.drawString(text, textX, textY);

    // 重置画笔
    g2.setStroke(new BasicStroke(1));
    g2.setPaint(null);
}
```

- [ ] **Step 2: 添加鼠标悬停状态变量**

```java
// 在 BirdGame.java 的成员变量区域添加
int hoveredButton = -1;        // 当前悬停按钮索引
Point lastMousePosition = null; // 最后鼠标位置
```

- [ ] **Step 3: 添加鼠标移动事件监听**

```java
// 在 action() 方法中，在 MouseListener 之后添加
MouseMotionListener motionListener = new MouseMotionAdapter() {
    @Override
    public void mouseMoved(MouseEvent e) {
        lastMousePosition = e.getPoint();
        updateHoveredButton(e.getX(), e.getY());
    }
};
addMouseMotionListener(motionListener);
```

- [ ] **Step 4: 添加悬停检测方法**

```java
// 在 BirdGame.java 中添加以下方法
/**
 * 更新当前悬停的按钮
 */
private void updateHoveredButton(int mouseX, int mouseY) {
    hoveredButton = -1;

    switch (state) {
        case MAIN_MENU:
            // 检测主菜单按钮
            if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 150, 160, 38)) hoveredButton = 0;
            else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 195, 160, 38)) hoveredButton = 1;
            else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 240, 160, 38)) hoveredButton = 2;
            else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 285, 160, 38)) hoveredButton = 3;
            else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 330, 160, 38)) hoveredButton = 4;
            else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 375, 160, 38)) hoveredButton = 5;
            else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 420, 160, 38)) hoveredButton = 6;
            else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 465, 160, 38)) hoveredButton = 7;
            else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 510, 160, 38)) hoveredButton = 8;
            break;
        // 其他状态可以后续添加
    }
}
```

- [ ] **Step 5: 编译验证**

Run: `javac -encoding UTF-8 cn/edu/huse/game/*.java`
Expected: 编译成功，无错误

- [ ] **Step 6: 提交**

```bash
git add cn/edu/huse/game/BirdGame.java
git commit -m "feat(ui): 添加样式化按钮和悬停效果"
```

---

## Task 3: 动画工具类

**Files:**
- Create: `cn/edu/huse/game/AnimationUtils.java`

- [ ] **Step 1: 创建动画工具类**

```java
package cn.edu.huse.game;

/**
 * 动画工具类 - 提供各种动画效果
 */
public class AnimationUtils {

    /**
     * 线性插值
     * @param start 起始值
     * @param end 结束值
     * @param t 进度 (0.0 - 1.0)
     * @return 插值结果
     */
    public static float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }

    /**
     * 缓动函数 - 先慢后快再慢
     * @param t 进度 (0.0 - 1.0)
     * @return 缓动后的进度
     */
    public static float easeInOut(float t) {
        return t < 0.5f ? 2 * t * t : 1 - (float)Math.pow(-2 * t + 2, 2) / 2;
    }

    /**
     * 缓动函数 - 先慢后快
     * @param t 进度 (0.0 - 1.0)
     * @return 缓动后的进度
     */
    public static float easeIn(float t) {
        return t * t;
    }

    /**
     * 缓动函数 - 先快后慢
     * @param t 进度 (0.0 - 1.0)
     * @return 缓动后的进度
     */
    public static float easeOut(float t) {
        return 1 - (1 - t) * (1 - t);
    }

    /**
     * 淡入效果
     * @param currentAlpha 当前透明度
     * @param targetAlpha 目标透明度
     * @param speed 速度
     * @return 新的透明度
     */
    public static int fadeIn(int currentAlpha, int targetAlpha, int speed) {
        if (currentAlpha < targetAlpha) {
            return Math.min(currentAlpha + speed, targetAlpha);
        }
        return currentAlpha;
    }

    /**
     * 淡出效果
     * @param currentAlpha 当前透明度
     * @param targetAlpha 目标透明度
     * @param speed 速度
     * @return 新的透明度
     */
    public static int fadeOut(int currentAlpha, int targetAlpha, int speed) {
        if (currentAlpha > targetAlpha) {
            return Math.max(currentAlpha - speed, targetAlpha);
        }
        return currentAlpha;
    }

    /**
     * 脉冲效果
     * @param time 时间
     * @param speed 速度
     * @return 脉冲值 (0.0 - 1.0)
     */
    public static float pulse(float time, float speed) {
        return (float)(Math.sin(time * speed) * 0.5 + 0.5);
    }

    /**
     * 弹跳效果
     * @param time 时间
     * @param height 高度
     * @return 弹跳偏移量
     */
    public static float bounce(float time, float height) {
        return (float)(height * Math.abs(Math.sin(time * 2)));
    }

    /**
     * 缩放脉冲效果
     * @param time 时间
     * @param speed 速度
     * @param minScale 最小缩放
     * @param maxScale 最大缩放
     * @return 缩放值
     */
    public static float scalePulse(float time, float speed, float minScale, float maxScale) {
        float t = pulse(time, speed);
        return lerp(minScale, maxScale, t);
    }
}
```

- [ ] **Step 2: 编译验证**

Run: `javac -encoding UTF-8 cn/edu/huse/game/*.java`
Expected: 编译成功，无错误

- [ ] **Step 3: 提交**

```bash
git add cn/edu/huse/game/AnimationUtils.java
git commit -m "feat(animation): 添加动画工具类"
```

---

## Task 4: 音效增强 - 扩展SoundManager

**Files:**
- Modify: `cn/edu/huse/game/SoundManager.java`

- [ ] **Step 1: 添加音效变体成员变量**

```java
// 在 SoundManager.java 中添加成员变量
private Clip[] coinClips;      // 金币音效变体
private Clip hoverClip;        // 按钮悬停音效
private Clip transitionClip;   // 页面切换音效
```

- [ ] **Step 2: 修改构造方法加载变体音效**

```java
// 修改构造方法
public SoundManager(SettingsManager settingsManager) {
    this.settingsManager = settingsManager;
    // 预加载音效，失败时静默降级
    clickClip = loadClip("click.wav");
    scoreClip = loadClip("score.wav");
    hitClip = loadClip("hit.wav");
    gameOverClip = loadClip("gameover.wav");
    levelCompleteClip = loadClip("levelcomplete.wav");
    powerUpClip = loadClip("powerup.wav");
    coinClip = loadClip("coin.wav");

    // 加载音效变体
    coinClips = new Clip[3];
    coinClips[0] = loadClip("coin1.wav");
    coinClips[1] = loadClip("coin2.wav");
    coinClips[2] = loadClip("coin3.wav");

    // 如果变体加载失败，使用默认金币音效
    for (int i = 0; i < coinClips.length; i++) {
        if (coinClips[i] == null) {
            coinClips[i] = coinClip;
        }
    }

    // UI音效
    hoverClip = loadClip("hover.wav");
    transitionClip = loadClip("transition.wav");
}
```

- [ ] **Step 3: 添加随机金币音效方法**

```java
/**
 * 随机播放金币音效
 */
public void playRandomCoin() {
    if (coinClips == null || coinClips.length == 0) {
        play(coinClip);
        return;
    }

    int index = (int)(Math.random() * coinClips.length);
    play(coinClips[index]);
}
```

- [ ] **Step 4: 添加变调播放方法**

```java
/**
 * 变调播放音效
 * @param clip 音频片段
 * @param pitch 音高倍数 (0.5 - 2.0)
 */
private void playWithPitch(Clip clip, float pitch) {
    if (clip == null) return;
    try {
        // 通过改变采样率实现变调
        if (clip.isControlSupported(FloatControl.Type.SAMPLE_RATE)) {
            FloatControl rateControl = (FloatControl) clip.getControl(FloatControl.Type.SAMPLE_RATE);
            float baseRate = rateControl.getValue();
            rateControl.setValue(baseRate * pitch);
        }
        clip.setFramePosition(0);
        clip.start();
    } catch (Exception e) {
        // 降级到普通播放
        play(clip);
    }
}
```

- [ ] **Step 5: 添加碰撞音效方法**

```java
/**
 * 播放碰撞音效（根据强度变调）
 * @param hard 是否为强烈碰撞
 */
public void playHit(boolean hard) {
    if (hard) {
        playWithPitch(hitClip, 0.8f);  // 低沉音效
    } else {
        playWithPitch(hitClip, 1.2f);  // 高音音效
    }
}
```

- [ ] **Step 6: 添加UI音效方法**

```java
/**
 * 播放按钮悬停音效
 */
public void playButtonHover() {
    play(hoverClip);
}

/**
 * 播放页面切换音效
 */
public void playScreenTransition() {
    play(transitionClip);
}
```

- [ ] **Step 7: 编译验证**

Run: `javac -encoding UTF-8 cn/edu/huse/game/*.java`
Expected: 编译成功，无错误

- [ ] **Step 8: 提交**

```bash
git add cn/edu/huse/game/SoundManager.java
git commit -m "feat(sound): 添加音效变体和随机播放功能"
```

---

## Task 5: 背景音乐系统

**Files:**
- Create: `cn/edu/huse/game/MusicManager.java`

- [ ] **Step 1: 创建MusicManager类**

```java
package cn.edu.huse.game;

import javax.sound.sampled.*;
import java.io.File;

/**
 * 背景音乐管理器 - 飞翔小鸟冒险版
 */
public class MusicManager {
    private Clip currentClip;
    private String currentTrack;
    private int volume;          // 0-100
    private boolean isPlaying;
    private boolean loop;

    /**
     * 构造方法
     */
    public MusicManager() {
        volume = 60;
        isPlaying = false;
        loop = true;
    }

    /**
     * 加载音乐文件
     * @param filename 音乐文件名
     */
    public void loadMusic(String filename) {
        try {
            // 如果正在播放，先停止
            if (currentClip != null) {
                stop();
            }

            File file = new File(getClass().getResource(filename).toURI());
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            currentClip = AudioSystem.getClip();
            currentClip.open(ais);
            currentTrack = filename;

            // 应用音量
            applyVolume();

            // 添加循环播放监听
            if (loop) {
                currentClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP && isPlaying) {
                        currentClip.setFramePosition(0);
                        currentClip.start();
                    }
                });
            }
        } catch (Exception e) {
            // 加载失败静默处理
            currentClip = null;
        }
    }

    /**
     * 播放音乐
     */
    public void play() {
        if (currentClip == null) return;
        try {
            applyVolume();
            currentClip.setFramePosition(0);
            currentClip.start();
            isPlaying = true;
        } catch (Exception e) {
            // 播放失败静默处理
        }
    }

    /**
     * 暂停音乐
     */
    public void pause() {
        if (currentClip == null || !isPlaying) return;
        try {
            currentClip.stop();
            isPlaying = false;
        } catch (Exception e) {
            // 暂停失败静默处理
        }
    }

    /**
     * 停止音乐
     */
    public void stop() {
        if (currentClip == null) return;
        try {
            currentClip.stop();
            currentClip.setFramePosition(0);
            isPlaying = false;
        } catch (Exception e) {
            // 停止失败静默处理
        }
    }

    /**
     * 设置音量
     * @param volume 音量值 (0-100)
     */
    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume));
        applyVolume();
    }

    /**
     * 获取当前音量
     * @return 音量值 (0-100)
     */
    public int getVolume() {
        return volume;
    }

    /**
     * 切换音乐
     * @param filename 新的音乐文件名
     */
    public void switchTrack(String filename) {
        if (filename.equals(currentTrack)) return;

        // 淡出当前音乐
        fadeOut(500);

        // 加载并播放新音乐
        loadMusic(filename);
        fadeIn(500);
    }

    /**
     * 淡入效果
     * @param duration 淡入时长（毫秒）
     */
    public void fadeIn(int duration) {
        if (currentClip == null) return;

        // 在新线程中执行淡入
        new Thread(() -> {
            try {
                int steps = 20;
                int stepDuration = duration / steps;
                float stepVolume = (float) volume / steps;

                // 从0音量开始
                for (int i = 0; i < steps; i++) {
                    int currentVol = (int)(stepVolume * i);
                    applyVolume(currentVol);
                    Thread.sleep(stepDuration);
                }

                // 恢复目标音量
                applyVolume();
            } catch (InterruptedException e) {
                // 线程中断静默处理
            }
        }).start();
    }

    /**
     * 淡出效果
     * @param duration 淡出时长（毫秒）
     */
    public void fadeOut(int duration) {
        if (currentClip == null || !isPlaying) return;

        // 在新线程中执行淡出
        new Thread(() -> {
            try {
                int steps = 20;
                int stepDuration = duration / steps;
                float stepVolume = (float) volume / steps;

                // 逐渐降低音量
                for (int i = steps; i > 0; i--) {
                    int currentVol = (int)(stepVolume * i);
                    applyVolume(currentVol);
                    Thread.sleep(stepDuration);
                }

                // 停止播放
                stop();
            } catch (InterruptedException e) {
                // 线程中断静默处理
            }
        }).start();
    }

    /**
     * 应用音量设置
     */
    private void applyVolume() {
        applyVolume(volume);
    }

    /**
     * 应用指定音量
     * @param vol 音量值 (0-100)
     */
    private void applyVolume(int vol) {
        if (currentClip == null) return;

        try {
            if (currentClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);

                // 将 0-100 转换为分贝值
                float dB;
                if (vol == 0) {
                    dB = -80.0f;
                } else {
                    dB = (float) (20.0 * Math.log10(vol / 100.0));
                }

                // 确保在有效范围内
                dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
                gainControl.setValue(dB);
            }
        } catch (Exception e) {
            // 音量设置失败静默处理
        }
    }

    /**
     * 检查是否正在播放
     * @return 是否正在播放
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * 获取当前曲目
     * @return 当前曲目文件名
     */
    public String getCurrentTrack() {
        return currentTrack;
    }

    /**
     * 设置是否循环播放
     * @param loop 是否循环
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
}
```

- [ ] **Step 2: 编译验证**

Run: `javac -encoding UTF-8 cn/edu/huse/game/*.java`
Expected: 编译成功，无错误

- [ ] **Step 3: 提交**

```bash
git add cn/edu/huse/game/MusicManager.java
git commit -m "feat(music): 添加背景音乐管理系统"
```

---

## Task 6: 新手教程系统

**Files:**
- Create: `cn/edu/huse/game/TutorialManager.java`
- Modify: `cn/edu/huse/game/SaveManager.java`

- [ ] **Step 1: 创建TutorialStep类**

```java
// 在 TutorialManager.java 中定义
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
```

- [ ] **Step 2: 创建TutorialManager类**

```java
package cn.edu.huse.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
```

- [ ] **Step 3: 修改SaveManager添加教程状态**

```java
// 在 SaveManager.java 的 GameSaveData 类中添加
public boolean tutorialCompleted;

// 在 saveGame 方法中添加
data.tutorialCompleted = tutorialCompleted;

// 在 loadGame 方法中添加
tutorialCompleted = data.tutorialCompleted;

// 添加新的成员变量
private boolean tutorialCompleted = false;

// 添加 getter 和 setter
public boolean isTutorialCompleted() {
    return tutorialCompleted;
}

public void setTutorialCompleted(boolean completed) {
    this.tutorialCompleted = completed;
}
```

- [ ] **Step 4: 编译验证**

Run: `javac -encoding UTF-8 cn/edu/huse/game/*.java`
Expected: 编译成功，无错误

- [ ] **Step 5: 提交**

```bash
git add cn/edu/huse/game/TutorialManager.java cn/edu/huse/game/SaveManager.java
git commit -m "feat(tutorial): 添加新手教程系统"
```

---

## Task 7: 集成所有功能到BirdGame

**Files:**
- Modify: `cn/edu/huse/game/BirdGame.java`

- [ ] **Step 1: 添加新的成员变量**

```java
// 在 BirdGame.java 的成员变量区域添加
MusicManager musicManager;
TutorialManager tutorialManager;
int screenAlpha = 0;           // 屏幕透明度（用于淡入淡出）
boolean showTutorial = false;  // 是否显示教程
```

- [ ] **Step 2: 初始化新管理器**

```java
// 在构造方法中添加
musicManager = new MusicManager();
tutorialManager = new TutorialManager();

// 加载教程完成状态
tutorialManager.setCompleted(saveManager.isTutorialCompleted());

// 检查是否首次启动
if (tutorialManager.isFirstLaunch()) {
    showTutorial = true;
}
```

- [ ] **Step 3: 修改paintComponent添加教程显示**

```java
// 在 paintComponent 方法的 switch 语句之前添加
// 绘制教程（在所有界面之上）
if (showTutorial && !tutorialManager.isCompleted()) {
    tutorialManager.showTutorial(g);
    return; // 教程显示时，不绘制其他内容
}
```

- [ ] **Step 4: 修改handleClick添加教程点击处理**

```java
// 在 handleClick 方法的开头添加
// 处理教程点击
if (showTutorial && !tutorialManager.isCompleted()) {
    if (tutorialManager.handleClick(mouseX, mouseY)) {
        if (tutorialManager.isCompleted()) {
            showTutorial = false;
            saveManager.setTutorialCompleted(true);
            saveManager.saveSettings();
        }
        return;
    }
}
```

- [ ] **Step 5: 添加页面切换淡入淡出效果**

```java
// 在 BirdGame.java 中添加以下方法
/**
 * 绘制淡入淡出效果
 */
private void drawFadeEffect(Graphics g) {
    if (screenAlpha > 0) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0, screenAlpha));
        g2.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);
    }
}

/**
 * 开始淡入效果
 */
private void startFadeIn() {
    screenAlpha = 255;
    new Thread(() -> {
        while (screenAlpha > 0) {
            screenAlpha = AnimationUtils.fadeOut(screenAlpha, 0, 10);
            try {
                Thread.sleep(16); // 约60FPS
            } catch (InterruptedException e) {
                break;
            }
        }
        screenAlpha = 0;
    }).start();
}

/**
 * 开始淡出效果
 */
private void startFadeOut() {
    screenAlpha = 0;
    new Thread(() -> {
        while (screenAlpha < 255) {
            screenAlpha = AnimationUtils.fadeIn(screenAlpha, 255, 10);
            try {
                Thread.sleep(16); // 约60FPS
            } catch (InterruptedException e) {
                break;
            }
        }
        screenAlpha = 255;
    }).start();
}
```

- [ ] **Step 6: 在paintComponent末尾添加淡入淡出效果**

```java
// 在 paintComponent 方法的最后添加
// 绘制淡入淡出效果
drawFadeEffect(g);
```

- [ ] **Step 7: 修改主菜单绘制使用样式化按钮**

```java
// 修改 drawMainMenu 方法，使用样式化按钮
private void drawMainMenu(Graphics g) {
    // 背景
    g.setColor(GameConfig.COLOR_PRIMARY);
    g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

    // 标题
    g.setFont(GameConfig.TITLE_FONT);
    g.setColor(GameConfig.COLOR_TEXT);
    g.drawString("飞翔小鸟", GameConfig.WIDTH / 2 - 100, 100);

    g.setFont(GameConfig.SUBTITLE_FONT);
    g.drawString("冒险版", GameConfig.WIDTH / 2 - 30, 150);

    // 按钮
    drawStyledButton(g, "开始游戏", GameConfig.WIDTH / 2 - 80, 150, 160, 38,
                    GameConfig.COLOR_SUCCESS, hoveredButton == 0);
    drawStyledButton(g, "关卡选择", GameConfig.WIDTH / 2 - 80, 195, 160, 38,
                    GameConfig.COLOR_ACCENT, hoveredButton == 1);
    drawStyledButton(g, "皮肤选择", GameConfig.WIDTH / 2 - 80, 240, 160, 38,
                    GameConfig.COLOR_WARNING, hoveredButton == 2);
    drawStyledButton(g, "场景选择", GameConfig.WIDTH / 2 - 80, 285, 160, 38,
                    new Color(150, 50, 200), hoveredButton == 3);
    drawStyledButton(g, "障碍物皮肤", GameConfig.WIDTH / 2 - 80, 330, 160, 38,
                    new Color(139, 90, 43), hoveredButton == 4);
    drawStyledButton(g, "升级系统", GameConfig.WIDTH / 2 - 80, 375, 160, 38,
                    Color.YELLOW, hoveredButton == 5);
    drawStyledButton(g, "成就系统", GameConfig.WIDTH / 2 - 80, 420, 160, 38,
                    Color.MAGENTA, hoveredButton == 6);
    drawStyledButton(g, "排行榜", GameConfig.WIDTH / 2 - 80, 465, 160, 38,
                    new Color(255, 200, 0), hoveredButton == 7);
    drawStyledButton(g, "设置", GameConfig.WIDTH / 2 - 80, 510, 160, 38,
                    Color.GRAY, hoveredButton == 8);

    // 其他内容...
}
```

- [ ] **Step 8: 编译验证**

Run: `javac -encoding UTF-8 cn/edu/huse/game/*.java`
Expected: 编译成功，无错误

- [ ] **Step 9: 提交**

```bash
git add cn/edu/huse/game/BirdGame.java
git commit -m "feat: 集成所有UI/UX改进功能"
```

---

## Task 8: 生成资源文件

**Files:**
- Create: `cn/edu/huse/game/generate_sounds.py` (修改)
- Create: 音效文件 (coin1.wav, coin2.wav, coin3.wav, hover.wav, transition.wav)
- Create: 音乐文件 (menu_music.wav, game_music.wav, gameover_music.wav)

- [ ] **Step 1: 修改Python脚本生成音效变体**

```python
# 在 generate_sounds.py 中添加以下函数
def generate_coin_variant(filename, base_freq=800, variation=0.2):
    """生成金币音效变体"""
    sample_rate = 44100
    duration = 0.1
    t = np.linspace(0, duration, int(sample_rate * duration), False)

    # 随机频率变化
    freq = base_freq * (1 + variation * (np.random.random() - 0.5))
    wave = np.sin(2 * np.pi * freq * t) * 0.5

    # 添加衰减
    envelope = np.exp(-t * 20)
    wave *= envelope

    # 转换为16位整数
    wave = np.int16(wave * 32767)

    # 保存为WAV文件
    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)
        wav_file.setsampwidth(2)
        wav_file.setframerate(sample_rate)
        wav_file.writeframes(wave.tobytes())

def generate_hover_sound(filename):
    """生成悬停音效"""
    sample_rate = 44100
    duration = 0.05
    t = np.linspace(0, duration, int(sample_rate * duration), False)

    # 高频短促音
    wave = np.sin(2 * np.pi * 1200 * t) * 0.3
    envelope = np.exp(-t * 50)
    wave *= envelope

    wave = np.int16(wave * 32767)

    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)
        wav_file.setsampwidth(2)
        wav_file.setframerate(sample_rate)
        wav_file.writeframes(wave.tobytes())

def generate_transition_sound(filename):
    """生成页面切换音效"""
    sample_rate = 44100
    duration = 0.3
    t = np.linspace(0, duration, int(sample_rate * duration), False)

    # 滑音效果
    freq = 400 + 800 * t / duration
    wave = np.sin(2 * np.pi * freq * t) * 0.4
    envelope = np.exp(-t * 5)
    wave *= envelope

    wave = np.int16(wave * 32767)

    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)
        wav_file.setsampwidth(2)
        wav_file.setframerate(sample_rate)
        wav_file.writeframes(wave.tobytes())

def generate_background_music(filename, theme="default"):
    """生成背景音乐"""
    sample_rate = 44100
    duration = 10.0  # 10秒循环
    t = np.linspace(0, duration, int(sample_rate * duration), False)

    # 基础旋律
    if theme == "menu":
        # 主菜单：轻快旋律
        freq = 440
        wave = np.sin(2 * np.pi * freq * t) * 0.3
        wave += np.sin(2 * np.pi * freq * 1.5 * t) * 0.2
        wave += np.sin(2 * np.pi * freq * 2 * t) * 0.1
    elif theme == "game":
        # 游戏中：节奏感强
        freq = 330
        wave = np.sin(2 * np.pi * freq * t) * 0.3
        # 添加节奏
        beat = np.sin(2 * np.pi * 2 * t) * 0.5 + 0.5
        wave *= beat
    else:
        # 游戏结束：舒缓
        freq = 220
        wave = np.sin(2 * np.pi * freq * t) * 0.3
        wave += np.sin(2 * np.pi * freq * 1.25 * t) * 0.2

    # 添加衰减
    envelope = np.exp(-t * 0.1)
    wave *= envelope

    # 归一化
    wave = wave / np.max(np.abs(wave)) * 0.8

    wave = np.int16(wave * 32767)

    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)
        wav_file.setsampwidth(2)
        wav_file.setframerate(sample_rate)
        wav_file.writeframes(wave.tobytes())
```

- [ ] **Step 2: 运行脚本生成音效文件**

Run: `python cn/edu/huse/game/generate_sounds.py`
Expected: 生成所有音效和音乐文件

- [ ] **Step 3: 验证生成的文件**

Run: `ls -la cn/edu/huse/game/*.wav`
Expected: 看到所有新生成的音效文件

- [ ] **Step 4: 提交**

```bash
git add cn/edu/huse/game/*.wav cn/edu/huse/game/generate_sounds.py
git commit -m "assets: 生成音效变体和背景音乐文件"
```

---

## Task 9: 测试和优化

**Files:**
- Test: 运行游戏进行功能测试

- [ ] **Step 1: 编译并运行游戏**

Run: `javac -encoding UTF-8 cn/edu/huse/game/*.java && java -cp . cn.edu.huse.game.BirdGame`
Expected: 游戏正常启动，显示主菜单

- [ ] **Step 2: 测试UI美化**

检查点：
- 主菜单按钮样式是否正确
- 配色方案是否一致
- 字体是否清晰

- [ ] **Step 3: 测试界面动画**

检查点：
- 按钮悬停效果是否工作
- 页面切换是否有淡入淡出
- 动画是否流畅

- [ ] **Step 4: 测试新手教程**

检查点：
- 首次启动是否显示教程
- 教程步骤是否正确
- 跳过功能是否工作
- 完成后是否不再显示

- [ ] **Step 5: 测试背景音乐**

检查点：
- 主菜单音乐是否播放
- 场景切换音乐是否切换
- 音量控制是否工作

- [ ] **Step 6: 测试音效增强**

检查点：
- 金币音效是否有变体
- 碰撞音效是否有变调
- UI音效是否工作

- [ ] **Step 7: 性能优化**

如果发现卡顿：
- 减少粒子数量
- 优化绘制方法
- 降低动画复杂度

- [ ] **Step 8: 最终提交**

```bash
git add .
git commit -m "feat: 完成UI/UX改进，版本v1.3.0"
```

---

## 执行选项

**Plan complete and saved to `docs/superpowers/plans/2026-06-03-ui-ux-improvements.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**
