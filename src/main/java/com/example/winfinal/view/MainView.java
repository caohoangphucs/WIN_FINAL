package com.example.winfinal.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Main application window: left sidebar + content area (CardLayout).
 * Replaces the old 400x200 placeholder window.
 */
public class MainView extends JFrame {

    private static final String PAGE_DASHBOARD   = "Dashboard";
    private static final String PAGE_FARM        = "Trang trại";
    private static final String PAGE_LOT         = "Lô sản xuất";
    private static final String PAGE_SUPPLY      = "Vật tư";
    private static final String PAGE_HARVEST     = "Thu hoạch";
    private static final String PAGE_CULTIVATION = "Canh tác";
    private static final String PAGE_PEST        = "Sâu bệnh";
    private static final String PAGE_TRACEABILITY= "Truy xuất";
    private static final String PAGE_REPORT      = "Báo cáo";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPane = new JPanel(cardLayout);

    private JButton activeBtn = null;

    public MainView() {
        setTitle("AgriChain – Agricultural Supply Chain Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 780);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(AppTheme.BG_MAIN);

        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
    }

    // ── Sidebar ───────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);

        // Logo header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 18));
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 70));
        JLabel logo = new JLabel("🌿 AgriChain");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logo.setForeground(Color.WHITE);
        header.add(logo);
        sidebar.add(header);

        // Divider
        sidebar.add(createSidebarSeparator());
        sidebar.add(Box.createVerticalStrut(8));

        // Nav items
        String[][] navItems = {
            {"📊", PAGE_DASHBOARD},
            {"🏡", PAGE_FARM},
            {"🌾", PAGE_LOT},
            {"📦", PAGE_SUPPLY},
            {"🌽", PAGE_HARVEST},
            {"📋", PAGE_CULTIVATION},
            {"🐛", PAGE_PEST},
            {"🔍", PAGE_TRACEABILITY},
            {"📈", PAGE_REPORT},
        };

        for (String[] item : navItems) {
            JButton btn = createNavButton(item[0], item[1]);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
            if (item[1].equals(PAGE_DASHBOARD)) {
                activeBtn = btn;
                setActive(btn);
            }
        }

        sidebar.add(Box.createVerticalGlue());

        // Bottom info
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        bottom.setOpaque(false);
        bottom.setMaximumSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 50));
        JLabel ver = new JLabel("v1.0.0  •  AgriChain");
        ver.setFont(AppTheme.FONT_SMALL);
        ver.setForeground(AppTheme.SIDEBAR_TEXT);
        bottom.add(ver);
        sidebar.add(createSidebarSeparator());
        sidebar.add(bottom);

        return sidebar;
    }

    private JButton createNavButton(String icon, String label) {
        JButton btn = new JButton() {
            boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean active = (this == activeBtn);
                if (active) {
                    g2.setColor(AppTheme.SIDEBAR_ITEM_ACTIVE);
                    g2.fill(new RoundRectangle2D.Double(8, 2, getWidth() - 16, getHeight() - 4, 10, 10));
                } else if (hovered) {
                    g2.setColor(AppTheme.SIDEBAR_ITEM_HOVER);
                    g2.fill(new RoundRectangle2D.Double(8, 2, getWidth() - 16, getHeight() - 4, 10, 10));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setText(icon + "  " + label);
        btn.setFont(AppTheme.FONT_SIDEBAR);
        btn.setForeground(AppTheme.SIDEBAR_TEXT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 42));
        btn.setPreferredSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 42));
        btn.setBorder(new EmptyBorder(0, 12, 0, 12));

        btn.addActionListener(e -> {
            setActive(btn);
            cardLayout.show(contentPane, label);
        });

        return btn;
    }

    private void setActive(JButton btn) {
        if (activeBtn != null) {
            activeBtn.setFont(AppTheme.FONT_SIDEBAR);
            activeBtn.setForeground(AppTheme.SIDEBAR_TEXT);
        }
        activeBtn = btn;
        btn.setFont(AppTheme.FONT_SIDEBAR_ACTIVE);
        btn.setForeground(AppTheme.SIDEBAR_TEXT_ACTIVE);
        btn.repaint();
    }

    private JSeparator createSidebarSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x2D6A4F));
        sep.setMaximumSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 1));
        return sep;
    }

    // ── Content area (CardLayout) ─────────────────────────────

    private JPanel buildContent() {
        contentPane.setBackground(AppTheme.BG_MAIN);
        contentPane.setOpaque(true);

        contentPane.add(new DashboardView(), PAGE_DASHBOARD);
        contentPane.add(new FarmView(),      PAGE_FARM);
        contentPane.add(new ProductionLotView(), PAGE_LOT);
        contentPane.add(new AgriSupplyView(), PAGE_SUPPLY);
        contentPane.add(new HarvestRecordView(), PAGE_HARVEST);
        contentPane.add(new CultivationLogView(), PAGE_CULTIVATION);
        contentPane.add(new PestReportView(), PAGE_PEST);
        contentPane.add(new TraceabilityView(), PAGE_TRACEABILITY);
        contentPane.add(new ReportView(), PAGE_REPORT);

        return contentPane;
    }
}
