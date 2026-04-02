package com.example.winfinal.view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Factory helpers for styled Swing components.
 */
public final class UiUtils {

    private UiUtils() {}
    


    // ── Rounded panel (card) ──────────────────────────────────

    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                        AppTheme.CARD_ARC, AppTheme.CARD_ARC));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(AppTheme.CARD_PADDING));
        return card;
    }

    // ── Stat card (dashboard) ─────────────────────────────────

    public static JPanel createStatCard(String icon, String title, String value, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 14, 14));
                // Left accent bar
                g2.setColor(accentColor);
                g2.fill(new RoundRectangle2D.Double(0, 0, 5, getHeight(), 4, 4));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 4));
        card.setBorder(new EmptyBorder(16, 20, 16, 16));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(AppTheme.FONT_SUBTITLE);
        iconLabel.setForeground(accentColor);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.FONT_SMALL);
        titleLabel.setForeground(AppTheme.TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(AppTheme.TEXT_PRIMARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(valueLabel);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    // ── Styled buttons ────────────────────────────────────────

    public static JButton createPrimaryButton(String text) {
        return styledButton(text, AppTheme.PRIMARY, Color.WHITE);
    }

    public static JButton createSuccessButton(String text) {
        return styledButton(text, new Color(0x10B981), Color.WHITE);
    }

    public static JButton createDangerButton(String text) {
        return styledButton(text, AppTheme.DANGER, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return styledButton(text, new Color(0xE9F5EE), AppTheme.PRIMARY_DARK);
    }

    private static JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(bg.darker());
                } else {
                    g2.setColor(bg);
                }
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(AppTheme.FONT_BODY);
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, AppTheme.BUTTON_HEIGHT));
        return btn;
    }

    // ── Search field ──────────────────────────────────────────

    public static JTextField createSearchField(String placeholder) {
        JTextField field = new JTextField(placeholder) {
            boolean showPlaceholder = true;

            {
                setForeground(AppTheme.TEXT_MUTED);
                addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) {
                        if (showPlaceholder) { setText(""); setForeground(AppTheme.TEXT_PRIMARY); showPlaceholder = false; }
                    }
                    @Override public void focusLost(FocusEvent e) {
                        if (getText().isEmpty()) { setText(placeholder); setForeground(AppTheme.TEXT_MUTED); showPlaceholder = true; }
                    }
                });
            }

            @Override
            public String getText() {
                return showPlaceholder ? "" : super.getText();
            }
        };
        field.setFont(AppTheme.FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                new EmptyBorder(6, 12, 6, 12)));
        field.setPreferredSize(new Dimension(220, AppTheme.BUTTON_HEIGHT));
        return field;
    }

    // ── Section label ─────────────────────────────────────────

    public static JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.FONT_TITLE);
        label.setForeground(AppTheme.TEXT_PRIMARY);
        label.setBorder(new EmptyBorder(0, 0, 8, 0));
        return label;
    }

    // ── Styled JTable ─────────────────────────────────────────

    public static void styleTable(JTable table) {
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(AppTheme.BORDER_LIGHT);
        table.setSelectionBackground(new Color(0xD8F3DC));
        table.setSelectionForeground(AppTheme.TEXT_PRIMARY);
        table.setFillsViewportHeight(true);
        table.setBackground(AppTheme.BG_CARD);
        table.getTableHeader().setFont(AppTheme.FONT_SUBTITLE);
        table.getTableHeader().setBackground(AppTheme.BG_TABLE_HEADER);
        table.getTableHeader().setForeground(AppTheme.TEXT_PRIMARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, AppTheme.PRIMARY));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? AppTheme.BG_CARD : AppTheme.BG_TABLE_ROW_ALT);
                }
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return c;
            }
        });
    }

    // ── Form field row ────────────────────────────────────────

    public static JTextField addFormField(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.FONT_BODY);
        label.setForeground(AppTheme.TEXT_SECONDARY);
        panel.add(label);

        JTextField field = new JTextField();
        field.setFont(AppTheme.FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                new EmptyBorder(5, 10, 5, 10)));
        panel.add(field);
        return field;
    }

    // ── Status badge ──────────────────────────────────────────

    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel badge = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(fg);
        badge.setOpaque(false);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setBorder(new EmptyBorder(3, 10, 3, 10));
        return badge;
    }

    // ── Separator ─────────────────────────────────────────────

    public static JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.BORDER);
        return sep;
    }
}
