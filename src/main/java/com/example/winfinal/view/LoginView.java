package com.example.winfinal.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginView extends JFrame {

    public LoginView(Runnable onSuccess) {
        setTitle("Đăng nhập - AgriChain");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.BG_MAIN);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(30, 0, 20, 0));
        JLabel lblTitle = new JLabel("AGRICHAIN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(AppTheme.PRIMARY);
        headerPanel.add(lblTitle);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField txtUsername = new JTextField();
        txtUsername.putClientProperty("JTextField.placeholderText", "Tên đăng nhập (admin)");
        txtUsername.setFont(AppTheme.FONT_BODY);
        txtUsername.setPreferredSize(new Dimension(300, 40));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                new EmptyBorder(5, 10, 5, 10)));

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.putClientProperty("JTextField.placeholderText", "Mật khẩu (admin)");
        txtPassword.setFont(AppTheme.FONT_BODY);
        txtPassword.setPreferredSize(new Dimension(300, 40));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                new EmptyBorder(5, 10, 5, 10)));

        gbc.gridy = 0;
        formPanel.add(txtUsername, gbc);
        gbc.gridy = 1;
        formPanel.add(txtPassword, gbc);

        // Bot
        JPanel botPanel = new JPanel();
        botPanel.setOpaque(false);
        botPanel.setBorder(new EmptyBorder(10, 0, 30, 0));

        JButton btnLogin = new JButton("ĐĂNG NHẬP") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(AppTheme.PRIMARY_DARK);
                } else {
                    g2.setColor(AppTheme.PRIMARY);
                }
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(300, 45));
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnLogin.addActionListener(e -> {
            String uname = txtUsername.getText();
            String pwd = new String(txtPassword.getPassword());
            if ("admin".equals(uname) && "admin".equals(pwd)) {
                dispose();
                onSuccess.run();
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Hỗ trợ nhấn Enter
        txtPassword.addActionListener(btnLogin.getActionListeners()[0]);
        txtUsername.addActionListener(btnLogin.getActionListeners()[0]);

        botPanel.add(btnLogin);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(botPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}
