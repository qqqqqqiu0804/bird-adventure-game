# 飞翔小鸟 - 冒险版 设计文档

> 日期：2026-06-02
> 项目：飞翔小鸟 - 冒险版（Java Swing Flappy Bird 增强版）
> 目标：闯关制 + 复活系统 + 道具系统 + 金币系统

---

## 1. 游戏概述

### 1.1 游戏主题
- 可爱小鸟在森林中冒险
- 点击控制小鸟飞翔，躲避障碍物，收集金币和道具
- 目标：闯过所有关卡，拯救被困的鸟伙伴

### 1.2 核心玩法
- 点击鼠标/空格键控制小鸟上升
- 躲避上下管道障碍物
- 收集金币和道具
- 闯关制，每关有不同目标

---

## 2. 核心系统设计

### 2.1 闯关系统
- **关卡数量**：20关
- **每关目标**：
  - 第1-5关：基础关，飞过10个障碍物
  - 第6-10关：收集关，吃到15个金币
  - 第11-15关：挑战关，飞过20个障碍物且不能死亡
  - 第16-20关：Boss关，躲避Boss攻击

### 2.2 复活系统
- **第一次免疫**：每关开始第一次死亡免费复活
- **复活效果**：原地复活 + 3秒无敌
- **后续复活**：花费50金币或观看广告

### 2.3 道具系统
| 道具 | 效果 | 持续时间 | 获取方式 |
|------|------|----------|----------|
| 无敌星星 | 无敌状态 | 5秒 | 随机出现 |
| 磁铁 | 自动吸金币 | 8秒 | 金币购买 |
| 减速器 | 游戏速度减慢 | 6秒 | 通关奖励 |
| 护盾 | 抵挡一次碰撞 | 一次性 | 金币购买 |

### 2.4 金币系统
- **获取方式**：
  - 吃金币（缝隙中随机出现）
  - 通关奖励（根据表现评分）
  - 成就奖励（首次通关、无死亡通关等）
- **消费方式**：
  - 复活：50金币
  - 道具购买：30-100金币
  - 皮肤解锁：200-500金币
  - 关卡跳过：100金币

---

## 3. 技术实现方案

### 3.1 方案选择
- **方案A：纯Java实现**
- 使用Java Swing图形界面
- 所有逻辑在本地运行
- 数据保存在本地文件

### 3.2 核心类设计

| 类名 | 职责 |
|------|------|
| `BirdGame` | 主游戏类，管理游戏循环和状态 |
| `Bird` | 小鸟类，控制飞行和碰撞检测 |
| `Obstacle` | 障碍物类，管理管道/柱子 |
| `Coin` | 金币类，可收集的金币 |
| `PowerUp` | 道具类，各种增益效果 |
| `Level` | 关卡类，管理关卡配置 |
| `GameState` | 游戏状态枚举 |
| `GameConfig` | 游戏配置常量 |
| `ScoreManager` | 分数管理，保存最高分和金币 |
| `SoundManager` | 音效管理 |
| `ParticleSystem` | 粒子特效 |

### 3.3 文件结构
```
cn/edu/huse/game/
├── BirdGame.java          # 主游戏类
├── Bird.java              # 小鸟类
├── Obstacle.java          # 障碍物类
├── Coin.java              # 金币类
├── PowerUp.java           # 道具类
├── Level.java             # 关卡类
├── GameState.java         # 游戏状态枚举
├── GameConfig.java        # 游戏配置常量
├── ScoreManager.java      # 分数管理
├── SoundManager.java      # 音效管理
├── ParticleSystem.java    # 粒子特效
├── generate_images.py     # 图片生成脚本
└── generate_sounds.py     # 音效生成脚本
```

---

## 4. 实现顺序

| 步骤 | 内容 | 涉及文件 |
|------|------|----------|
| 1 | 基础框架：GameConfig、GameState、Bird、Obstacle | GameConfig.java, GameState.java, Bird.java, Obstacle.java |
| 2 | 核心玩法：BirdGame、碰撞检测、计分 | BirdGame.java |
| 3 | 闯关系统：Level类、关卡切换 | Level.java |
| 4 | 复活系统：免费复活、金币复活 | BirdGame.java |
| 5 | 道具系统：PowerUp类、道具效果 | PowerUp.java |
| 6 | 金币系统：Coin类、金币收集 | Coin.java |
| 7 | 数据持久化：ScoreManager、金币保存 | ScoreManager.java |
| 8 | 音效系统：SoundManager | SoundManager.java |
| 9 | 粒子特效：ParticleSystem | ParticleSystem.java |
| 10 | 视觉打磨：UI美化、动画效果 | BirdGame.java |

---

## 5. 注意事项

- 保持包名不变：`cn.edu.huse.game`
- 资源加载使用 `getClass().getResource()`
- 兼容 Java 8+
- 图片资源通过 Python 脚本生成
- 音效容错：所有播放方法必须 try-catch
- 数据保存路径：`System.getProperty("user.home") + "/.bird_adventure/"`
