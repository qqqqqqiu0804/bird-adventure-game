package cn.edu.huse.game;

import javax.sound.sampled.*;
import java.io.InputStream;

/**
 * 音效管理类 - 飞翔小鸟冒险版
 */
public class SoundManager {
    private Clip clickClip;
    private Clip scoreClip;
    private Clip hitClip;
    private Clip gameOverClip;
    private Clip levelCompleteClip;
    private Clip powerUpClip;
    private Clip coinClip;
    private Clip[] coinClips;      // 金币音效变体
    private Clip hoverClip;        // 按钮悬停音效
    private Clip transitionClip;   // 页面切换音效
    private SettingsManager settingsManager;  // 添加设置管理器引用

    /**
     * 构造方法
     */
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

    /**
     * 加载音效文件
     */
    private Clip loadClip(String filename) {
        try {
            InputStream is = getClass().getResourceAsStream(filename);
            if (is == null) return null;
            AudioInputStream ais = AudioSystem.getAudioInputStream(is);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (Exception e) {
            return null;  // 加载失败，静默降级
        }
    }

    /**
     * 播放音效（应用音量设置）
     */
    private void play(Clip clip) {
        if (clip == null) return;  // 容错
        try {
            // 应用音效音量设置
            applyVolume(clip, settingsManager.getSfxVolume());
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception e) {
            // 播放失败静默处理
        }
    }

    /**
     * 应用音量设置到 Clip
     * @param clip 音频片段
     * @param volume 音量值 0-100
     */
    private void applyVolume(Clip clip, int volume) {
        try {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                // 将 0-100 转换为分贝值
                // 0% -> -80dB (静音), 100% -> 0dB (最大)
                float dB;
                if (volume == 0) {
                    dB = -80.0f;
                } else {
                    dB = (float) (20.0 * Math.log10(volume / 100.0));
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
     * 播放点击音效
     */
    public void playClick() {
        play(clickClip);
    }

    /**
     * 播放得分音效
     */
    public void playScore() {
        play(scoreClip);
    }

    /**
     * 播放碰撞音效
     */
    public void playHit() {
        play(hitClip);
    }

    /**
     * 播放游戏结束音效
     */
    public void playGameOver() {
        play(gameOverClip);
    }

    /**
     * 播放关卡完成音效
     */
    public void playLevelComplete() {
        play(levelCompleteClip);
    }

    /**
     * 播放道具音效
     */
    public void playPowerUp() {
        play(powerUpClip);
    }

    /**
     * 播放金币音效
     */
    public void playCoin() {
        play(coinClip);
    }

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
}
