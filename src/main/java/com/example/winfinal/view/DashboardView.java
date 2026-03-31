package com.example.winfinal.view;

import com.example.winfinal.controller.*;
import com.example.winfinal.dto.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Dashboard screen: 5 stat cards + alert list + bar chart.
 */
public class DashboardView extends JPanel {

    private final AgriSupplyController   supplyCtrl = new AgriSupplyController();
    private final ProductionLotController lotCtrl   = new ProductionLotController();
    private final PestReportController   pestCtrl   = new PestReportController();
    private final FarmController         farmCtrl   = new FarmController();

    private JLabel lblFarms, lblLots, lblLowStock, lblPests, lblHarvest;

    public DashboardView() {
        setLayout(new BorderLayout(0, 0));
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(28, 28, 28, 28));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel title = new JLabel("Tong quan he thong");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel date = new JLabel("Hom nay: "
                + new java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        date.setFont(AppTheme.FONT_BODY);
        date.setForeground(AppTheme.TEXT_SECONDARY);

        JButton btnRefresh = UiUtils.createSecondaryButton("Lam moi");
        btnRefresh.addActionListener(e -> refresh());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(date);
        right.add(btnRefresh);

        p.add(title, BorderLayout.WEST);
        p.add(right,  BorderLayout.EAST);
        p.setBorder(new EmptyBorder(0, 0, 20, 0));
        return p;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setOpaque(false);
        body.add(buildStatCards(), BorderLayout.NORTH);

        JPanel bottom = new JPanel(new GridLayout(1, 2, 20, 0));
        bottom.setOpaque(false);
        bottom.add(buildAlertPanel());
        bottom.add(buildChartPanel());
        body.add(bottom, BorderLayout.CENTER);
        return body;
    }

    // ── Stat cards ────────────────────────────────────────────

    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 5, 14, 0));
        row.setOpaque(false);

        lblFarms    = addStatCard(row, "Trang trai",       "--", AppTheme.PRIMARY);
        lblLots     = addStatCard(row, "Lo san xuat",      "--", AppTheme.INFO);
        lblLowStock = addStatCard(row, "Vat tu sap het",   "--", AppTheme.WARNING);
        lblPests    = addStatCard(row, "Canh bao sau benh","--", AppTheme.DANGER);
        lblHarvest  = addStatCard(row, "Thu hoach sap toi","--", new Color(0x9B72CF));

        return row;
    }

    private JLabel addStatCard(JPanel parent, String title, String initVal, Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.BG_CARD);
                g2.fill(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(accent);
                g2.fill(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), 5, 4, 4));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 18, 14, 18));

        JLabel valL = new JLabel(initVal);
        valL.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valL.setForeground(AppTheme.TEXT_PRIMARY);
        valL.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleL = new JLabel(title);
        titleL.setFont(AppTheme.FONT_SMALL);
        titleL.setForeground(AppTheme.TEXT_SECONDARY);
        titleL.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(Box.createVerticalStrut(4));
        card.add(valL);
        card.add(titleL);

        parent.add(card);
        return valL;
    }

    // ── Alert panel ───────────────────────────────────────────

    private JPanel buildAlertPanel() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel header = new JLabel("Canh bao can xu ly");
        header.setFont(AppTheme.FONT_SUBTITLE);
        header.setForeground(AppTheme.TEXT_PRIMARY);

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        list.setFont(AppTheme.FONT_BODY);
        list.setBackground(AppTheme.BG_CARD);
        list.setFixedCellHeight(36);
        list.setCellRenderer(new AlertCellRenderer());

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppTheme.BG_CARD);

        card.add(header, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        try {
            List<AgriSupplyDTO> low = supplyCtrl.getLowStockSupplies();
            for (AgriSupplyDTO s : low) {
                model.addElement("[VAT TU]  " + s.getName() + "  con " + s.getStockQty() + " " + s.getUnit());
            }
        } catch (Exception ignored) {}

        try {
            var pests = pestCtrl.getHighSeverityReports();
            for (var p : pests) {
                String lotInfo = "--";
                try { if (p.getLot() != null) lotInfo = String.valueOf(p.getLot().getId()); }
                catch (Exception ignored2) {}
                model.addElement("[SAU BENH]  Nghiem trong - Lo #" + lotInfo);
            }
        } catch (Exception ignored) {}

        if (model.isEmpty()) {
            model.addElement("Khong co canh bao nao");
        }

        return card;
    }

    // ── Chart panel ───────────────────────────────────────────

    private JPanel buildChartPanel() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel header = new JLabel("Nang suat theo mua vu (kg)");
        header.setFont(AppTheme.FONT_SUBTITLE);
        header.setForeground(AppTheme.TEXT_PRIMARY);

        card.add(header, BorderLayout.NORTH);
        card.add(new SimpleBarChart(), BorderLayout.CENTER);
        return card;
    }

    // ── Data refresh ──────────────────────────────────────────

    private void refresh() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            int farms = 0, lots = 0, lowStock = 0, pests = 0, harvest = 0;

            @Override
            protected Void doInBackground() {
                try { farms    = farmCtrl.getAllFarms().size(); }           catch (Exception ignored) {}
                try { lots     = lotCtrl.getAllLots().size(); }             catch (Exception ignored) {}
                try { lowStock = supplyCtrl.getLowStockSupplies().size(); } catch (Exception ignored) {}
                try { pests    = pestCtrl.getHighSeverityReports().size(); }catch (Exception ignored) {}
                try {
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DATE, 14);
                    harvest = lotCtrl.getUpcomingHarvest(c.getTime()).size();
                } catch (Exception ignored) {}
                return null;
            }

            @Override
            protected void done() {
                lblFarms.setText(String.valueOf(farms));
                lblLots.setText(String.valueOf(lots));
                lblLowStock.setText(String.valueOf(lowStock));
                lblPests.setText(String.valueOf(pests));
                lblHarvest.setText(String.valueOf(harvest));
            }
        };
        worker.execute();
    }

    // ── Inner: Alert renderer ─────────────────────────────────

    static class AlertCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            String text = value.toString();
            if (!isSelected) {
                lbl.setBackground(index % 2 == 0 ? AppTheme.BG_CARD : AppTheme.BG_TABLE_ROW_ALT);
            }
            if (text.startsWith("[VAT TU]"))    lbl.setForeground(new Color(0xB45309));
            else if (text.startsWith("[SAU"))    lbl.setForeground(AppTheme.DANGER);
            lbl.setBorder(new EmptyBorder(4, 8, 4, 8));
            lbl.setFont(AppTheme.FONT_BODY);
            return lbl;
        }
    }

    // ── Inner: Bar Chart ──────────────────────────────────────

    static class SimpleBarChart extends JPanel {
        private final String[] labels = {"Mua Xuan", "He Thu", "Thu Dong", "Dong Xuan"};
        private final int[]    values = {4200, 6800, 5100, 7300};
        private final Color[]  colors = {
            new Color(0x52B788), new Color(0x40916C),
            new Color(0x74C69D), new Color(0x2D6A4F)
        };

        SimpleBarChart() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 160));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int n = values.length;
            int maxVal = Arrays.stream(values).max().getAsInt();
            int padL = 10, padR = 10, padT = 10, padB = 30;
            int chartW = getWidth() - padL - padR;
            int chartH = getHeight() - padT - padB;
            int barGap = 10;
            int barW   = chartW / n - barGap;

            for (int i = 0; i < n; i++) {
                int barH = (int) ((double) values[i] / maxVal * chartH);
                int x = padL + i * (barW + barGap);
                int y = padT + chartH - barH;

                g2.setColor(colors[i]);
                g2.fill(new java.awt.geom.RoundRectangle2D.Double(x, y, barW, barH, 6, 6));

                g2.setColor(AppTheme.TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String valStr = values[i] + " kg";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(valStr, x + (barW - fm.stringWidth(valStr)) / 2, y - 4);

                g2.setColor(AppTheme.TEXT_SECONDARY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                FontMetrics fm2 = g2.getFontMetrics();
                g2.drawString(labels[i],
                        x + (barW - fm2.stringWidth(labels[i])) / 2,
                        getHeight() - 8);
            }
            g2.dispose();
        }
    }
}
