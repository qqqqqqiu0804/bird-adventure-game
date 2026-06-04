"""生成飞翔小鸟冒险版的音效文件（WAV格式）"""
import struct
import math
import os
import numpy as np
import wave

SAMPLE_RATE = 22050  # 采样率（较低以减小文件体积）


def generate_wav(filename, samples):
    """将采样数据写入WAV文件"""
    n = len(samples)
    # WAV文件头
    header = struct.pack('<4sI4s', b'RIFF', 36 + n * 2, b'WAVE')
    fmt = struct.pack('<4sIHHIIHH', b'fmt ', 16, 1, 1,
                      SAMPLE_RATE, SAMPLE_RATE * 2, 2, 16)
    data_header = struct.pack('<4sI', b'data', n * 2)
    with open(filename, 'wb') as f:
        f.write(header + fmt + data_header)
        for s in samples:
            f.write(struct.pack('<h', max(-32768, min(32767, int(s)))))


def make_click():
    """点击上升音效 — 短促上升音"""
    samples = []
    duration = 0.1
    n = int(SAMPLE_RATE * duration)
    for i in range(n):
        t = i / SAMPLE_RATE
        freq = 800 + 400 * (i / n)  # 频率上升
        amp = 3000 * (1 - i / n)     # 衰减
        samples.append(amp * math.sin(2 * math.pi * freq * t))
    return samples


def make_score():
    """得分音效 — 清脆叮声"""
    samples = []
    duration = 0.15
    n = int(SAMPLE_RATE * duration)
    for i in range(n):
        t = i / SAMPLE_RATE
        freq = 1200
        amp = 3000 * math.exp(-8 * t)  # 快速衰减
        samples.append(amp * math.sin(2 * math.pi * freq * t))
    return samples


def make_hit():
    """碰撞音效 — 低沉撞击"""
    samples = []
    duration = 0.3
    n = int(SAMPLE_RATE * duration)
    for i in range(n):
        t = i / SAMPLE_RATE
        freq = 150 * math.exp(-3 * t)
        amp = 4000 * math.exp(-5 * t)
        noise = 500 * (2 * (hash(i) % 2) - 1) * math.exp(-8 * t)
        samples.append(amp * math.sin(2 * math.pi * freq * t) + noise)
    return samples


def make_gameover():
    """游戏结束音效 — 下降音"""
    samples = []
    duration = 0.5
    n = int(SAMPLE_RATE * duration)
    for i in range(n):
        t = i / SAMPLE_RATE
        freq = 600 - 400 * (i / n)  # 频率下降
        amp = 2500 * (1 - i / n)
        samples.append(amp * math.sin(2 * math.pi * freq * t))
    return samples


def make_levelcomplete():
    """关卡完成音效 — 上升和弦"""
    samples = []
    duration = 0.6
    n = int(SAMPLE_RATE * duration)
    for i in range(n):
        t = i / SAMPLE_RATE
        freq1 = 400 + 200 * (i / n)
        freq2 = 500 + 250 * (i / n)
        freq3 = 600 + 300 * (i / n)
        amp = 3000 * (1 - i / n)
        samples.append(amp * (
            math.sin(2 * math.pi * freq1 * t) +
            math.sin(2 * math.pi * freq2 * t) +
            math.sin(2 * math.pi * freq3 * t)
        ) / 3)
    return samples


def make_powerup():
    """道具收集音效 — 魔法音"""
    samples = []
    duration = 0.3
    n = int(SAMPLE_RATE * duration)
    for i in range(n):
        t = i / SAMPLE_RATE
        freq = 800 + 400 * math.sin(10 * t)
        amp = 3000 * (1 - i / n)
        samples.append(amp * math.sin(2 * math.pi * freq * t))
    return samples


def make_coin():
    """金币收集音效 — 叮当声"""
    samples = []
    duration = 0.1
    n = int(SAMPLE_RATE * duration)
    for i in range(n):
        t = i / SAMPLE_RATE
        freq = 1000
        amp = 2000 * math.exp(-10 * t)
        samples.append(amp * math.sin(2 * math.pi * freq * t))
    return samples


def generate_coin_variant(filename, base_freq=800, variation=0.2):
    """生成金币音效变体"""
    sample_rate = 44100
    duration = 0.1
    t = np.linspace(0, duration, int(sample_rate * duration), False)

    # 随机频率变化
    freq = base_freq * (1 + variation * (np.random.random() - 0.5))
    wave_data = np.sin(2 * np.pi * freq * t) * 0.5

    # 添加衰减
    envelope = np.exp(-t * 20)
    wave_data *= envelope

    # 转换为16位整数
    wave_data = np.int16(wave_data * 32767)

    # 保存为WAV文件
    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)
        wav_file.setsampwidth(2)
        wav_file.setframerate(sample_rate)
        wav_file.writeframes(wave_data.tobytes())


def generate_hover_sound(filename):
    """生成悬停音效"""
    sample_rate = 44100
    duration = 0.05
    t = np.linspace(0, duration, int(sample_rate * duration), False)

    # 高频短促音
    wave_data = np.sin(2 * np.pi * 1200 * t) * 0.3
    envelope = np.exp(-t * 50)
    wave_data *= envelope

    wave_data = np.int16(wave_data * 32767)

    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)
        wav_file.setsampwidth(2)
        wav_file.setframerate(sample_rate)
        wav_file.writeframes(wave_data.tobytes())


def generate_transition_sound(filename):
    """生成页面切换音效"""
    sample_rate = 44100
    duration = 0.3
    t = np.linspace(0, duration, int(sample_rate * duration), False)

    # 滑音效果
    freq = 400 + 800 * t / duration
    wave_data = np.sin(2 * np.pi * freq * t) * 0.4
    envelope = np.exp(-t * 5)
    wave_data *= envelope

    wave_data = np.int16(wave_data * 32767)

    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)
        wav_file.setsampwidth(2)
        wav_file.setframerate(sample_rate)
        wav_file.writeframes(wave_data.tobytes())


def generate_background_music(filename, theme="default"):
    """生成背景音乐 - 优化版"""
    sample_rate = 44100
    duration = 16.0  # 16秒循环，更长更丰富
    t = np.linspace(0, duration, int(sample_rate * duration), False)

    wave_data = np.zeros_like(t)

    if theme == "menu":
        # 主菜单：轻快的电子风格
        # 使用 C大调和弦进行：C - Am - F - G
        chord_progression = [
            (261.63, [1, 1.25, 1.5]),    # C: C E G
            (220.00, [1, 1.2, 1.5]),     # Am: A C E
            (174.61, [1, 1.25, 1.5]),    # F: F A C
            (196.00, [1, 1.25, 1.5]),    # G: G B D
        ]

        chord_duration = duration / len(chord_progression)

        for i, (base_freq, ratios) in enumerate(chord_progression):
            start = int(i * chord_duration * sample_rate)
            end = int((i + 1) * chord_duration * sample_rate)
            t_chord = t[start:end] - t[start]

            # 和弦音
            for ratio in ratios:
                freq = base_freq * ratio
                amp = 0.15 * np.exp(-t_chord * 0.5)
                wave_data[start:end] += amp * np.sin(2 * np.pi * freq * t_chord)

            # 旋律音（高八度的简单旋律）
            melody_freqs = [base_freq * 2, base_freq * 2.5, base_freq * 3]
            for j, mf in enumerate(melody_freqs):
                t_note = t_chord[j*len(t_chord)//3:(j+1)*len(t_chord)//3]
                amp = 0.1 * np.exp(-t_note * 1)
                wave_data[start + j*len(t_chord)//3:start + (j+1)*len(t_chord)//3] += \
                    amp * np.sin(2 * np.pi * mf * t_note)

    elif theme == "game":
        # 游戏中：节奏感强，有驱动感
        # 使用 Am - F - C - G 进行
        bpm = 120
        beat_duration = 60.0 / bpm
        beats_per_measure = 4

        # 低音贝斯线
        bass_notes = [110, 87.31, 130.81, 98]  # A F C G
        for i, freq in enumerate(bass_notes):
            start = int(i * beat_duration * beats_per_measure * sample_rate)
            end = int((i + 1) * beat_duration * beats_per_measure * sample_rate)
            t_note = t[start:end] - t[start]

            # 贝斯音
            amp = 0.2 * np.exp(-t_note * 2)
            wave_data[start:end] += amp * np.sin(2 * np.pi * freq * t_note)

            # 节奏鼓点
            for beat in range(beats_per_measure):
                beat_start = start + int(beat * beat_duration * sample_rate)
                beat_end = min(beat_start + int(0.1 * sample_rate), end)
                t_beat = np.arange(beat_end - beat_start) / sample_rate
                kick = 0.15 * np.exp(-t_beat * 30) * np.sin(2 * np.pi * 60 * t_beat)
                wave_data[beat_start:beat_end] += kick

    else:
        # 游戏结束：舒缓的钢琴风格
        # 使用缓慢的和弦进行
        notes = [
            (261.63, 0.3),  # C4
            (329.63, 0.25), # E4
            (392.00, 0.2),  # G4
            (523.25, 0.15), # C5
            (392.00, 0.2),  # G4
            (329.63, 0.25), # E4
        ]

        note_duration = duration / len(notes)

        for i, (freq, amp) in enumerate(notes):
            start = int(i * note_duration * sample_rate)
            end = int((i + 1) * note_duration * sample_rate)
            t_note = t[start:end] - t[start]

            # 钢琴音色（基音+泛音）
            envelope = amp * np.exp(-t_note * 1.5) * (1 - np.exp(-t_note * 20))
            wave_data[start:end] += envelope * (
                np.sin(2 * np.pi * freq * t_note) +
                0.5 * np.sin(2 * np.pi * freq * 2 * t_note) +
                0.25 * np.sin(2 * np.pi * freq * 3 * t_note)
            )

    # 归一化
    max_val = np.max(np.abs(wave_data))
    if max_val > 0:
        wave_data = wave_data / max_val * 0.7

    wave_data = np.int16(wave_data * 32767)

    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)
        wav_file.setsampwidth(2)
        wav_file.setframerate(sample_rate)
        wav_file.writeframes(wave_data.tobytes())


def main():
    out_dir = os.path.dirname(os.path.abspath(__file__))
    sounds = {
        'click.wav': make_click,
        'score.wav': make_score,
        'hit.wav': make_hit,
        'gameover.wav': make_gameover,
        'levelcomplete.wav': make_levelcomplete,
        'powerup.wav': make_powerup,
        'coin.wav': make_coin,
    }
    for name, func in sounds.items():
        path = os.path.join(out_dir, name)
        generate_wav(path, func())
        print(f'  生成 {name}')


if __name__ == '__main__':
    os.chdir(os.path.dirname(os.path.abspath(__file__)))

    # 生成原始音效
    main()

    # 生成金币音效变体
    generate_coin_variant("coin1.wav", 800, 0.2)
    generate_coin_variant("coin2.wav", 900, 0.3)
    generate_coin_variant("coin3.wav", 700, 0.15)

    # 生成UI音效
    generate_hover_sound("hover.wav")
    generate_transition_sound("transition.wav")

    # 生成背景音乐
    generate_background_music("menu_music.wav", "menu")
    generate_background_music("game_music.wav", "game")
    generate_background_music("gameover_music.wav", "gameover")

    print("音效和音乐文件生成完成！")
