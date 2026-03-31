package com.example.winfinal.view;

import com.example.winfinal.controller.*;
import com.example.winfinal.dto.ProductionLotDTO;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Traceability screen: enter a lot code and see a timeline of events.
 */
public class TraceabilityView extends JPanel {

    private final ProductionLotController  lotCtrl         = new ProductionLotController();
    private final CultivationLogController cultivationCtrl = new CultivationLogController();
    private final IrrigationLogController  irrigationCtrl  = new IrrigationLogController();
    private final PestReportController     pestCtrl        = new PestReportController();
    private final HarvestRecordController  harvestCtrl     = new HarvestRecordController();

    private JTextField txtLotCode;
    private JPanel timelinePanel;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public TraceabilityView() {
        setLayout(new BorderLayout(0, 20));
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        add(buildSearchBar(), BorderLayout.NORTH);
        add(buildTimeline(),  BorderLayout.CENTER);
    }

    // ── Search bar ────────────────────────────────────────────

    private JPanel buildSearchBar() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);

        JLabel title = UiUtils.createSectionTitle("Truy Xuat Nguon Goc");

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputRow.setOpaque(false);

        txtLotCode = UiUtils.createSearchField("Nhap ma lo, vd: FARM001-2026-001");
        txtLotCode.setPreferredSize(new Dimension(320, AppTheme.BUTTON_HEIGHT));

        JButton btnTrace = UiUtils.createPrimaryButton("Truy xuat");
        JButton btnClear = UiUtils.createSecondaryButton("Xoa");

        btnTrace.addActionListener(e -> doTrace());
        txtLotCode.addActionListener(e -> doTrace());
        btnClear.addActionListener(e -> {
            txtLotCode.setText("");
            timelinePanel.removeAll();
            timelinePanel.revalidate();
            timelinePanel.repaint();
        });

        inputRow.add(new JLabel("Ma lo:"));
        inputRow.add(txtLotCode);
        inputRow.add(btnTrace);
        inputRow.add(btnClear);

        p.add(title,    BorderLayout.NORTH);
        p.add(inputRow, BorderLayout.SOUTH);
        return p;
    }

    // ── Timeline panel ────────────────────────────────────────

    private JScrollPane buildTimeline() {
        timelinePanel = new JPanel();
        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        timelinePanel.setBackground(AppTheme.BG_MAIN);

        JScrollPane scroll = new JScrollPane(timelinePanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppTheme.BG_MAIN);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    // ── Trace logic ───────────────────────────────────────────

    private void doTrace() {
        String lotCode = txtLotCode.getText().trim();
        if (lotCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long nhap ma lo.");
            return;
        }

        timelinePanel.removeAll();

        // Lot info card
        try {
            ProductionLotDTO lot = lotCtrl.getFullTraceabilityInfo(lotCode);
            if (lot != null) {
                timelinePanel.add(buildLotInfoCard(lot));
                timelinePanel.add(Box.createVerticalStrut(12));
            }
        } catch (Exception ignored) {}

        timelinePanel.add(buildSectionLabel("Timeline hoat dong"));
        timelinePanel.add(Box.createVerticalStrut(8));

        // Cultivation logs
        try {
            List<Object[]> logs = cultivationCtrl.getTraceabilityLogs(lotCode);
            for (Object[] row : logs) {
                String date = row[0] == null ? "N/A" : row[0].toString();
                String type = row[1] == null ? "" : row[1].toString();
                String desc = row[2] == null ? "" : row[2].toString();
                timelinePanel.add(buildTimelineItem("[CANH TAC]", date,
                        type + " - " + desc, AppTheme.PRIMARY, false));
                timelinePanel.add(Box.createVerticalStrut(6));
            }
        } catch (Exception ignored) {}

        // Irrigation logs
        try {
            List<Object[]> logs = irrigationCtrl.getTraceabilityLogs(lotCode);
            for (Object[] row : logs) {
                String date = row[0] == null ? "N/A" : row[0].toString();
                String vol  = row[1] == null ? "" : "Luong nuoc: " + row[1] + " L";
                timelinePanel.add(buildTimelineItem("[TUOI TIEU]", date,
                        "Tuoi tieu - " + vol, AppTheme.INFO, false));
                timelinePanel.add(Box.createVerticalStrut(6));
            }
        } catch (Exception ignored) {}

        // Pest reports
        try {
            List<Object[]> logs = pestCtrl.getTraceabilityLogs(lotCode);
            for (Object[] row : logs) {
                String date     = row[0] == null ? "N/A" : row[0].toString();
                String type     = row[1] == null ? "" : row[1].toString();
                String sev      = row[2] == null ? "" : row[2].toString();
                boolean serious = sev.equalsIgnoreCase("CRITICAL") || sev.equalsIgnoreCase("HIGH");
                timelinePanel.add(buildTimelineItem("[SAU BENH]", date,
                        "Sau benh: " + type + " [" + sev + "]",
                        serious ? AppTheme.DANGER : AppTheme.WARNING, serious));
                timelinePanel.add(Box.createVerticalStrut(6));
            }
        } catch (Exception ignored) {}

        // Harvest
        try {
            var harvests = harvestCtrl.findByLotCode(lotCode);
            for (var h : harvests) {
                String date  = h.getHarvestDate() == null ? "N/A" : sdf.format(h.getHarvestDate());
                String yInfo = "Nang suat: " + h.getYieldKg() + " kg, Chat luong: " + h.getQualityGradeCode();
                timelinePanel.add(buildTimelineItem("[THU HOACH]", date,
                        yInfo, AppTheme.SUCCESS, false));
                timelinePanel.add(Box.createVerticalStrut(6));
            }
        } catch (Exception ignored) {}

        if (timelinePanel.getComponentCount() <= 2) {
            JLabel empty = new JLabel("Khong tim thay du lieu cho ma lo: " + lotCode);
            empty.setFont(AppTheme.FONT_BODY);
            empty.setForeground(AppTheme.TEXT_SECONDARY);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            timelinePanel.add(empty);
        }

        timelinePanel.revalidate();
        timelinePanel.repaint();
    }

    // ── Lot info card ─────────────────────────────────────────

    private JPanel buildLotInfoCard(ProductionLotDTO lot) {
        JPanel card = new JPanel(new GridLayout(2, 3, 16, 8));
        card.setBackground(new Color(0xECFDF5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.PRIMARY, 1, true),
                new EmptyBorder(12, 16, 12, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        addInfoItem(card, "Ma lo",              lot.getLotCode());
        addInfoItem(card, "Trang thai",          lot.getStatusCode());
        addInfoItem(card, "Dien tich",           lot.getAreaM2() + " m2");
        addInfoItem(card, "Ngay trong",
                lot.getPlantDate() == null ? "N/A" : sdf.format(lot.getPlantDate()));
        addInfoItem(card, "Du kien thu hoach",
                lot.getExpectedHarvestDate() == null ? "N/A" : sdf.format(lot.getExpectedHarvestDate()));
        addInfoItem(card, "Farm ID",             String.valueOf(lot.getFarmId()));
        return card;
    }

    private void addInfoItem(JPanel p, String label, String value) {
        JPanel item = new JPanel(new BorderLayout(0, 2));
        item.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        JLabel val = new JLabel(value == null ? "-" : value);
        val.setFont(AppTheme.FONT_SUBTITLE);
        val.setForeground(AppTheme.TEXT_PRIMARY);
        item.add(lbl, BorderLayout.NORTH);
        item.add(val, BorderLayout.CENTER);
        p.add(item);
    }

    // ── Timeline item ─────────────────────────────────────────

    private JPanel buildTimelineItem(String tag, String date, String text,
                                     Color accentColor, boolean isAlert) {
        JPanel item = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.BG_CARD);
                g2.fill(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(accentColor);
                g2.fill(new java.awt.geom.RoundRectangle2D.Double(0, 0, 4, getHeight(), 4, 4));
                g2.dispose();
            }
        };
        item.setOpaque(false);
        item.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel tagLbl = new JLabel(tag);
        tagLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tagLbl.setForeground(accentColor);

        JLabel dateLbl = new JLabel(date);
        dateLbl.setFont(AppTheme.FONT_SMALL);
        dateLbl.setForeground(AppTheme.TEXT_SECONDARY);
        dateLbl.setPreferredSize(new Dimension(90, 0));

        JLabel textLbl = new JLabel(text);
        textLbl.setFont(isAlert ? AppTheme.FONT_SUBTITLE : AppTheme.FONT_BODY);
        textLbl.setForeground(isAlert ? accentColor : AppTheme.TEXT_PRIMARY);

        item.add(tagLbl);
        item.add(dateLbl);
        item.add(textLbl);
        return item;
    }

    private JLabel buildSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.FONT_SUBTITLE);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(4, 0, 4, 0));
        return lbl;
    }
}
