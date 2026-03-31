package com.example.winfinal.view;

import com.example.winfinal.controller.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;

/**
 * Report – matches reference: dark green title bar + 2x2 card grid.
 */
public class ReportView extends JPanel {

    private final HarvestRecordController harvestCtrl = new HarvestRecordController();
    private final SupplyImportController  importCtrl  = new SupplyImportController();
    private final ProductionLotController lotCtrl     = new ProductionLotController();

    public ReportView() {
        setLayout(new BorderLayout(0,0));
        setBackground(AppTheme.BG_MAIN);
        buildAll();
    }

    private void buildAll() {
        removeAll();
        add(buildTopBar(),   BorderLayout.NORTH);
        add(buildGrid(),     BorderLayout.CENTER);
        revalidate(); repaint();
    }

    // ── Top bar (dark green) ──────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(0x2D6A4F));
                g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 50));
        bar.setBorder(new EmptyBorder(0, 18, 0, 18));

        JLabel title = new JLabel("Báo cáo & Phân tích");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        right.setOpaque(false);
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFont(AppTheme.FONT_BODY);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(0x40916C));
        btnRefresh.setBorderPainted(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> buildAll());
        right.add(btnRefresh);

        bar.add(title, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── 2×2 card grid ─────────────────────────────────────────

    private JPanel buildGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.setBackground(AppTheme.BG_MAIN);
        grid.setBorder(new EmptyBorder(14, 14, 14, 14));

        grid.add(buildEfficiencyCard());
        grid.add(buildCustomerCard());
        grid.add(buildFinanceCard());
        grid.add(buildWeatherCard());
        return grid;
    }

    // ── Card 1: Báo cáo Hiệu Quả (grouped bar) ───────────────

    private JPanel buildEfficiencyCard() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout(0,8));

        JLabel title = new JLabel("Báo cáo Hiệu Quả");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        card.add(title, BorderLayout.NORTH);
        card.add(new GroupedBarChart(), BorderLayout.CENTER);
        return card;
    }

    // ── Card 2: Báo cáo Khách Hàng ───────────────────────────

    private JPanel buildCustomerCard() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout(0,8));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Báo cáo khách hàng");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        JButton btnDetail = new JButton("Xem chi tiết");
        btnDetail.setFont(AppTheme.FONT_SMALL);
        btnDetail.setForeground(new Color(0x2D6A4F));
        btnDetail.setBorder(BorderFactory.createLineBorder(new Color(0x2D6A4F), 1, true));
        btnDetail.setContentAreaFilled(false);
        btnDetail.setFocusPainted(false);
        btnDetail.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        header.add(title, BorderLayout.WEST);
        header.add(btnDetail, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        String[] cols = {"Khách hàng","Số lượng mua","Tổng chi tiêu"};
        Object[][] data = {
            {"  Nguyễn Thị Lan",   "400 tấn", "1,2 triệu VND"},
            {"  Công ty GreenFarm", "350 tấn", "1 triệu VND"},
            {"  Đại lý Minh Hoàng","300 tấn", "700 triệu VND"},
            {"  Trần Văn Bình",    "250 tấn", "650 triệu VND"},
        };
        JTable t = buildStyledTable(cols, data, false);
        JScrollPane sc = new JScrollPane(t);
        sc.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        sc.getViewport().setBackground(Color.WHITE);
        card.add(sc, BorderLayout.CENTER);
        return card;
    }

    // ── Card 3: Báo cáo Tài Chính ────────────────────────────

    private JPanel buildFinanceCard() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout(0,8));

        JLabel title = new JLabel("Báo cáo Tài chính (Ước tính)");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Tổng chi phí vật tư đầu tư cho từng lô để tiết tính lợi nhuận");
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel headerPanel = new JPanel(new BorderLayout(0,2));
        headerPanel.setOpaque(false);
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(sub, BorderLayout.CENTER);
        card.add(headerPanel, BorderLayout.NORTH);

        String[] cols   = {"Lô","Chi phí vật tư","Doanh thu","Lợi nhuận ước"};
        Object[][] rows = null;
        try {
            List<Object[]> data = lotCtrl.getTopYieldingLots(5);
            if (data != null && !data.isEmpty()) {
                rows = new Object[data.size()][4];
                for (int i=0; i<data.size(); i++) {
                    Object[] r = data.get(i);
                    rows[i][0] = r[0]==null ? "" : r[0].toString();
                    rows[i][1] = "N/A";
                    rows[i][2] = r[1]==null ? "0" : String.format("%.0f kg", ((Number)r[1]).doubleValue());
                    rows[i][3] = "N/A";
                }
            }
        } catch (Exception ignored) {}

        if (rows == null) {
            rows = new Object[][]{
                {"Lô 12", "15 triệu", "90 triệu",  "75 triệu"},
                {"Lô 5",  "25 triệu", "105 triệu", "80 triệu"},
                {"Lô 8",  "20 triệu", "60 triệu",  "40 triệu"},
                {"Lô 23", "18 triệu", "70 triệu",  "52 triệu"},
            };
        }

        JTable t = buildStyledTable(cols, rows, true);
        JScrollPane sc = new JScrollPane(t);
        sc.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        sc.getViewport().setBackground(Color.WHITE);

        JButton btnExport = new JButton("Xuất báo cáo") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x1B4332) : new Color(0x2D6A4F));
                g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),8,8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExport.setForeground(Color.WHITE);
        btnExport.setContentAreaFilled(false);
        btnExport.setBorderPainted(false);
        btnExport.setFocusPainted(false);
        btnExport.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExport.setPreferredSize(new Dimension(160, 36));
        btnExport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Đã xuất báo cáo (demo)."));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        btnRow.setOpaque(false);
        btnRow.add(btnExport);

        JPanel center = new JPanel(new BorderLayout(0,6));
        center.setOpaque(false);
        center.add(sc, BorderLayout.CENTER);
        center.add(btnRow, BorderLayout.SOUTH);
        card.add(center, BorderLayout.CENTER);
        return card;
    }

    // ── Card 4: Báo cáo Thời Tiết ────────────────────────────

    private JPanel buildWeatherCard() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout(0,8));

        JLabel title = new JLabel("Báo cáo Thời tiết");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        card.add(title, BorderLayout.NORTH);
        card.add(new WeatherBarChart(), BorderLayout.CENTER);
        return card;
    }

    // ── Helpers ───────────────────────────────────────────────

    private JPanel makeCard() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),12,12));
                g2.setColor(AppTheme.BORDER_LIGHT);
                g2.draw(new RoundRectangle2D.Double(0,0,getWidth()-1,getHeight()-1,12,12));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 16, 14, 16));
        return p;
    }

    private JTable buildStyledTable(String[] cols, Object[][] rows, boolean numbered) {
        DefaultTableModel m = new DefaultTableModel(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(m);
        t.setFont(AppTheme.FONT_BODY);
        t.setRowHeight(32);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(new Color(0xF3F4F6));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBackground(new Color(0xF9FAFB));
        t.getTableHeader().setForeground(AppTheme.TEXT_SECONDARY);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tb, val, sel, foc, row, col);
                if (!sel) c.setBackground(row%2==0 ? Color.WHITE : new Color(0xF9FAFB));
                ((JLabel)c).setBorder(new EmptyBorder(0,10,0,10));
                return c;
            }
        });
        return t;
    }

    // ══════════════════════════════════════════════════════════
    // Inner chart panels
    // ══════════════════════════════════════════════════════════

    /** Grouped vertical bars (green=revenue, orange=volume) */
    static class GroupedBarChart extends JPanel {
        static final String[] FARMS = {"Lập nền Farm","Hưng Thịnh Farm","Bình An Farm"};
        static final int[] REV  = {2300, 1270, 1220}; // triệu VND (×10^6)
        static final int[] VOL  = {900,  800,  600};  // tấn
        static final Color GREEN  = new Color(0x2D6A4F);
        static final Color ORANGE = new Color(0xE88C2A);

        GroupedBarChart() { setOpaque(false); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padL=50, padR=10, padT=24, padB=40;
            int W = getWidth()-padL-padR;
            int H = getHeight()-padT-padB;
            int n = FARMS.length;
            int maxRev = 2500, maxVol = 1000;
            int groupW = W/n;
            int barW = (groupW-16)/2;

            // Grid + Y labels (left = VND)
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            for (int i=0; i<=5; i++) {
                int y = padT + H*i/5;
                g2.setColor(new Color(0xEEEEEE)); g2.drawLine(padL, y, padL+W, y);
                g2.setColor(AppTheme.TEXT_SECONDARY);
                int vnd = (5-i)*maxRev/5;
                g2.drawString(vnd/1000+"b", 2, y+4);
            }
            // Y right label (volume)
            g2.setColor(AppTheme.TEXT_SECONDARY);
            g2.drawString("Sản lượng", padL+W+2, padT-4);

            // Legend
            drawLegendDot(g2, padL+10, padT-12, GREEN,  "Doanh thu");
            drawLegendDot(g2, padL+90, padT-12, ORANGE, "Sản lượng");

            for (int i=0; i<n; i++) {
                int gx = padL + i*groupW + 8;
                // Green bar (revenue)
                int revH = (int)((double)REV[i]/maxRev*H);
                int rx = gx;
                int ry = padT + H - revH;
                g2.setColor(GREEN);
                g2.fill(new RoundRectangle2D.Double(rx, ry, barW, revH, 4, 4));

                // Label above
                g2.setColor(AppTheme.TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                String rl = (REV[i]/100f)+" tỷ";
                g2.drawString(rl, rx+(barW-g2.getFontMetrics().stringWidth(rl))/2, ry-3);

                // Orange bar (volume) – scaled to right axis
                int volH = (int)((double)VOL[i]/maxVol*H);
                int ox = gx + barW + 2;
                int oy = padT + H - volH;
                g2.setColor(ORANGE);
                g2.fill(new RoundRectangle2D.Double(ox, oy, barW, volH, 4, 4));

                g2.setColor(AppTheme.TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                String ol = VOL[i]+" tấn";
                g2.drawString(ol, ox+(barW-g2.getFontMetrics().stringWidth(ol))/2, oy-3);

                // Farm name
                g2.setColor(AppTheme.TEXT_SECONDARY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                String farm = FARMS[i];
                int fw = g2.getFontMetrics().stringWidth(farm);
                g2.drawString(farm, padL + i*groupW + groupW/2 - fw/2, padT+H+14);
            }
            g2.dispose();
        }

        private void drawLegendDot(Graphics2D g2, int x, int y, Color c, String label) {
            g2.setColor(c);
            g2.fillRect(x, y, 14, 8);
            g2.setColor(AppTheme.TEXT_SECONDARY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.drawString(label, x+18, y+8);
        }
    }

    /** Bar + line weather chart */
    static class WeatherBarChart extends JPanel {
        static final String[] MONTHS = {"Tháng 1","Tháng 2","Tháng 3","Tháng 4","Tháng 6"};
        static final int[] RAIN = {13, 13, 16, 17, 20}; // ngày mưa
        static final int[] YIELD= {0,  0,  0,   0,  100}; // sản lượng (%)

        WeatherBarChart() { setOpaque(false); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padL=36,padR=50,padT=26,padB=36;
            int W=getWidth()-padL-padR, H=getHeight()-padT-padB;
            int n=MONTHS.length;
            int maxR=25, maxY=120;
            int barW = Math.max(8, W/n - 14);

            // Grid
            for (int i=0;i<=5;i++) {
                int y=padT+H*i/5;
                g2.setColor(new Color(0xEEEEEE)); g2.drawLine(padL,y,padL+W,y);
                g2.setColor(AppTheme.TEXT_SECONDARY);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,9));
                g2.drawString(String.valueOf((5-i)*maxR/5), 2, y+4);
            }

            // Right Y axis
            g2.setColor(AppTheme.TEXT_SECONDARY);
            g2.setFont(new Font("Segoe UI",Font.PLAIN,9));
            for (int i=0;i<=5;i++) {
                int y=padT+H*i/5;
                g2.drawString(String.valueOf((5-i)*maxY/5), padL+W+4, y+4);
            }
            g2.drawString("3 triệu VND", padL+W+4, padT-4);

            // Bars (rain days)
            int[] xs=new int[n], ys=new int[n];
            for (int i=0;i<n;i++) {
                int x = padL + i*(W/(n-1)) - barW/2;
                int bh= (int)((double)RAIN[i]/maxR*H);
                int by= padT+H-bh;
                g2.setColor(new Color(0x4895EF));
                g2.fill(new RoundRectangle2D.Double(x,by,barW,bh,4,4));
                xs[i] = padL + i*(W/(n-1));
                ys[i] = padT + H - (int)((double)YIELD[i]/maxY*H);
            }

            // Green line (yield)
            g2.setColor(new Color(0x40916C));
            g2.setStroke(new BasicStroke(2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            for (int i=0;i<n-1;i++) g2.drawLine(xs[i],ys[i],xs[i+1],ys[i+1]);
            for (int i=0;i<n;i++) {
                g2.setColor(Color.WHITE); g2.fillOval(xs[i]-4,ys[i]-4,8,8);
                g2.setColor(new Color(0x40916C)); g2.drawOval(xs[i]-4,ys[i]-4,8,8);
                // value
                if (YIELD[i]>0) {
                    g2.setFont(new Font("Segoe UI",Font.BOLD,10));
                    g2.drawString(YIELD[i]+"", xs[i]+6, ys[i]-4);
                }
            }

            // X labels
            g2.setColor(AppTheme.TEXT_SECONDARY);
            g2.setFont(new Font("Segoe UI",Font.PLAIN,9));
            for (int i=0;i<n;i++) {
                int lw=g2.getFontMetrics().stringWidth(MONTHS[i]);
                g2.drawString(MONTHS[i], xs[i]-lw/2, padT+H+16);
            }

            // Legend
            drawRect(g2, padL+5, padT-16, new Color(0x4895EF)); g2.setFont(new Font("Segoe UI",Font.PLAIN,9)); g2.setColor(AppTheme.TEXT_SECONDARY); g2.drawString("Ngày mưa", padL+22, padT-8);
            drawRect(g2, padL+82,padT-16, new Color(0x40916C)); g2.drawString("Sản lượng", padL+99, padT-8);

            g2.dispose();
        }

        private void drawRect(Graphics2D g2, int x, int y, Color c) {
            g2.setColor(c); g2.fillRect(x, y, 12, 8);
        }
    }
}
