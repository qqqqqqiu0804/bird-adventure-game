package cn.edu.huse.game;

import javax.sound.sampled.*;
import java.io.File;

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
    }

    /**
     * 加载音效文件
     */
    private Clip loadClip(String filename) {
        try {
            File file = new File(getClass().getResource(filename).toURI());
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
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
}
