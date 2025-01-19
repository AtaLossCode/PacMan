package chy.game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ghost {
    // 使用枚举来管理鬼的状态，避免字符串错误
    public enum GhostState {
        NORMAL,
        SCARED
    }

    private double ghostX, ghostY; // 鬼的位置，使用浮点数支持小数移动
    private double stepSize = 2; // 每次移动的步长
    private final double escapeStepSize = 1; // 逃离时的移动步长
    private double currentStepSize = stepSize; // 当前移动步长
    private double totalMoved = 0; // 累计移动的像素数
    private final int moveThreshold = 22; // 满 22 像素后才可以改变方向
    private String direction = "left"; // 鬼的初始移动方向
    private GhostState state = GhostState.NORMAL; // 鬼的状态
    private final Random random = new Random();
    private final Map map; // 地图，用于碰撞检测
    private final PacMan pacMan; // 用于追踪PacMan

    private boolean isEaten = false; // 标记鬼是否被吃掉
    private boolean isHardLevel; // 标记是否为困难关卡

    // 鬼的图像
    private final ImageIcon ghostNormalRight, ghostNormalLeft, ghostScaredRight, ghostScaredLeft;
    private ImageIcon currentGhostImage;

    public Ghost(Map map, PacMan pacMan, int initialX, int initialY, boolean isHardLevel) {
        this.map = map;
        this.pacMan = pacMan;
        this.isHardLevel = isHardLevel;

        // 加载鬼的图片
        ghostNormalRight = new ImageIcon(getClass().getResource("/Ghost1.gif"));
        ghostNormalLeft = new ImageIcon(getClass().getResource("/Ghost2.gif"));
        ghostScaredRight = new ImageIcon(getClass().getResource("/GhostScared1.gif"));
        ghostScaredLeft = new ImageIcon(getClass().getResource("/GhostScared2.gif"));

        // 初始化鬼的位置
        this.ghostX = initialX;
        this.ghostY = initialY;

        // 默认是鬼的正常状态，向左走
        currentGhostImage = ghostNormalLeft;
    }

    // 判断目标位置是否为墙
    private boolean CanMove(double targetX, double targetY) {
        char[][] mapData = map.getMap();

        int ghostWidth = getWidth();
        int ghostHeight = getHeight();

        int startMapX = (int) (targetX / pacMan.getGridSize());
        int startMapY = (int) (targetY / pacMan.getGridSize());
        int endMapX = (int) ((targetX + ghostWidth - 1) / pacMan.getGridSize());
        int endMapY = (int) ((targetY + ghostHeight - 1) / pacMan.getGridSize());

        for (int y = startMapY; y <= endMapY; y++) {
            for (int x = startMapX; x <= endMapX; x++) {
                if (y < 0 || y >= mapData.length || x < 0 || x >= mapData[0].length || mapData[y][x] == '#') {
                    return false;
                }
            }
        }

        return true;
    }

    // 第一关追踪逻辑
    private void chasePacMan() {
        if (isHardLevel) {
            Level2ChasePacMan();
        } else {
            Level1ChasePacMan();
        }
    }

    private void Level1ChasePacMan() {
        int pacManX = pacMan.getX();
        int pacManY = pacMan.getY();
        // 计算鬼与PacMan在x轴和y轴上的曼哈顿距离
        int deltaX = (int) Math.abs(pacManX - ghostX);
        int deltaY = (int) Math.abs(pacManY - ghostY);
        if (totalMoved < moveThreshold) {
            moveInDirection();
        } else {
            totalMoved = 0; // 重置累计移动
            // 根据曼哈顿距离选择追踪方向
            if (deltaX > deltaY) {
                if (pacManX > ghostX && CanMove(ghostX + stepSize, ghostY)) {
                    ghostX += stepSize;
                    direction = "right";
                    totalMoved += stepSize;
                } else if (pacManX < ghostX && CanMove(ghostX - stepSize, ghostY)) {
                    ghostX -= stepSize;
                    direction = "left";
                    totalMoved += stepSize;
                } else {
                    wanderRandomly();
                }
            } else {
                if (pacManY > ghostY && CanMove(ghostX, ghostY + stepSize)) {
                    ghostY += stepSize;
                    direction = "down";
                    totalMoved += stepSize;
                } else if (pacManY < ghostY && CanMove(ghostX, ghostY - stepSize)) {
                    ghostY -= stepSize;
                    direction = "up";
                    totalMoved += stepSize;
                } else {
                    wanderRandomly();
                }
            }
        }
    }

    //第二关追踪逻辑
    private void Level2ChasePacMan() {
        if (totalMoved >= moveThreshold) {
            // 计算所有可行方向的距离，选择最短的
            List<String> possibleDirections = new ArrayList<>();
            if (CanMove(ghostX, ghostY - currentStepSize)) possibleDirections.add("up");
            if (CanMove(ghostX, ghostY + currentStepSize)) possibleDirections.add("down");
            if (CanMove(ghostX - currentStepSize, ghostY)) possibleDirections.add("left");
            if (CanMove(ghostX + currentStepSize, ghostY)) possibleDirections.add("right");

            String bestDirection = direction;
            double minDistance = Double.MAX_VALUE;

            for (String dir : possibleDirections) {
                double newX = ghostX;
                double newY = ghostY;
                switch (dir) {
                    case "up": newY -= currentStepSize; break;
                    case "down": newY += currentStepSize; break;
                    case "left": newX -= currentStepSize; break;
                    case "right": newX += currentStepSize; break;
                }

                // 计算每个方向的距离
                double distance = Math.hypot(pacMan.getX() - newX, pacMan.getY() - newY);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestDirection = dir;
                }
            }

            // 更新方向
            if (!bestDirection.equals(direction)) {
                direction = bestDirection;
            }

            totalMoved = 0;
        }

        moveInDirection();
    }

    //逃离逻辑
    private void escapePacMan() {
        int pacManX = pacMan.getX();
        int pacManY = pacMan.getY();
        // 计算鬼与PacMan在x轴和y轴上的差值
        double deltaX = ghostX - pacManX;
        double deltaY = pacManY - ghostY;
        // 设置逃离步长
        currentStepSize = escapeStepSize;
        if (totalMoved < moveThreshold) {
            moveInDirection();
        } else {
            totalMoved = 0; // 重置累计移动
            // 先判断X轴的方向
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                if (deltaX > 0 && CanMove(ghostX + currentStepSize, ghostY)) {
                    ghostX += currentStepSize;
                    direction = "right";
                    totalMoved += currentStepSize;
                } else if (deltaX < 0 && CanMove(ghostX - currentStepSize, ghostY)) {
                    ghostX -= currentStepSize;
                    direction = "left";
                    totalMoved += currentStepSize;
                } else {
                    wanderRandomly();
                }
            } else {
                // 然后判断Y轴的方向
                if (deltaY > 0 && CanMove(ghostX, ghostY + currentStepSize)) {
                    ghostY += currentStepSize;
                    direction = "down";
                    totalMoved += currentStepSize;
                } else if (deltaY < 0 && CanMove(ghostX, ghostY - currentStepSize)) {
                    ghostY -= currentStepSize;
                    direction = "up";
                    totalMoved += currentStepSize;
                } else {
                    wanderRandomly();
                }
            }
        }
    }


    // 随机游荡的逻辑
    private void wanderRandomly() {
        // 随机选择一个方向，但改为基于周围环境
        List<String> possibleDirections = new ArrayList<>();

        // 判断当前的四个方向是否可以移动
        if (CanMove(ghostX, ghostY - currentStepSize)) possibleDirections.add("up");
        if (CanMove(ghostX, ghostY + currentStepSize)) possibleDirections.add("down");
        if (CanMove(ghostX - currentStepSize, ghostY)) possibleDirections.add("left");
        if (CanMove(ghostX + currentStepSize, ghostY)) possibleDirections.add("right");

        if (possibleDirections.isEmpty()) {
            // 如果没有可用的移动方向，随机停下来（即不做任何移动）
            return;
        }

        // 如果有多个方向可以移动，从这些方向中随机选择一个
        String chosenDirection = possibleDirections.get(random.nextInt(possibleDirections.size()));

        // 更新位置
        switch (chosenDirection) {
            case "up":
                ghostY -= currentStepSize;
                break;
            case "down":
                ghostY += currentStepSize;
                break;
            case "left":
                ghostX -= currentStepSize;
                break;
            case "right":
                ghostX += currentStepSize;
                break;
        }

        // 更新方向和累计移动
        direction = chosenDirection;
        totalMoved = 0; // 每次随机移动后重置累计移动的像素
        System.out.printf("随机移动：%s\n", direction);
    }


    // 根据累计移动的像素数来决定是否改变方向
    private void moveInDirection() {
        if (totalMoved < moveThreshold) {
            // 在还没移动满22像素之前，只按照当前方向移动currentStepSize像素
            switch (direction) {
                case "right":
                    if (CanMove(ghostX + currentStepSize, ghostY)) {
                        ghostX += currentStepSize;
                        totalMoved += currentStepSize;
                        System.out.printf("%s累计移动：%.1f像素\n", direction, totalMoved);
                    } else {
                        wanderRandomly();
                    }
                    break;
                case "left":
                    if (CanMove(ghostX - currentStepSize, ghostY)) {
                        ghostX -= currentStepSize;
                        totalMoved += currentStepSize;
                        System.out.printf("%s累计移动：%.1f像素\n", direction, totalMoved);
                    } else {
                        wanderRandomly();
                    }
                    break;
                case "up":
                    if (CanMove(ghostX, ghostY - currentStepSize)) {
                        ghostY -= currentStepSize;
                        totalMoved += currentStepSize;
                        System.out.printf("%s累计移动：%.1f像素\n", direction, totalMoved);
                    } else {
                        wanderRandomly();
                    }
                    break;
                case "down":
                    if (CanMove(ghostX, ghostY + currentStepSize)) {
                        ghostY += currentStepSize;
                        totalMoved += currentStepSize;
                        System.out.printf("%s累计移动：%.1f像素\n", direction, totalMoved);
                    } else {
                        wanderRandomly();
                    }
                    break;
            }
        }
    }

    // 更新鬼的位置和动画
    public void update() {
        if (isEaten) return; // 鬼被吃掉后不再更新

        System.out.println("Ghost update. State: " + state);

        if (state == GhostState.NORMAL) {
            // 追踪PacMan的行为
            chasePacMan();
            // 更新鬼的正常状态图像
            if (direction.equals("right")) {
                currentGhostImage = ghostNormalRight;
            } else if (direction.equals("left")) {
                currentGhostImage = ghostNormalLeft;
            } else if (direction.equals("up") || direction.equals("down")) {
                currentGhostImage = ghostNormalRight; // 默认使用右向图像
            }
        } else if (state == GhostState.SCARED) {
            // 逃离PacMan
            escapePacMan();
            // 更新鬼的scared状态图像
            if (direction.equals("right")) {
                currentGhostImage = ghostScaredRight;
            } else if (direction.equals("left")) {
                currentGhostImage = ghostScaredLeft;
            } else if (direction.equals("up") || direction.equals("down")) {
                currentGhostImage = ghostScaredRight; // 默认使用右向图像
            }
        }
    }

    // 绘制鬼的图像
    public void draw(Graphics g) {
        if (!isEaten) {
            g.drawImage(currentGhostImage.getImage(), (int) ghostX, (int) ghostY, null);
        }
    }

    // 获取鬼的当前位置
    public double getX() {
        return ghostX;
    }

    public double getY() {
        return ghostY;
    }

    // 设置鬼的状态
    public void setState(GhostState state) {
        this.state = state;
        System.out.println("Ghost state set to: " + state);
        if (state == GhostState.SCARED) {
            currentStepSize = isHardLevel ? escapeStepSize : stepSize;
        } else {
            currentStepSize = stepSize;
        }
    }

    // 获取鬼的当前状态
    public GhostState getState() {
        return state;
    }

    // 获取鬼的移动方向
    public String getDirection() {
        return direction;
    }

    // 获取鬼的宽度
    public int getWidth() {
        return currentGhostImage.getIconWidth();
    }

    // 获取鬼的高度
    public int getHeight() {
        return currentGhostImage.getIconHeight();
    }

    // 标记鬼被吃掉
    public void eat() {
        isEaten = true;
        System.out.println("Ghost has been eaten.");
    }

    // 检查鬼是否被吃掉
    public boolean isEaten() {
        return isEaten;
    }

    

}

