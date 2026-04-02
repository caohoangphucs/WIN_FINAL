package com.example.winfinal.view;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Main application window: left sidebar + content area (CardLayout).
 */
public class MainView extends JFrame {

    private static final String PAGE_DASHBOARD = "Bảng điều khiển";
    private static final String PAGE_FARM = "Trang tr\u1EA1i";
    private static final String PAGE_LOT = "L\u00F4 s\u1EA3n xu\u1EA5t";
    private static final String PAGE_SUPPLY = "V\u1EADt t\u01B0";
    private static final String PAGE_HARVEST = "Thu ho\u1EA1ch";
    private static final String PAGE_CULTIVATION = "Canh t\u00E1c";
    private static final String PAGE_PEST = "S\u00E2u b\u1EC7nh";
    private static final String PAGE_REPORT = "B\u00E1o c\u00E1o";
    public static final String PAGE_TRACEABILITY = "Truy xuất nguồn gốc";
    private static final String PAGE_EMPLOYEE = "Nhân viên";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPane = new JPanel(cardLayout);
    private TraceabilityDetailView traceView;

    private JButton activeBtn = null;

    public MainView() {
        setTitle("AgriChain - Agricultural Supply Chain Management");
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
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel logo = new JLabel("AgriChain");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logo.setForeground(Color.WHITE);
        header.add(logo);
        sidebar.add(header);

        sidebar.add(createSidebarSeparator());
        sidebar.add(Box.createVerticalStrut(8));

        // Nav items (no icons)
        String[] navItems = {
                PAGE_DASHBOARD,
                PAGE_FARM,
                PAGE_LOT,
                PAGE_SUPPLY,
                PAGE_HARVEST,
                PAGE_CULTIVATION,
                PAGE_PEST,
                PAGE_REPORT,
                PAGE_TRACEABILITY,
                PAGE_EMPLOYEE,
        };

        for (String label : navItems) {
            JButton btn = createNavButton(label);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
            if (label.equals(PAGE_DASHBOARD)) {
                activeBtn = btn;
                setActive(btn);
            }
        }

        sidebar.add(Box.createVerticalGlue());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        bottom.setOpaque(false);
        bottom.setMaximumSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 50));
        bottom.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel ver = new JLabel("v1.0.0  -  AgriChain");
        ver.setFont(AppTheme.FONT_SMALL);
        ver.setForeground(AppTheme.SIDEBAR_TEXT);
        bottom.add(ver);
        sidebar.add(createSidebarSeparator());
        sidebar.add(bottom);

        return sidebar;
    }

    private JButton createNavButton(String label) {
        JButton btn = new JButton() {
            boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
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

        btn.setText(label);
        btn.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
            }

            @Override
            public int getIconWidth() {
                return 1;
            }

            @Override
            public int getIconHeight() {
                return 1;
            }
        });
        btn.setFont(AppTheme.FONT_SIDEBAR);
        btn.setForeground(AppTheme.SIDEBAR_TEXT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMinimumSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 42));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setIconTextGap(0);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 12));

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
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }

    // ── Content area (CardLayout) ─────────────────────────────

    private JPanel buildContent() {
        contentPane.setBackground(AppTheme.BG_MAIN);
        contentPane.setOpaque(true);

        contentPane.add(new DashboardView(), PAGE_DASHBOARD);
        contentPane.add(new FarmView(), PAGE_FARM);
        contentPane.add(new ProductionLotView(), PAGE_LOT);
        contentPane.add(new AgriSupplyView(), PAGE_SUPPLY);
        contentPane.add(new HarvestRecordView(), PAGE_HARVEST);
        contentPane.add(new CultivationLogView(), PAGE_CULTIVATION);
        contentPane.add(new PestReportView(), PAGE_PEST);
        contentPane.add(new ReportView(), PAGE_REPORT);
        contentPane.add(new EmployeeView(), PAGE_EMPLOYEE);
        traceView = new TraceabilityDetailView();
        contentPane.add(traceView, PAGE_TRACEABILITY);

        return contentPane;
    }

    public void showPage(String pageName) {
        cardLayout.show(contentPane, pageName);
    }

    public TraceabilityDetailView getTraceabilityView() {
        return traceView;
    }
}
