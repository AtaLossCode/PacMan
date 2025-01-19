package chy.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game {

    public static void main(String[] args) {
        // 创建主界面
        JFrame mainFrame = new JFrame("PacMan");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(389, 450);
        mainFrame.setLayout(new GridBagLayout()); // 使用GridBagLayout以支持居中
        mainFrame.getContentPane().setBackground(Color.BLACK); // 设置背景为黑色

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // gridx 0：第1列
        gbc.gridy = 0; // gridy 0：第1行
        gbc.insets = new Insets(10, 0, 10, 0); // 设置外边距
        gbc.anchor = GridBagConstraints.CENTER; // 居中

        // 添加标题
        JLabel titleLabel = new JLabel("PacMan");
        titleLabel.setForeground(Color.BLUE); // 设置字体颜色为白色
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36)); // 设置字体为加粗加大
        gbc.gridy = 0;
        mainFrame.add(titleLabel, gbc);

        // 添加开始游戏按钮
        JButton startGameButton = new JButton("开始游戏");
        startGameButton.setForeground(Color.BLACK); // 设置字体颜色为白色
        startGameButton.setFont(new Font("SansSerif", Font.PLAIN, 18)); // 设置字体大小
        gbc.gridy = 1;
        mainFrame.add(startGameButton, gbc);

        // 添加规则按钮
        JButton rulesButton = new JButton("游戏规则");
        rulesButton.setForeground(Color.BLACK); // 设置字体颜色为白色
        rulesButton.setFont(new Font("SansSerif", Font.PLAIN, 18)); // 设置字体大小
        gbc.gridy = 2;
        mainFrame.add(rulesButton, gbc);

        // 规则界面
        JFrame rulesFrame = new JFrame("游戏规则");
        rulesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        rulesFrame.setSize(389, 450);
        rulesFrame.setLayout(new BoxLayout(rulesFrame.getContentPane(), BoxLayout.Y_AXIS));
        rulesFrame.getContentPane().setBackground(Color.BLACK);

        // 添加规则文本
        JTextArea rulesText = new JTextArea("游戏规则：\n" +
                "1、请使用方向键移动，每吃掉一个食物，则得分+1，您的目标是消灭所有Ghost。\n2、您一共有三条生命，在正常情况下，Ghost为红色，若您在这个时候碰到他们，则生命值-1。若生命归零，则游戏结束。\n" +
                "3、FrightFruit会让您进入强化状态，在3s内您可以吃掉鬼，并增加20分。\n" );
        rulesText.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        rulesText.setEditable(false);
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        rulesText.setForeground(Color.BLACK); // 设置字体颜色为白色
        rulesText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 添加内边距
        rulesFrame.add(rulesText);

        // 添加返回按钮
        JButton backButton = new JButton("返回");
        backButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        backButton.setForeground(Color.BLACK); // 设置字体颜色为白色
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 18)); // 设置字体大小
        rulesFrame.add(backButton);

        // 按钮事件监听器
        startGameButton.addActionListener(e -> {
            LevelSelection levelSelection = new LevelSelection();
            levelSelection.setVisible(true);
            mainFrame.setVisible(false); // 隐藏主界面
        });

        rulesButton.addActionListener(e -> {
            rulesFrame.setVisible(true); // 显示规则界面
        });

        backButton.addActionListener(e -> {
            rulesFrame.dispose(); // 关闭规则界面
            mainFrame.setVisible(true); // 显示主界面
        });

        // 居中显示主界面
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        // 居中显示规则界面
        rulesFrame.setLocationRelativeTo(null);
    }
}