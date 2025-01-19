package chy.game;

import javax.swing.*;
import java.awt.*;

public class FrightFruit {
    private int x;
    private int y;

    public FrightFruit(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // 绘制 FrightFruit
    public void draw(Graphics g) {
        Image frightFruitImage = new ImageIcon(getClass().getResource("/FrightFruit.png")).getImage();
        g.drawImage(frightFruitImage, x * 22, y * 22, 22, 22, null); // 绘制FrightFruit
    }
}

