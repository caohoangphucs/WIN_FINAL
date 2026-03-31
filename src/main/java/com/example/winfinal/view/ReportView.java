package com.example.winfinal.view;

import com.example.winfinal.controller.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

/**
 * Report screen: statistical summaries using custom-painted charts.
 */
public class ReportView extends JPanel {

    private final HarvestRecordController harvestCtrl     = new HarvestRecordController();
    private final SupplyImportController  importCtrl      = new SupplyImportController();
    private final ProductionLotController lotCtrl         = new ProductionLotController();

    public ReportView() {
        setLayout(new BorderLayout(0, 0));
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCharts(),  BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 20, 0));
        p.add(UiUtils.createSectionTitle("📈  Báo Cáo & Thống Kê"), BorderLayout.WEST);

        JButton btnRefresh = UiUtils.createSecondaryButton("↻  Làm mới");
        btnRefresh.addActionListener(e -> {
            removeAll();
            add(buildHeader(), BorderLayout.NORTH);
            add(buildCharts(), BorderLayout.CENTER);
            revalidate();
            repaint();
        });
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(btnRefresh);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JPanel buildCharts() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);

        grid.add(buildYieldByCropChart());
        grid.add(buildQualityGradeChart());
        grid.add(buildTopLotsTable());
        grid.add(buildCostBySupplierTable());

        return grid;
    }

    // ── Chart 1: Yield by Crop Type (horizontal bar) ─────────

    private JPanel buildYieldByCropChart() {
        JPanel card = createChartCard("🌾  Năng suất trung bình theo loại cây");
        List<Object[]> data = null;
        try { data = harvestCtrl.getAvgYieldByCropType(); } catch (Exception ignored) {}

        if (data == null || data.isEmpty()) {
            card.add(buildNoDataLabel());
        } else {
            card.add(new HorizBarChart(data), BorderLayout.CENTER);
        }
        return card;
    }

    // ── Chart 2: Quality Grade Donut ─────────────────────────

    private JPanel buildQualityGradeChart() {
        JPanel card = createChartCard("🏅  Phân phối chất lượng thu hoạch");
        List<Object[]> data = null;
        try { data = harvestCtrl.getQualityGradeStats(); } catch (Exception ignored) {}

        if (data == null || data.isEmpty()) {
            card.add(buildNoDataLabel());
        } else {
            card.add(new DonutChart(data), BorderLayout.CENTER);
        }
        return card;
    }

    // ── Table 3: Top Yielding Lots ────────────────────────────

    private JPanel buildTopLotsTable() {
        JPanel card = createChartCard("🥇  Top 5 lô năng suất cao nhất");
        try {
            List<Object[]> data = lotCtrl.getTopYieldingLots(5);
            if (data == null || data.isEmpty()) {
                card.add(buildNoDataLabel());
            } else {
                String[] cols = {"#", "Mã lô", "Tổng năng suất (kg)"};
                String[][] rows = new String[data.size()][3];
                for (int i = 0; i < data.size(); i++) {
                    Object[] r = data.get(i);
                    rows[i][0] = String.valueOf(i + 1);
                    rows[i][1] = r[0] == null ? "" : r[0].toString();
                    rows[i][2] = r[1] == null ? "0" : String.format("%.1f", ((Number) r[1]).doubleValue());
                }
                JTable t = new JTable(rows, cols);
                UiUtils.styleTable(t);
                t.setEnabled(false);
                JScrollPane scroll = new JScrollPane(t);
                scroll.setBorder(BorderFactory.createEmptyBorder());
                card.add(scroll, BorderLayout.CENTER);
            }
        } catch (Exception ex) {
            card.add(buildNoDataLabel());
        }
        return card;
    }

    // ── Table 4: Cost by Supplier ─────────────────────────────

    private JPanel buildCostBySupplierTable() {
        JPanel card = createChartCard("💰  Tổng chi phí nhập kho theo nhà cung cấp");
        try {
            List<Object[]> data = importCtrl.getTotalCostBySupplier();
            if (data == null || data.isEmpty()) {
                card.add(buildNoDataLabel());
            } else {
                String[] cols = {"Nhà cung cấp", "Tổng chi phí (VNĐ)"};
                String[][] rows = new String[data.size()][2];
                for (int i = 0; i < data.size(); i++) {
                    Object[] r = data.get(i);
                    rows[i][0] = r[0] == null ? "" : r[0].toString();
                    rows[i][1] = r[1] == null ? "0"
                            : String.format("%,.0f", ((Number) r[1]).doubleValue());
                }
                JTable t = new JTable(rows, cols);
                UiUtils.styleTable(t);
                t.setEnabled(false);
                JScrollPane scroll = new JScrollPane(t);
                scroll.setBorder(BorderFactory.createEmptyBorder());
                card.add(scroll, BorderLayout.CENTER);
            }
        } catch (Exception ex) {
            card.add(buildNoDataLabel());
        }
        return card;
    }

    // ── Helpers ───────────────────────────────────────────────

    private JPanel createChartCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        JLabel lbl = new JLabel(title);
        lbl.setFont(AppTheme.FONT_SUBTITLE);
        lbl.setForeground(AppTheme.TEXT_PRIMARY);
        card.add(lbl, BorderLayout.NORTH);
        return card;
    }

    private JLabel buildNoDataLabel() {
        JLabel lbl = new JLabel("Không có dữ liệu hoặc chưa kết nối DB");
        lbl.setFont(AppTheme.FONT_BODY);
        lbl.setForeground(AppTheme.TEXT_MUTED);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    // ── Inner: Horizontal Bar Chart ───────────────────────────

    static class HorizBarChart extends JPanel {
        private final List<Object[]> data;
        private final Color[] palette = {
            new Color(0x52B788), new Color(0x40916C), new Color(0x74C69D),
            new Color(0x2D6A4F), new Color(0x95D5B2)
        };

        HorizBarChart(List<Object[]> data) {
            this.data = data;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double maxVal = data.stream()
                    .mapToDouble(r -> r[1] == null ? 0 : ((Number) r[1]).doubleValue())
                    .max().orElse(1);

            int padL = 120, padR = 60, padT = 10, rowH = 32, gap = 6;
            int chartW = getWidth() - padL - padR;

            for (int i = 0; i < data.size(); i++) {
                Object[] row = data.get(i);
                String label = row[0] == null ? "N/A" : row[0].toString();
                double val   = row[1] == null ? 0 : ((Number) row[1]).doubleValue();
                int barW = (int) (val / maxVal * chartW);
                int y    = padT + i * (rowH + gap);

                g2.setColor(palette[i % palette.length]);
                g2.fill(new java.awt.geom.RoundRectangle2D.Double(padL, y, barW, rowH - 4, 6, 6));

                g2.setColor(AppTheme.TEXT_SECONDARY);
                g2.setFont(AppTheme.FONT_SMALL);
                g2.drawString(label, 4, y + rowH - 10);

                g2.setColor(AppTheme.TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.drawString(String.format("%.0f kg", val), padL + barW + 6, y + rowH - 10);
            }
            g2.dispose();
        }
    }

    // ── Inner: Donut Chart ────────────────────────────────────

    static class DonutChart extends JPanel {
        private final List<Object[]> data;
        private final Color[] palette = {
            new Color(0x52B788), new Color(0x4895EF), new Color(0xFFB833), new Color(0xE63946)
        };

        DonutChart(List<Object[]> data) {
            this.data = data;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double total = data.stream()
                    .mapToDouble(r -> r[1] == null ? 0 : ((Number) r[1]).doubleValue())
                    .sum();
            if (total == 0) { g2.dispose(); return; }

            int size = Math.min(getWidth(), getHeight()) - 40;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            double startAngle = 0;

            for (int i = 0; i < data.size(); i++) {
                double val = data.get(i)[1] == null ? 0 : ((Number) data.get(i)[1]).doubleValue();
                double sweep = val / total * 360.0;

                g2.setColor(palette[i % palette.length]);
                g2.fill(new java.awt.geom.Arc2D.Double(x, y, size, size,
                        startAngle, sweep, java.awt.geom.Arc2D.PIE));

                startAngle += sweep;
            }

            // Donut hole
            int holeSize = size / 2;
            int hx = x + (size - holeSize) / 2;
            int hy = y + (size - holeSize) / 2;
            g2.setColor(AppTheme.BG_CARD);
            g2.fillOval(hx, hy, holeSize, holeSize);

            // Legend
            int legY = y + size + 12;
            int legX = x;
            g2.setFont(AppTheme.FONT_SMALL);
            for (int i = 0; i < data.size(); i++) {
                String label = data.get(i)[0] == null ? "N/A" : data.get(i)[0].toString();
                double val   = data.get(i)[1] == null ? 0 : ((Number) data.get(i)[1]).doubleValue();
                g2.setColor(palette[i % palette.length]);
                g2.fillOval(legX, legY - 10, 12, 12);
                g2.setColor(AppTheme.TEXT_SECONDARY);
                g2.drawString(label + " (" + (int)(val / total * 100) + "%)",
                        legX + 16, legY);
                legX += 90;
            }

            g2.dispose();
        }
    }
}
