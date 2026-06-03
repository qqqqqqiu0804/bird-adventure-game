"""生成飞翔小鸟冒险版的游戏图片资源"""
import math
import random
from PIL import Image, ImageDraw, ImageFont

random.seed(42)

def draw_bird(frame):
    """绘制UFO动画帧（3帧）"""
    img = Image.new('RGBA', (50, 50), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = 25, 25

    # UFO主体 - 椭圆形
    draw.ellipse([8, 15, 42, 35], fill=(150, 150, 150), outline=(100, 100, 100))

    # UFO顶部 - 透明罩
    draw.ellipse([15, 8, 35, 22], fill=(100, 200, 255), outline=(80, 150, 200))

    # UFO灯光（根据帧变化）
    light_offset = (frame % 3) * 2
    light_colors = [(255, 255, 0), (0, 255, 255), (255, 0, 255)]
    for i in range(3):
        angle = i * 120 + frame * 30
        rad = math.radians(angle)
        lx = cx + int(15 * math.cos(rad))
        ly = cy + int(10 * math.sin(rad))
        draw.ellipse([lx - 3, ly - 3, lx + 3, ly + 3], fill=light_colors[i])

    # UFO底部光束
    beam_offset = (frame % 2) * 2
    draw.polygon([(20, 35), (30, 35), (35, 45 + beam_offset), (15, 45 + beam_offset)],
                 fill=(100, 255, 100, 150))

    return img


def draw_bird_skin(frame, color):
    """绘制小鸟皮肤"""
    img = Image.new('RGBA', (50, 50), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # 小鸟身体
    draw.ellipse([10, 10, 40, 40], fill=color, outline=(max(0, color[0]-50), max(0, color[1]-50), max(0, color[2]-50)))

    # 小鸟眼睛
    draw.ellipse([25, 15, 35, 25], fill=(255, 255, 255), outline=(0, 0, 0))
    draw.ellipse([28, 18, 32, 22], fill=(0, 0, 0))

    # 小鸟嘴巴
    draw.polygon([(40, 25), (48, 28), (40, 31)], fill=(255, 200, 50))

    # 小鸟翅膀
    wing_offset = (frame % 3) * 3
    draw.polygon([(15, 25), (5, 20 + wing_offset), (15, 35)], fill=(max(0, color[0]-30), max(0, color[1]-30), max(0, color[2]-30)))

    return img


def draw_bee_skin(frame):
    """绘制蜜蜂皮肤"""
    img = Image.new('RGBA', (50, 50), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # 蜜蜂身体
    draw.ellipse([10, 15, 40, 40], fill=(255, 200, 0), outline=(200, 150, 0))

    # 蜜蜂条纹
    for i in range(3):
        y = 20 + i * 8
        draw.rectangle([15, y, 35, y + 4], fill=(0, 0, 0))

    # 蜜蜂眼睛
    draw.ellipse([28, 18, 34, 24], fill=(255, 255, 255), outline=(0, 0, 0))
    draw.ellipse([30, 20, 32, 22], fill=(0, 0, 0))

    # 蜜蜂触角
    draw.line([(30, 15), (25, 5)], fill=(0, 0, 0), width=2)
    draw.line([(30, 15), (35, 5)], fill=(0, 0, 0), width=2)
    draw.ellipse([23, 3, 27, 7], fill=(0, 0, 0))
    draw.ellipse([33, 3, 37, 7], fill=(0, 0, 0))

    # 蜜蜂翅膀
    wing_offset = (frame % 2) * 5
    draw.ellipse([5, 10 - wing_offset, 20, 25 - wing_offset], fill=(200, 200, 255, 150))
    draw.ellipse([30, 10 - wing_offset, 45, 25 - wing_offset], fill=(200, 200, 255, 150))

    return img


def draw_rocket_skin(frame):
    """绘制火箭皮肤"""
    img = Image.new('RGBA', (50, 50), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # 火箭主体
    draw.polygon([(25, 5), (15, 40), (35, 40)], fill=(200, 50, 50), outline=(150, 30, 30))

    # 火箭窗户
    draw.ellipse([20, 15, 30, 25], fill=(100, 200, 255), outline=(80, 150, 200))

    # 火箭尾翼
    draw.polygon([(15, 35), (5, 45), (15, 40)], fill=(200, 50, 50))
    draw.polygon([(35, 35), (45, 45), (35, 40)], fill=(200, 50, 50))

    # 火箭火焰
    flame_offset = (frame % 3) * 3
    draw.polygon([(20, 40), (25, 45 + flame_offset), (30, 40)], fill=(255, 200, 50))
    draw.polygon([(22, 40), (25, 42 + flame_offset), (28, 40)], fill=(255, 255, 100))

    return img


def draw_ghost_skin(frame):
    """绘制幽灵皮肤"""
    img = Image.new('RGBA', (50, 50), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # 幽灵身体
    draw.ellipse([10, 5, 40, 35], fill=(200, 200, 255), outline=(150, 150, 200))

    # 幽灵底部波浪
    for i in range(4):
        x = 10 + i * 8
        draw.ellipse([x, 30, x + 8, 40], fill=(200, 200, 255))

    # 幽灵眼睛
    draw.ellipse([18, 15, 24, 25], fill=(0, 0, 0))
    draw.ellipse([28, 15, 34, 25], fill=(0, 0, 0))

    # 幽灵嘴巴
    draw.arc([22, 22, 30, 30], 0, 180, fill=(0, 0, 0), width=2)

    return img


def draw_obstacle():
    """绘制太空站障碍物 - 上下两个太空站模块，中间有空隙"""
    w, h = 80, 700  # 增加高度到700，确保覆盖整个屏幕
    img = Image.new('RGBA', (w, h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # 空隙位置（图片中心）
    gap_center = h // 2
    gap_size = 150  # 空隙大小

    # 上太空站模块（从顶部到空隙上边缘）
    top_end = gap_center - gap_size // 2

    # 主体结构 - 金属灰色
    draw.rectangle([0, 0, w, top_end], fill=(100, 100, 110), outline=(70, 70, 80))

    # 太空站外壳 - 更厚的金属板
    draw.rectangle([0, 0, w, 15], fill=(80, 80, 90), outline=(60, 60, 70))
    draw.rectangle([0, top_end - 15, w, top_end], fill=(80, 80, 90), outline=(60, 60, 70))

    # 金属板纹理 - 横向线条
    for y in range(15, top_end - 15, 25):
        draw.line([(0, y), (w, y)], fill=(90, 90, 100), width=1)

    # 金属板纹理 - 纵向线条
    for x in range(0, w, 20):
        draw.line([(x, 15), (x, top_end - 15)], fill=(90, 90, 100), width=1)

    # 铆钉装饰 - 更密集
    for y in range(20, top_end - 20, 20):
        for x in range(10, w - 10, 20):
            draw.ellipse([x - 2, y - 2, x + 2, y + 2], fill=(70, 70, 80))

    # 灯光条 - 更宽
    draw.rectangle([0, top_end - 10, w, top_end], fill=(0, 180, 220))
    draw.rectangle([0, top_end - 8, w, top_end - 2], fill=(80, 220, 255))

    # 太空站窗户
    for x in range(15, w - 15, 30):
        draw.rectangle([x, 25, x + 15, 40], fill=(150, 200, 255), outline=(100, 150, 200))

    # 太空站天线
    draw.line([(w // 2, 0), (w // 2, -20)], fill=(150, 150, 160), width=3)
    draw.ellipse([w // 2 - 6, -24, w // 2 + 6, -12], fill=(255, 50, 50))
    draw.line([(w // 4, 0), (w // 4, -15)], fill=(150, 150, 160), width=2)
    draw.ellipse([w // 4 - 4, -19, w // 4 + 4, -11], fill=(255, 255, 50))

    # 下太空站模块（从空隙下边缘到底部）
    bottom_start = gap_center + gap_size // 2

    # 主体结构 - 金属灰色
    draw.rectangle([0, bottom_start, w, h], fill=(100, 100, 110), outline=(70, 70, 80))

    # 太空站外壳 - 更厚的金属板
    draw.rectangle([0, bottom_start, w, bottom_start + 15], fill=(80, 80, 90), outline=(60, 60, 70))
    draw.rectangle([0, h - 15, w, h], fill=(80, 80, 90), outline=(60, 60, 70))

    # 金属板纹理 - 横向线条
    for y in range(bottom_start + 15, h - 15, 25):
        draw.line([(0, y), (w, y)], fill=(90, 90, 100), width=1)

    # 金属板纹理 - 纵向线条
    for x in range(0, w, 20):
        draw.line([(x, bottom_start + 15), (x, h - 15)], fill=(90, 90, 100), width=1)

    # 铆钉装饰 - 更密集
    for y in range(bottom_start + 20, h - 20, 20):
        for x in range(10, w - 10, 20):
            draw.ellipse([x - 2, y - 2, x + 2, y + 2], fill=(70, 70, 80))

    # 灯光条 - 更宽
    draw.rectangle([0, bottom_start, w, bottom_start + 10], fill=(0, 180, 220))
    draw.rectangle([0, bottom_start + 2, w, bottom_start + 8], fill=(80, 220, 255))

    # 太空站窗户
    for x in range(15, w - 15, 30):
        draw.rectangle([x, h - 40, x + 15, h - 25], fill=(150, 200, 255), outline=(100, 150, 200))

    # 太空站天线
    draw.line([(w // 2, h), (w // 2, h + 20)], fill=(150, 150, 160), width=3)
    draw.ellipse([w // 2 - 6, h + 12, w // 2 + 6, h + 24], fill=(255, 50, 50))
    draw.line([(w // 4, h), (w // 4, h + 15)], fill=(150, 150, 160), width=2)
    draw.ellipse([w // 4 - 4, h + 11, w // 4 + 4, h + 19], fill=(255, 255, 50))

    return img


def draw_coin():
    """绘制金币"""
    img = Image.new('RGBA', (20, 20), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # 金币主体
    draw.ellipse([2, 2, 18, 18], fill=(255, 215, 0), outline=(200, 170, 0))

    # 金币高光
    draw.ellipse([4, 4, 10, 10], fill=(255, 240, 100))

    # 金币符号
    draw.text((7, 5), "$", fill=(200, 170, 0))

    return img


def draw_star():
    """绘制无敌星星"""
    img = Image.new('RGBA', (25, 25), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # 星星主体
    points = []
    for i in range(5):
        angle = math.radians(i * 72 - 90)
        x = 12 + int(10 * math.cos(angle))
        y = 12 + int(10 * math.sin(angle))
        points.append((x, y))
        angle2 = math.radians(i * 72 + 36 - 90)
        x2 = 12 + int(5 * math.cos(angle2))
        y2 = 12 + int(5 * math.sin(angle2))
        points.append((x2, y2))

    draw.polygon(points, fill=(255, 255, 0), outline=(200, 200, 0))

    return img


def draw_magnet():
    """绘制磁铁"""
    img = Image.new('RGBA', (25, 25), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # 磁铁主体
    draw.arc([2, 2, 23, 23], 0, 180, fill=(255, 0, 0), width=3)
    draw.arc([2, 2, 23, 23], 180, 360, fill=(0, 0, 255), width=3)

    # 磁铁两端
    draw.rectangle([2, 10, 8, 23], fill=(255, 0, 0))
    draw.rectangle([17, 10, 23, 23], fill=(0, 0, 255))

    return img


def draw_slow():
    """绘制减速器"""
    img = Image.new('RGBA', (25, 25), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # 时钟主体
    draw.ellipse([2, 2, 23, 23], fill=(0, 200, 0), outline=(0, 150, 0))

    # 时钟指针
    draw.line([(12, 12), (12, 6)], fill=(255, 255, 255), width=2)
    draw.line([(12, 12), (18, 12)], fill=(255, 255, 255), width=2)

    return img


def draw_shield():
    """绘制护盾"""
    img = Image.new('RGBA', (25, 25), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # 盾牌主体
    draw.polygon([(12, 2), (22, 8), (20, 20), (12, 24), (4, 20), (2, 8)],
                 fill=(100, 100, 255), outline=(80, 80, 200))

    # 盾牌装饰
    draw.polygon([(12, 6), (18, 10), (16, 18), (12, 20), (8, 18), (6, 10)],
                 fill=(150, 150, 255))

    return img


def draw_background():
    """绘制星空背景"""
    w, h = 600, 800
    img = Image.new('RGB', (w, h), (10, 10, 40))
    draw = ImageDraw.Draw(img)

    # 渐变背景（从深蓝到黑）
    for y in range(h):
        r = int(10 + 15 * (y / h))
        g = int(10 + 10 * (y / h))
        b = int(40 - 20 * (y / h))
        draw.line([(0, y), (w, y)], fill=(r, g, b))

    # 星星
    for _ in range(200):
        x = random.randint(0, w - 1)
        y = random.randint(0, h - 1)
        brightness = random.randint(150, 255)
        size = random.choice([1, 1, 1, 2])
        color = (brightness, brightness, brightness)
        if size == 1:
            draw.point((x, y), fill=color)
        else:
            draw.ellipse([x, y, x + size, y + size], fill=color)

    # 较亮的星星
    for _ in range(20):
        x = random.randint(0, w - 1)
        y = random.randint(0, h - 1)
        draw.ellipse([x - 1, y - 1, x + 1, y + 1], fill=(255, 255, 240))

    return img


def draw_ground():
    """绘制地面"""
    w, h = 600, 100
    img = Image.new('RGB', (w, h), (180, 100, 60))
    draw = ImageDraw.Draw(img)

    # 地面纹理
    for y in range(h):
        for x in range(0, w, 3):
            base_r = 180 - int(40 * (y / h))
            base_g = 100 - int(30 * (y / h))
            base_b = 60 - int(20 * (y / h))
            noise = random.randint(-15, 15)
            r = max(0, min(255, base_r + noise))
            g = max(0, min(255, base_g + noise))
            b = max(0, min(255, base_b + noise))
            draw.point((x, y), fill=(r, g, b))
            draw.point((x + 1, y), fill=(r, g, b))
            draw.point((x + 2, y), fill=(r, g, b))

    # 地面裂缝
    for _ in range(15):
        x = random.randint(0, w - 1)
        y = random.randint(10, h - 10)
        length = random.randint(20, 60)
        for dx in range(0, length, 2):
            dy = random.randint(-2, 2)
            px = x + dx
            py = y + dy
            if 0 <= px < w and 0 <= py < h:
                draw.point((px, py), fill=(120, 70, 40))

    return img


def main():
    import os
    out_dir = os.path.dirname(os.path.abspath(__file__))

    print("正在生成小鸟动画帧...")
    for i in range(3):
        img = draw_bird(i)
        img.save(os.path.join(out_dir, f"bird{i}.png"))
        print(f"  生成 bird{i}.png")

    print("正在生成皮肤...")
    # 红色小鸟
    for i in range(3):
        img = draw_bird_skin(i, (255, 80, 80))
        img.save(os.path.join(out_dir, f"bird_red{i}.png"))
        print(f"  生成 bird_red{i}.png")

    # 蓝色小鸟
    for i in range(3):
        img = draw_bird_skin(i, (80, 80, 255))
        img.save(os.path.join(out_dir, f"bird_blue{i}.png"))
        print(f"  生成 bird_blue{i}.png")

    # 蜜蜂
    for i in range(3):
        img = draw_bee_skin(i)
        img.save(os.path.join(out_dir, f"bee{i}.png"))
        print(f"  生成 bee{i}.png")

    # 火箭
    for i in range(3):
        img = draw_rocket_skin(i)
        img.save(os.path.join(out_dir, f"rocket{i}.png"))
        print(f"  生成 rocket{i}.png")

    # 幽灵
    for i in range(3):
        img = draw_ghost_skin(i)
        img.save(os.path.join(out_dir, f"ghost{i}.png"))
        print(f"  生成 ghost{i}.png")

    print("正在生成障碍物...")
    draw_obstacle().save(os.path.join(out_dir, "obstacle.png"))

    print("正在生成金币...")
    draw_coin().save(os.path.join(out_dir, "coin.png"))

    print("正在生成道具...")
    draw_star().save(os.path.join(out_dir, "star.png"))
    draw_magnet().save(os.path.join(out_dir, "magnet.png"))
    draw_slow().save(os.path.join(out_dir, "slow.png"))
    draw_shield().save(os.path.join(out_dir, "shield.png"))

    print("正在生成背景...")
    draw_background().save(os.path.join(out_dir, "bg.png"))

    print("正在生成地面...")
    draw_ground().save(os.path.join(out_dir, "ground.png"))

    print("所有图片生成完成！")


if __name__ == "__main__":
    main()
