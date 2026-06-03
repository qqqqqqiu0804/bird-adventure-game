"""生成飞翔小鸟冒险版的音效文件（WAV格式）"""
import struct
import math
import os

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
    main()
