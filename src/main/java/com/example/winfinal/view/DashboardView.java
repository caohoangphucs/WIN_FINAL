package com.example.winfinal.view;

import com.example.winfinal.controller.*;
import com.example.winfinal.dto.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * Dashboard – matches reference: 3 chart cards + alert table below.
 */
public class DashboardView extends JPanel {

    private final AgriSupplyController    supplyCtrl = new AgriSupplyController();
    private final ProductionLotController lotCtrl    = new ProductionLotController();
    private final PestReportController    pestCtrl   = new PestReportController();

    private DefaultTableModel alertModel;

    public DashboardView() {
        setLayout(new BorderLayout(0, 0));
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);

        refresh();
    }

    // ── Top header bar ────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel date = new JLabel(new java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        date.setFont(AppTheme.FONT_BODY);
        date.setForeground(AppTheme.TEXT_SECONDARY);

        JButton btnRefresh = UiUtils.createSecondaryButton("Làm mới");
        btnRefresh.addActionListener(e -> refresh());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(date);
        right.add(btnRefresh);

        p.add(title, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ── Body ──────────────────────────────────────────────────

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.add(buildChartRow(), BorderLayout.NORTH);
        body.add(buildAlertTable(), BorderLayout.CENTER);
        return body;
    }

    // ── 3 Chart cards ─────────────────────────────────────────

    private JPanel buildChartRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 14, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 280));

        row.add(buildYieldBarCard());
        row.add(buildQualityPieCard());
        row.add(buildSeasonLineCard());
        return row;
    }

    private JPanel buildYieldBarCard() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout(0, 8));

        JLabel title = new JLabel("Năng suất theo loại cây trồng");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        card.add(title, BorderLayout.NORTH);

        String[] labels = {"Rau", "Lúa", "Trái Cây", "Hoa"};
        int[]    values = {8, 12, 10, 6};
        Color[]  colors = {new Color(0x52B788), new Color(0xFFB833), new Color(0x9B72CF), new Color(0x4895EF)};
        card.add(new BarChartPanel(labels, values, colors, "Tấn"), BorderLayout.CENTER);
        return card;
    }

    private JPanel buildQualityPieCard() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout(0, 8));

        JLabel title = new JLabel("Tỉ lệ xếp loại chất lượng A/B/C");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        card.add(title, BorderLayout.NORTH);

        String[] labels = {"A", "B", "C"};
        double[] values = {50, 30, 20};
        Color[]  colors = {new Color(0x52B788), new Color(0xFFB833), new Color(0xE63946)};
        card.add(new PieChartPanel(labels, values, colors), BorderLayout.CENTER);
        return card;
    }

    private JPanel buildSeasonLineCard() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout(0, 8));

        JLabel title = new JLabel("Sản lượng qua các mùa vụ");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        card.add(title, BorderLayout.NORTH);

        String[] labels = {"Xuân", "Hạ", "Thu", "Đông"};
        int[]    values = {4, 5, 14, 8};
        card.add(new LineChartPanel(labels, values, "Tấn"), BorderLayout.CENTER);
        return card;
    }

    // ── Alert table ───────────────────────────────────────────

    private JPanel buildAlertTable() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel("Cảnh báo cần chú ý");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        card.add(title, BorderLayout.NORTH);

        String[] cols = {"Loại", "Lô / Vật tư", "Vật tư", "Trạng thái", "Ngày"};
        alertModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(alertModel);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(AppTheme.BORDER_LIGHT);
        table.setBackground(Color.WHITE);
        table.getTableHeader().setFont(AppTheme.FONT_SUBTITLE);
        table.getTableHeader().setBackground(AppTheme.BG_TABLE_HEADER);
        table.getTableHeader().setForeground(AppTheme.TEXT_PRIMARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,2,0, AppTheme.PRIMARY));
        table.setDefaultRenderer(Object.class, new AlertTableRenderer());

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ── Data refresh ──────────────────────────────────────────

    private void refresh() {
        SwingWorker<Void, Void> w = new SwingWorker<>() {
            final List<Object[]> rows = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                String today = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date());
                try {
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DATE, 14);
                    List<ProductionLotDTO> harvest = lotCtrl.getUpcomingHarvest(c.getTime());
                    for (ProductionLotDTO l : harvest) {
                        rows.add(new Object[]{"LOT", l.getLotCode(), "", "Sắp thu hoạch", today});
                    }
                } catch (Exception ignored) {}

                try {
                    List<AgriSupplyDTO> low = supplyCtrl.getLowStockSupplies();
                    for (AgriSupplyDTO s : low) {
                        rows.add(new Object[]{"VT", s.getName(), s.getName(),
                                "Tồn kho " + (s.getStockQty() != null && s.getStockQty() == 0
                                        ? "sắp hết" : "thấp"), today});
                    }
                } catch (Exception ignored) {}
                return null;
            }

            @Override
            protected void done() {
                alertModel.setRowCount(0);
                if (rows.isEmpty()) {
                    alertModel.addRow(new Object[]{"", "Không có cảnh báo", "", "", ""});
                } else {
                    rows.forEach(r -> alertModel.addRow(r));
                }
            }
        };
        w.execute();
    }

    // ── Helpers ───────────────────────────────────────────────

    private JPanel makeCard() {
        JPanel c = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),12,12));
                g2.setColor(AppTheme.BORDER_LIGHT);
                g2.draw(new RoundRectangle2D.Double(0,0,getWidth()-1,getHeight()-1,12,12));
                g2.dispose();
            }
        };
        c.setOpaque(false);
        c.setBorder(new EmptyBorder(16, 16, 16, 16));
        return c;
    }

    // ── Alert table renderer ──────────────────────────────────

    static class AlertTableRenderer extends DefaultTableCellRenderer {
        // warm yellow for harvest, light red for supply
        static final Color HARVEST_BG = new Color(0xFEF9C3);
        static final Color SUPPLY_BG  = new Color(0xFEE2E2);
        static final Color HARVEST_ICON_BG = new Color(0xFDE68A);
        static final Color SUPPLY_ICON_BG  = new Color(0xFCA5A5);

        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            Object type = t.getModel().getValueAt(row, 0);
            boolean isSupply = "VT".equals(type);
            boolean isLot    = "LOT".equals(type);

            if (!sel) {
                lbl.setBackground(isSupply ? SUPPLY_BG : isLot ? HARVEST_BG : Color.WHITE);
            }

            if (col == 0) {
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if (isSupply) {
                    lbl.setText("!");
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    lbl.setForeground(AppTheme.DANGER);
                } else if (isLot) {
                    lbl.setText("!");
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    lbl.setForeground(new Color(0xB45309));
                }
            } else {
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setFont(AppTheme.FONT_BODY);
                if (col == 3 && isSupply) lbl.setForeground(AppTheme.DANGER);
                else if (col == 3 && isLot) lbl.setForeground(new Color(0x92400E));
                else lbl.setForeground(AppTheme.TEXT_PRIMARY);
            }
            lbl.setBorder(new EmptyBorder(0, 10, 0, 10));
            return lbl;
        }
    }

    // ══════════════════════════════════════════════════════════
    // Inner chart panels
    // ══════════════════════════════════════════════════════════

    /** Vertical bar chart */
    static class BarChartPanel extends JPanel {
        private final String[] labels;
        private final int[]    values;
        private final Color[]  colors;
        private final String   unit;

        BarChartPanel(String[] l, int[] v, Color[] c, String unit) {
            this.labels = l; this.values = v; this.colors = c; this.unit = unit;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padL=40, padR=10, padT=20, padB=30;
            int W = getWidth()-padL-padR;
            int H = getHeight()-padT-padB;
            int maxV = Arrays.stream(values).max().orElse(1);
            int n = values.length;
            int step = Math.max(1, maxV/5);
            int barGap = 14;
            int barW = Math.max(8, W/n - barGap);

            // Y-axis gridlines + labels
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(0xE5E7EB));
            for (int i=0; i<=5; i++) {
                int v = step*i;
                int y = padT + H - (int)((double)v/maxV*H);
                g2.drawLine(padL, y, padL+W, y);
                g2.setColor(AppTheme.TEXT_SECONDARY);
                g2.drawString(String.valueOf(v), 2, y+4);
                g2.setColor(new Color(0xE5E7EB));
            }

            // Unit label
            g2.setColor(AppTheme.TEXT_SECONDARY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.drawString(unit, 2, padT-2);

            // Bars
            for (int i=0; i<n; i++) {
                int barH = (int)((double)values[i]/maxV*H);
                int x = padL + i*(barW+barGap);
                int y = padT + H - barH;
                g2.setColor(colors[i%colors.length]);
                g2.fill(new RoundRectangle2D.Double(x, y, barW, barH, 6, 6));

                // value label above bar
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(AppTheme.TEXT_PRIMARY);
                String vs = String.valueOf(values[i]);
                int sw = g2.getFontMetrics().stringWidth(vs);
                g2.drawString(vs, x+(barW-sw)/2, y-4);

                // x-axis label
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(AppTheme.TEXT_SECONDARY);
                int lw = g2.getFontMetrics().stringWidth(labels[i]);
                g2.drawString(labels[i], x+(barW-lw)/2, padT+H+16);
            }
            g2.dispose();
        }
    }

    /** Pie / donut chart */
    static class PieChartPanel extends JPanel {
        private final String[] labels;
        private final double[] values;
        private final Color[]  colors;

        PieChartPanel(String[] l, double[] v, Color[] c) {
            this.labels=l; this.values=v; this.colors=c;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()-30) - 20;
            size = Math.max(size, 40);
            int cx = (getWidth()-size)/2, cy = 10;
            double total = Arrays.stream(values).sum();
            if (total==0) { g2.dispose(); return; }

            double start = -90;
            for (int i=0; i<values.length; i++) {
                double sweep = values[i]/total*360;
                g2.setColor(colors[i%colors.length]);
                g2.fill(new Arc2D.Double(cx, cy, size, size, start, sweep, Arc2D.PIE));

                // sector label
                double mid = Math.toRadians(start + sweep/2);
                int lx = cx+size/2 + (int)((size/2-size/5)*Math.cos(mid));
                int ly = cy+size/2 + (int)((size/2-size/5)*Math.sin(mid));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String pct = (int)values[i]+"%";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(pct, lx-fm.stringWidth(pct)/2, ly+4);

                start += sweep;
            }

            // legend
            int legY = cy+size+10;
            int legX = 10;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            for (int i=0; i<labels.length; i++) {
                g2.setColor(colors[i%colors.length]);
                g2.fillOval(legX, legY, 10, 10);
                g2.setColor(AppTheme.TEXT_SECONDARY);
                g2.drawString(labels[i]+" "+(int)values[i]+"%", legX+14, legY+9);
                legX += 70;
            }
            g2.dispose();
        }
    }

    /** Line / area chart */
    static class LineChartPanel extends JPanel {
        private final String[] labels;
        private final int[]    values;
        private final String   unit;

        LineChartPanel(String[] l, int[] v, String unit) {
            this.labels=l; this.values=v; this.unit=unit;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padL=40, padR=10, padT=20, padB=30;
            int W = getWidth()-padL-padR;
            int H = getHeight()-padT-padB;
            int maxV = Arrays.stream(values).max().orElse(1);
            int n = values.length;
            if (n<2) { g2.dispose(); return; }

            int[] xs = new int[n], ys = new int[n];
            for (int i=0; i<n; i++) {
                xs[i] = padL + i*W/(n-1);
                ys[i] = padT + H - (int)((double)values[i]/maxV*H);
            }

            // gridlines
            g2.setColor(new Color(0xE5E7EB));
            for (int i=0; i<=4; i++) {
                int y = padT + H*i/4;
                g2.drawLine(padL, y, padL+W, y);
            }

            // area fill
            int[] fillX = new int[n+2], fillY = new int[n+2];
            System.arraycopy(xs, 0, fillX, 0, n);
            System.arraycopy(ys, 0, fillY, 0, n);
            fillX[n]=xs[n-1]; fillY[n]=padT+H;
            fillX[n+1]=xs[0]; fillY[n+1]=padT+H;
            g2.setColor(new Color(0x4895EF, false) {
                { // alpha 60
                }
            });
            Color areaColor = new Color(0x4895EF);
            g2.setColor(new Color(areaColor.getRed(), areaColor.getGreen(), areaColor.getBlue(), 50));
            g2.fillPolygon(fillX, fillY, n+2);

            // line
            g2.setColor(new Color(0x4895EF));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i=0; i<n-1; i++) g2.drawLine(xs[i], ys[i], xs[i+1], ys[i+1]);

            // dots + labels
            for (int i=0; i<n; i++) {
                g2.setColor(Color.WHITE);
                g2.fillOval(xs[i]-5, ys[i]-5, 10, 10);
                g2.setColor(new Color(0x4895EF));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(xs[i]-5, ys[i]-5, 10, 10);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(AppTheme.TEXT_SECONDARY);
                String lbl = labels[i];
                g2.drawString(lbl, xs[i] - g2.getFontMetrics().stringWidth(lbl)/2, padT+H+16);
            }

            g2.setColor(AppTheme.TEXT_SECONDARY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.drawString(unit, 2, padT-2);
            g2.dispose();
        }
    }
}
