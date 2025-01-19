package chy.game;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;
import javax.swing.Timer;

public class PacMan {
    private static final int STEP_SIZE = 2; // 每次移动的像素数
    private static final int GRID_SIZE = 22; // 每格的像素大小

    private int pacmanX = 100; // PacMan的初始X坐标
    private int pacmanY = 100; // PacMan的初始Y坐标
    private String direction = "right"; // 初始方向为向右
    private int animationStep = 1; // 动画帧，控制PacMan动画的帧数

    private boolean isMoving = false; // 当前是否在移动
    private String moveDirection = ""; // 当前的移动方向

    private float distanceMoved = 0; // 已移动的距离

    private final Map map; // 地图对象，用来进行碰撞检测

    private boolean justAteFrightFruit = false; // 标记是否刚吃下 FrightFruit

    // 有关生命的变量
    private int lives = 3; // 初始生命数量
    private boolean isInvincible = false; // 无敌状态
    private Timer invincibleTimer; // 无敌时间计时器
    private boolean isVisible = true; // 用于闪烁效果

    // PacMan的生命图标
    public final ImageIcon lifeIcon = new ImageIcon(getClass().getResource("/PacMan3left.gif"));
    public final Image lifeImage = lifeIcon.getImage();

    //把distanceMoved置0
    public void SettingInitialState(){
        distanceMoved = 0;
        isMoving = false;
        direction = "right";
        moveDirection = "right";
    }

    public int getGridSize(){
        return GRID_SIZE;
    }

    //访问是否移动的isMoving变量
    public void isMoving(boolean moving) {
        this.isMoving = moving;
    }

    // 获取PacMan的X坐标（以格子为单位）
    public int getX() {
        return pacmanX;
    }

    // 获取PacMan的Y坐标（以格子为单位）
    public int getY() {
        return pacmanY;
    }

    // 获取PacMan的方向（向上、向下、向左、向右）
    public String getDirection() {
        return direction;
    }

    // 构造方法，接收地图对象
    public PacMan(Map map) {
        this.map = map; // 初始化地图对象
        initializePosition(); // 初始化PacMan的位置
    }

    // 初始化PacMan的位置：固定在(1,1)
    private void initializePosition() {
        pacmanX = 1 * GRID_SIZE;
        pacmanY = 1 * GRID_SIZE;
    }


    // 添加设置 PacMan 位置的方法
    public void setPosition(int x, int y) {
        this.pacmanX = x;
        this.pacmanY = y;
    }

    // 获取剩余生命数量
    public int getLives() {
        return lives;
    }

    // 减少一条生命
    public void loseLife() {
        lives--;
    }

    // 设置PacMan为无敌状态，并启动闪烁效果
    public void setInvincible() {
        isInvincible = true;
        isVisible = true;

        // 如果计时器已经存在，则先停止并重新启动
        if (invincibleTimer != null && invincibleTimer.isRunning()) {
            invincibleTimer.stop();
        }

        // 设置闪烁计时器，每200ms切换可见性
        Timer blinkTimer = new Timer(300, e -> {
            isVisible = !isVisible;
        });
        blinkTimer.start();

        // 设置无敌时间计时器，3秒后取消无敌状态
        invincibleTimer = new Timer(30000, e -> {
            isInvincible = false;
            isVisible = true;
            blinkTimer.stop();
        });
        invincibleTimer.setRepeats(false);
        invincibleTimer.start();
    }

    // 判断PacMan是否处于无敌状态
    public boolean isInvincible() {
        return isInvincible;
    }

    // 判断PacMan是否可见（用于闪烁效果）
    public boolean isVisible() {
        return isVisible;
    }

    // 处理键盘按键的逻辑，控制PacMan的移动方向
    public void HandleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP: // 如果按下的是“向上”键
                HandleDirectionChange("up"); // 处理向上移动的逻辑
                break;
            case KeyEvent.VK_DOWN: // 如果按下的是“向下”键
                HandleDirectionChange("down"); // 处理向下移动的逻辑
                break;
            case KeyEvent.VK_LEFT: // 如果按下的是“向左”键
                HandleDirectionChange("left"); // 处理向左移动的逻辑
                break;
            case KeyEvent.VK_RIGHT: // 如果按下的是“向右”键
                HandleDirectionChange("right"); // 处理向右移动的逻辑
                break;
        }
    }

    // 处理PacMan的方向切换逻辑
    private void HandleDirectionChange(String newDirection) {
        // 如果当前方向与目标方向相反，则可以立即切换方向
        if (moveDirection.equals(OppositeDirection(newDirection))) {
            moveDirection = newDirection;
            direction = newDirection;
            isMoving = true;
        }
        // 如果PacMan没有在移动，或者已经移完一整格（移动到格子边界），则可以切换方向
        else if (!isMoving || (pacmanX % GRID_SIZE == 0 && pacmanY % GRID_SIZE == 0)) {
            if (CanMove(newDirection)) { // 判断该方向是否可移动
                moveDirection = newDirection;
                direction = newDirection;
                isMoving = true;
            }
        }
    }

    // 获取与当前方向相反的方向
    private String OppositeDirection(String direction) {
        return switch (direction) {
            case "up" -> "down";
            case "down" -> "up";
            case "left" -> "right";
            case "right" -> "left";
            default -> direction;
        };
    }

    // 更新PacMan的动画和状态
    public void animate() {
        if (isMoving) {
            Move(); // 如果PacMan正在移动，则继续移动
        }

        animationStep++; // 更新动画帧
        if (animationStep > 4) animationStep = 1; // 循环动画帧
    }

    // 计算PacMan的移动
    private void Move() {
        switch (moveDirection) {
            case "up" -> pacmanY -= STEP_SIZE; // 向上移动
            case "down" -> pacmanY += STEP_SIZE; // 向下移动
            case "left" -> pacmanX -= STEP_SIZE; // 向左移动
            case "right" -> pacmanX += STEP_SIZE; // 向右移动
        }

        // 计算方向上的位移
        switch (moveDirection) {
            case "up", "right" -> {
                distanceMoved += STEP_SIZE;// 垂直方向：累加位移
//                System.out.println(distanceMoved);
            }
            case "down", "left" -> {
                distanceMoved -= STEP_SIZE;  // 水平方向：累减位移
//                System.out.println(distanceMoved);
            }
        }

        if (Math.abs(distanceMoved) >= GRID_SIZE || distanceMoved == 0) { // 每移动22像素或者来回移动归零后检查一次是否可以继续移动
            if (!CanMove(moveDirection)) { // 如果遇到障碍物，停止移动
                isMoving = false;
            }
            distanceMoved = 0; // 重置已移动的距离
        }

        // 检查是否吃到食物
        int mapX = pacmanX / GRID_SIZE;
        int mapY = pacmanY / GRID_SIZE;
        if (map.getMap()[mapY][mapX] == '*' || map.getMap()[mapY][mapX] == '!') {
            if (map.getMap()[mapY][mapX] == '!') {
                map.clearFrightFruit(mapX, mapY); // 清除FrightFruit
                justAteFrightFruit = true; // 标记为刚吃下 FrightFruit
            } else {
                map.clearNormalFood(mapX, mapY); // 清除普通食物
            }
        }

    }

    // 判断PacMan是否能够在给定方向上继续移动
    private boolean CanMove(String direction) {
        int nextX = pacmanX;
        int nextY = pacmanY;

        // 根据方向计算下一步的位置
        switch (direction) {
            case "up" -> nextY -= STEP_SIZE * 11;
            case "down" -> nextY += STEP_SIZE * 11;
            case "left" -> nextX -= STEP_SIZE * 11;
            case "right" -> nextX += STEP_SIZE * 11;
        }

        // 转换为地图坐标
        int mapX = nextX / GRID_SIZE;
        int mapY = nextY / GRID_SIZE;

        // 如果越界，不能移动
        if (mapY < 0 || mapY >= map.getMap().length || mapX < 0 || mapX >= map.getMap()[0].length) {
            return false;
        }

        // 如果是墙壁，不能移动
        return map.getMap()[mapY][mapX] != '#';
    }

    // 绘制PacMan图像
    public void draw(Graphics g) {
        Image pacmanImage = loadImage(direction, animationStep); // 加载对应方向和帧数的图片
        g.drawImage(pacmanImage, pacmanX, pacmanY, GRID_SIZE, GRID_SIZE, null); // 绘制PacMan
    }

    // 加载PacMan的图片
    private Image loadImage(String direction, int step) {
        String path = String.format("/PacMan%d%s.gif", step, direction); // 根据当前帧数和方向生成图片路径
        URL resource = getClass().getResource(path);
        if (resource == null) {
            System.err.println("无法加载PacMan图片: " + path);
            System.exit(1); // 输出错误信息并终止程序
        }
        return new ImageIcon(resource).getImage(); // 成功加载图片
    }

    // 新增方法：检查是否刚吃下 FrightFruit
    public boolean justAteFrightFruit() {
        return justAteFrightFruit;
    }

    // 新增方法：重置刚吃下 FrightFruit 的标志
    public void resetJustAteFrightFruit() {
        justAteFrightFruit = false;
    }

    // 新增方法：重置PacMan的位置并设置无敌状态
    public void respawn() {
        setPosition(2 * GRID_SIZE, 1 * GRID_SIZE); // 重置位置到(1,1)
        setInvincible(); // 设置无敌状态
    }

    // 新增方法：获取是否还有生命
    public boolean hasLives() {
        return lives > 0;
    }
}
