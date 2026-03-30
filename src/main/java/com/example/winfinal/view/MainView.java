package com.example.winfinal.view;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {

    public MainView() {
        setTitle("AgriChain - Welcome");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel welcomeLabel = new JLabel("Ok chạy ngon rồi giờ code UI đi cu");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel subLabel = new JLabel("Agricultural Supply Chain Management");
        subLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(welcomeLabel, gbc);

        gbc.gridy = 1;
        panel.add(subLabel, gbc);

        add(panel);
    }
}
