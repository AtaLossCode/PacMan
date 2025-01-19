package chy.game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Map {
    private final char[][] map = {
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', ' ', ' ', ' ', ' ', '#', '#', ' ', ' ', ' ', '#', '#', ' ', ' ', ' ', ' ', '#'},
            {'#', ' ', '#', '#', ' ', ' ', ' ', ' ', '#', ' ', ' ', ' ', ' ', '#', '#', ' ', '#'},
            {'#', ' ', ' ', ' ', ' ', '#', '#', ' ', ' ', ' ', '#', '#', ' ', ' ', ' ', ' ', '#'},
            {'#', ' ', '#', '#', ' ', ' ', '#', ' ', '#', ' ', '#', ' ', ' ', '#', '#', ' ', '#'},
            {'#', ' ', '#', '#', '#', ' ', '#', ' ', '#', ' ', '#', ' ', '#', '#', '#', ' ', '#'},
            {'#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#'},
            {'#', '#', ' ', '#', ' ', '#', ' ', '#', '#', '#', ' ', '#', ' ', '#', ' ', '#', '#'},
            {'#', '#', ' ', '#', ' ', '#', ' ', '#', '#', '#', ' ', '#', ' ', '#', ' ', '#', '#'},
            {'#', '#', ' ', '#', ' ', '#', ' ', '#', '#', '#', ' ', '#', ' ', '#', ' ', '#', '#'},
            {'#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#'},
            {'#', ' ', '#', '#', '#', '#', ' ', '#', ' ', '#', ' ', '#', '#', '#', '#', ' ', '#'},
            {'#', ' ', '#', '#', '#', '#', ' ', ' ', ' ', ' ', ' ', '#', '#', '#', '#', ' ', '#'},
            {'#', ' ', ' ', ' ', ' ', '#', ' ', '#', '#', '#', ' ', '#', ' ', ' ', ' ', ' ', '#'},
            {'#', ' ', '#', '#', ' ', ' ', ' ', ' ', '#', ' ', ' ', ' ', ' ', '#', '#', ' ', '#'},
            {'#', ' ', ' ', ' ', ' ', '#', '#', ' ', ' ', ' ', '#', '#', ' ', ' ', ' ', ' ', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
    };

    private boolean isHardLevel; // 标记是否为困难关卡

    private int score = 0; // 分数变量
    private int totalFood = 0; // 食物总数
    private int totalFruit=0;

    private boolean isScared = false; // 标志 PacMan 是否吃下 FrightFruit

    private ArrayList<FrightFruit> frightFruits; // 存储所有 FIGHTFruit

    // 初始化食物
    public Map(boolean isHardLevel) {
        this.isHardLevel = isHardLevel;
        if (this.isHardLevel) {
            // 在地图上放置食物：'!' 为 FIGHTFruit, '*' 为 普通食物
            for (int y = 0; y < map.length; y++) {
                for (int x = 0; x < map[y].length; x++) {
                    if (map[y][x] == ' ' && !(x == 1 && y == 1) && !(x == 15 && y == 15) && !(x == 15 && y == 13) && !(x == 2 && y == 15)) { // 排除出生点
                        if ((y == 2 && x == map[y].length - 2) || (y == map.length - 3 && x == 1)) {
                            map[y][x] = '!'; // FIGHTFruit
                            totalFruit++;
                        } else {
                            map[y][x] = '*'; // 普通食物
                        }
                    }
                }
            }
            increaseFoodCount();//初始化食物数量
        }else{
            // 在地图上放置食物：'!' 为 FIGHTFruit, '*' 为 普通食物
            for (int y = 0; y < map.length; y++) {
                for (int x = 0; x < map[y].length; x++) {
                    if (map[y][x] == ' ' && !(x == 1 && y == 1) && !(x == 15 && y == 15) && !(x == 15 && y == 13) && !(x == 2 && y == 15)) { // 排除出生点
                        if ((y == 2 && x == 1) || (y == 2 && x == map[y].length - 2) ||
                                (y == map.length - 3 && x == 1) || (y == map.length - 3 && x == map[y].length - 2)) {
                            map[y][x] = '!'; // FIGHTFruit
                            totalFruit++;
                        } else {
                            map[y][x] = '*'; // 普通食物
                        }
                    }
                }
            }
            increaseFoodCount();//初始化食物数量
        }
    }

    // 增加食物数量（初始化时调用）
    public void increaseFoodCount() {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == '*' || map[y][x] == '!') {
                    totalFood++;
                }
            }
        }
    }

    // Map类的新增方法：刷新 FIGHTFruit
    public void refreshFrightFruit() {
        System.out.println("水果没了，要刷新一个");
        // 找到所有空白位置
        ArrayList<Object> emptySpaces = new ArrayList<>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == ' ') {
                    emptySpaces.add(new Point(x, y));
                }
            }
        }

        // 如果有空位，随机选择一个位置放置 FIGHTFruit
        if (!emptySpaces.isEmpty()) {
            Point randomSpot = (Point) emptySpaces.get((int) (Math.random() * emptySpaces.size()));
            map[randomSpot.y][randomSpot.x] = '!'; // 放置新的 FIGHTFruit
            totalFruit++; // 更新 FIGHTFruit 总数
            increaseFoodCount(); // 更新总食物数量
        }
    }



    // 获取地图的数据
    public char[][] getMap() {
        return map;
    }

    // 获取当前分数
    public int getScore() {
        return score;
    }

    // 吃掉鬼增加分数
    public void addScore(int points) {
        this.score += points;
    }

    // 获取总食物数量
    public int getTotalFood() {
        return totalFood;
    }

    public int getTotalFruit()
    {
        return totalFruit;
    }

    // 绘制地图
    public void draw(Graphics g) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                // 仅在当前格子是墙体时才绘制
                if (map[y][x] == '#') {
                    if (isScared) {
                        g.setColor(new Color(128, 0, 128)); // 紫色
                    } else {
                        g.setColor(new Color(30, 180, 234)); // 普通墙体颜色
                    }

                    // 判断是否绘制右边框
                    if (x == map[y].length - 1 || map[y][x + 1] != '#') {
                        // 如果当前墙体格子的右侧不是墙体，或者是地图的边界，则绘制右边框
                        g.drawLine(x * 22 + 22, y * 22, x * 22 + 22, y * 22 + 22);
                    }

                    // 判断是否绘制下边框
                    if (y == map.length - 1 || map[y + 1][x] != '#') {
                        // 如果当前墙体格子的下方不是墙体，或者是地图的边界，则绘制下边框
                        g.drawLine(x * 22, y * 22 + 22, x * 22 + 22, y * 22 + 22);
                    }

                    // 绘制上边框（始终绘制）
                    if (y == 0 || map[y - 1][x] != '#') {
                        g.drawLine(x * 22, y * 22, x * 22 + 22, y * 22); // 上边框
                    }

                    // 绘制左边框（始终绘制）
                    if (x == 0 || map[y][x - 1] != '#') {
                        g.drawLine(x * 22, y * 22, x * 22, y * 22 + 22); // 左边框
                    }
                } else if (map[y][x] == '*') {
                    g.setColor(Color.YELLOW);
                    g.fillRect(x * 22 + 9, y * 22 + 9, 2, 2); // 绘制普通食物
                } else if (map[y][x] == '!') {
                    Image frightFruitImage = new ImageIcon(getClass().getResource("/FrightFruit.png")).getImage();
                    g.drawImage(frightFruitImage, x * 22, y * 22, 22, 22, null); // 绘制FrightFruit
                }
            }
        }
    }


    // 清除FrightFruit
    public void clearFrightFruit(int x, int y) {
        if (map[y][x] == '!') {
            map[y][x] = ' ';
            score++;
            totalFood--; // 减少食物总数
            totalFruit--;
            //isScared = true; // 标记 PacMan 吃下了 FrightFruit
            System.out.printf("分数：%d，剩余食物：%d，剩余 FIGHTFruit：%d%n", score, totalFood, totalFruit);
        }
    }

    // 清除普通食物
    public void clearNormalFood(int x, int y) {
        if (map[y][x] == '*') {
            map[y][x] = ' ';
            score++;
            totalFood--; // 减少食物总数
            System.out.printf("分数：%d，剩余食物：%d%n",score,totalFood);
        }
    }

    // 判断是否游戏胜利
    public boolean isGameWon() {
        return totalFood == 0; // 如果所有食物被吃掉，返回true
    }

    // 设置是否处于scared状态
    public void setScared(boolean scared) {
        isScared = scared;
    }

    // 获取是否处于scared状态
    public boolean isScared() {
        return isScared;
    }
}
