package chy.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel {

    private final PacMan pacman; // PacMan对象，负责PacMan的状态和行为
    private final Map map; // 游戏地图对象，负责地图的绘制和碰撞检测
    private final List<Ghost> ghosts;  // 用于管理鬼的实例

    // 用于判断游戏结束
    private boolean gameOver = false;
    private boolean gameWon = false;
    private Timer animationTimer; // 动画计时器
    private Timer scaredTimer; // scared状态计时器

    // 用于重新绘制闪烁效果
    private Timer repaintTimer;

    private String level; // 关卡类型
    private JButton returnButton; // 返回按钮
    private JButton returnButton2;


    public GamePanel(String level) {
        this.level = level;
        setBackground(Color.BLACK); // 设置背景颜色为黑色，符合游戏主题
        setPreferredSize(new Dimension(385, 450)); // 设置面板的首选尺寸为385*450

        // 初始化地图和PacMan
        map = new Map(level.equalsIgnoreCase("HARD")); // 创建地图对象
        pacman = new PacMan(map); // 创建PacMan对象，并将地图传递给它
        ghosts = new ArrayList<>();  // 初始化鬼

        // 设置PacMan的初始位置为 (1,1)
        pacman.setPosition(1 * pacman.getGridSize(), 1 * pacman.getGridSize());

        // 定义鬼魂的初始位置
        int[][] ghostInitialPositions;
        if (level.equalsIgnoreCase("HARD")) {
            ghostInitialPositions = new int[][]{
                    {15 * pacman.getGridSize(), 15 * pacman.getGridSize()},
                    {10 * pacman.getGridSize(), 13 * pacman.getGridSize()},
                    {2 * pacman.getGridSize(), 15 * pacman.getGridSize()},
                    {15 * pacman.getGridSize(), 2 * pacman.getGridSize()},
                    {10 * pacman.getGridSize(), 6 * pacman.getGridSize()},
                    {5 * pacman.getGridSize(), 10 * pacman.getGridSize()}
            };
        } else { // 默认普通关卡
            ghostInitialPositions = new int[][]{
                    {15 * pacman.getGridSize(), 15 * pacman.getGridSize()},
                    {15 * pacman.getGridSize(), 13 * pacman.getGridSize()},
                    {2 * pacman.getGridSize(), 15 * pacman.getGridSize()}
            };
        }

        // 添加鬼到列表中
        for (int i = 0; i < ghostInitialPositions.length; i++) {
            int initialX = ghostInitialPositions[i][0];
            int initialY = ghostInitialPositions[i][1];
            Ghost ghost = new Ghost(map, pacman, initialX, initialY, level.equalsIgnoreCase("HARD"));
            ghosts.add(ghost);
        }

        // 动画计时器，每100ms调用一次pacman.animate()来更新动画
        animationTimer = new Timer(100, e -> {
            if(!gameOver) {
                pacman.animate(); // 更新PacMan动画
                for (Ghost ghost : ghosts) {
                    ghost.update(); // 更新每个鬼的动画
                }

                // 检查是否吃下了 FrightFruit
                if (pacman.justAteFrightFruit()) {
                    map.setScared(true); // 设置地图为scared状态，墙壁变紫色
                    for (Ghost ghost : ghosts) {
                        ghost.setState(Ghost.GhostState.SCARED); // 设置每个鬼为scared状态，逃离PacMan
                    }
                    pacman.resetJustAteFrightFruit(); // 重置标志

                    System.out.println("PacMan ate FrightFruit! Ghost is scared.");

                    // 启动scared状态计时器，3秒后恢复正常状态
                    if (scaredTimer != null && scaredTimer.isRunning()) {
                        scaredTimer.stop(); // 停止任何现有的scared计时器
                    }
                    scaredTimer = new Timer(30000, event -> {
                        map.setScared(false); // 恢复地图正常状态，墙壁颜色恢复
                        for (Ghost ghost : ghosts) {
                            ghost.setState(Ghost.GhostState.NORMAL); // 恢复每个鬼的正常状态，追踪PacMan
                        }
                        scaredTimer.stop(); // 停止计时器
                        System.out.println("Scared state ended. Ghost back to normal.");
                    });
                    scaredTimer.setRepeats(false); // 计时器只触发一次
                    scaredTimer.start();
                }

                // 检查每个鬼与PacMan是否碰撞
                for (Ghost ghost : ghosts) {
                    if (ghost.isEaten()) {
                        continue; // 跳过已经被吃掉的鬼
                    }
                    if (isCollision(ghost)) {
                        System.out.println("Collision detected. Ghost state: " + ghost.getState());
                        if (!pacman.isInvincible()) { // 只有在非无敌状态下才处理生命减少
                            if (ghost.getState() == Ghost.GhostState.SCARED) {
                                // PacMan 吃掉鬼
                                map.addScore(20); // 增加20分
                                ghost.eat(); // 标记鬼为已吃掉
                                System.out.println("Ghost eaten! Score +20");
                            } else if (ghost.getState() == Ghost.GhostState.NORMAL) {
                                // 鬼吃掉PacMan，减少生命并重生
                                pacman.loseLife(); // 减少一条生命
                                System.out.println("PacMan was eaten! Lives left: " + pacman.getLives());
                                pacman.SettingInitialState();//初始化PacMan

                                if (pacman.hasLives()) {
                                    pacman.respawn(); // 重生
                                } else {
                                    gameOver = true;
                                    animationTimer.stop(); // 停止动画计时器
                                    System.out.println("PacMan was eaten! Game Over");
                                }
                            }
                        }
                    }
                }




                // 检查是否所有食物被吃完
                if (map.getTotalFruit() == 0) {
                    // 如果最后一个 FIGHTFruit 被吃掉，刷新一个新的
                    map.refreshFrightFruit();
                }

                // 胜利条件：检查是否消灭所有鬼魂且生命值大于0
                if (areAllGhostsEaten() && pacman.hasLives()) {
                    pacman.isMoving(false); // 停止PacMan的移动
                    setFocusable(false); // 停止接收键盘输入
                    animationTimer.stop(); // 停止动画计时器
                    gameWon = true; // 设置游戏获胜状态
                    System.out.println("All ghosts eaten! You Win!");
                }

                repaint(); // 重新绘制整个面板
            }
        });
        animationTimer.start(); // 启动计时器

        // 键盘监听器
        setFocusable(true); // 允许面板接收键盘输入
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameOver && !map.isGameWon()) { // 只有在游戏未结束且未获胜时处理键盘输入
                    pacman.HandleKeyPress(e.getKeyCode()); // 将按下的键传递给PacMan处理
                }
            }
        });

        // 设置闪烁效果的定时器，定时重绘面板以显示闪烁
        repaintTimer = new Timer(100, e -> repaint());
        repaintTimer.start();
    }

    // 判断鬼是否与PacMan发生碰撞
    private boolean isCollision(Ghost ghost) {
        // 获取PacMan和鬼的位置及大小
        Rectangle pacmanRect = new Rectangle(pacman.getX(), pacman.getY(), pacman.getGridSize(), pacman.getGridSize());
        Rectangle ghostRect = new Rectangle((int) ghost.getX(), (int) ghost.getY(), ghost.getWidth(), ghost.getHeight());

        return pacmanRect.intersects(ghostRect);
    }

    // 检查是否所有鬼都已经被吃掉
    private boolean areAllGhostsEaten() {
        for (Ghost ghost : ghosts) {
            if (!ghost.isEaten()) {
                return false; // 只要有一个鬼没有被吃掉，就返回false
            }
        }
        return true; // 所有鬼都被吃掉了
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 绘制地图
        map.draw(g); // 调用地图的绘制方法

        // 绘制所有鬼
        for (Ghost ghost : ghosts) {
            ghost.draw(g);
        }

        // 绘制分数
        g.setColor(Color.BLUE); // 设置颜色为蓝色
        g.setFont(new Font("Arial", Font.BOLD, 20)); // 设置字体
        g.drawString("Score: " + map.getScore(), getWidth() - 120, getHeight() - 10); // 绘制分数

        // 绘制生命
        drawLives(g);

        // 如果PacMan处于无敌状态且不可见，则跳过绘制
        if (pacman.isInvincible() && !pacman.isVisible()) {
            return;  // PacMan不可见时不绘制
        }

        // 绘制PacMan
        pacman.draw(g); // 调用PacMan的绘制方法

        // 游戏胜利提示
        if (gameWon) {
            g.setColor(Color.GREEN); // 设置颜色为绿色
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("You Win!", getWidth() / 2 - 110, getHeight() / 2);
            setFocusable(false); // 停止接收键盘输入

            // 创建返回按钮
            if (returnButton == null) {
                returnButton = new JButton("返回");
                returnButton.setForeground(Color.BLACK);
                returnButton.setBounds(getWidth() / 2 - 60, getHeight() / 2 + 50, 120, 40); // 设置按钮位置
                returnButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
                returnButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // 返回到levelselection界面
                        returnToLevelSelection();
                    }
                });
                add(returnButton); // 添加按钮到面板
                returnButton.setVisible(true);
            }
        }

        // 检查游戏结束
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.setColor(Color.RED);
            g.drawString("Game Over", getWidth() / 2 - 120, getHeight() / 2);
            setFocusable(false); // 停止接收键盘输入

            // 创建返回按钮
            if (returnButton2 == null) {
                returnButton2 = new JButton("返回");
                returnButton2.setForeground(Color.BLACK);
                returnButton2.setBounds(getWidth() / 2 - 60, getHeight() / 2 + 50, 120, 40); // 设置按钮位置
                returnButton2.setFont(new Font("SansSerif", Font.PLAIN, 18));
                returnButton2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // 返回到levelselection界面
                        returnToLevelSelection();
                    }
                });
                add(returnButton2); // 添加按钮到面板
                returnButton2.setVisible(true);
            }
        }

    }

    // 返回到LevelSelection界面的方法
    private void returnToLevelSelection() {
        // 销毁当前游戏界面
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this); // 获取父窗口
        if (topFrame != null) {
            topFrame.dispose(); // 关闭当前游戏窗口
        }

        // 创建并显示 LevelSelection 界面
        LevelSelection levelSelection = new LevelSelection();
        levelSelection.setVisible(true);
    }

    // 绘制生命图标
    private void drawLives(Graphics g) {
        int lifeCount = pacman.getLives();
        for (int i = 0; i < lifeCount; i++) {
            g.drawImage(pacman.lifeImage, 10 + i * (pacman.getGridSize() + 5), getHeight() - 30, pacman.getGridSize(), pacman.getGridSize(), null);
        }
    }
}



