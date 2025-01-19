package chy.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LevelSelection extends JFrame {
    public LevelSelection() {
        // 设置窗口属性
        setTitle("关卡选择");
        setSize(389, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        setLayout(null); // 使用绝对布局

        // 添加第一关按钮
        JButton level1Button = createLevelButton("1", 20, 20);
        getContentPane().add(level1Button);

        // 添加第二关按钮
        JButton level2Button = createLevelButton("2", 20 + 60, 20); // 假设按钮宽度为60像素
        getContentPane().add(level2Button);

        // 按钮事件监听器
        level1Button.addActionListener(e -> {

            startGame("NORMAL"); // 启动普通模式
        });

        level2Button.addActionListener(e -> {
            // 创建并显示第二关游戏界面

            startGame("HARD"); // 启动困难模式
        });

        // 在设置完所有组件后，居中显示窗口
        setLocationRelativeTo(null); // 居中显示窗口
    }

    private JButton createLevelButton(String level, int x, int y) {
        JButton levelButton = new JButton(level);
        levelButton.setForeground(Color.WHITE);
        levelButton.setBackground(Color.BLUE);
        levelButton.setFont(new Font("SansSerif", Font.BOLD, 10));
        levelButton.setFocusPainted(false); // 去除焦点边框
        levelButton.setBounds(x, y, 40, 40); // 设置按钮大小为60x60像素
        return levelButton;
    }

    private void startGame(String level) {
        this.dispose(); // 关闭关卡选择界面
        GamePanel gamePanel = new GamePanel(level); // 创建游戏面板
        JFrame frame = new JFrame("PacMan Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null); // 居中显示
    }
}