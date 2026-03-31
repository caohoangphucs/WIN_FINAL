package com.example.winfinal.view;

import com.example.winfinal.controller.PestReportController;
import com.example.winfinal.entity.operation.PestReport;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Pest Report screen — shows all reports with severity-color coding.
 * Uses only PestReport.getId() and loads full data via service queries.
 */
public class PestReportView extends JPanel {

    private final PestReportController ctrl = new PestReportController();

    private JTable table;
    private DefaultTableModel tableModel;

    public PestReportView() {
        setLayout(new BorderLayout(0, 16));
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(),  BorderLayout.CENTER);

        refreshTable(false);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(12, 8));
        p.setOpaque(false);
        p.add(UiUtils.createSectionTitle("🐛  Báo Cáo Sâu Bệnh"), BorderLayout.NORTH);

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setOpaque(false);

        JButton btnAll      = UiUtils.createSecondaryButton("Tất cả");
        JButton btnCritical = UiUtils.createDangerButton("🚨 CRITICAL / HIGH");
        JButton btnAdd      = UiUtils.createPrimaryButton("＋  Báo cáo mới");

        btnAll.addActionListener(e -> refreshTable(false));
        btnCritical.addActionListener(e -> refreshTable(true));
        btnAdd.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Chức năng thêm sâu bệnh cần nhập qua Entity (lot, employee, severity JPA relations).",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE));

        bar.add(btnAll);
        bar.add(btnCritical);
        bar.add(btnAdd);
        p.add(bar, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildTable() {
        String[] cols = {"ID", "Lot (ID)", "Severity", "Employee (ID)", "Ghi chú"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UiUtils.styleTable(table);

        // Severity badge — column 2
        table.getColumnModel().getColumn(2).setCellRenderer((t, val, sel, foc, row, col) -> {
            String sev = val == null ? "" : val.toString();
            Color[] clrs = severityColor(sev);
            JLabel lbl = UiUtils.createBadge(sev, clrs[0], clrs[1]);
            lbl.setOpaque(true);
            lbl.setBackground(sel ? AppTheme.BG_TABLE_HEADER : AppTheme.BG_CARD);
            return lbl;
        });

        table.getColumn("ID").setPreferredWidth(50);
        table.getColumn("Lot (ID)").setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1, true));
        scroll.getViewport().setBackground(AppTheme.BG_CARD);

        JPanel card = UiUtils.createCard();
        card.setLayout(new BorderLayout());
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private Color[] severityColor(String sev) {
        return switch (sev.toUpperCase()) {
            case "CRITICAL" -> new Color[]{new Color(0xFEE2E2), AppTheme.DANGER};
            case "HIGH"     -> new Color[]{new Color(0xFED7AA), new Color(0x9A3412)};
            case "MEDIUM"   -> new Color[]{new Color(0xFEF9C3), new Color(0x854D0E)};
            case "LOW"      -> new Color[]{new Color(0xDCFCE7), new Color(0x166534)};
            default         -> new Color[]{AppTheme.BG_TABLE_ROW_ALT, AppTheme.TEXT_SECONDARY};
        };
    }

    void refreshTable(boolean criticalOnly) {
        tableModel.setRowCount(0);
        try {
            List<PestReport> list = criticalOnly
                    ? ctrl.getHighSeverityReports()
                    : ctrl.getAllReports();
            for (PestReport p : list) {
                String lotInfo = "", sevCode = "", empInfo = "";
                try { lotInfo = p.getLot() == null ? "—" : String.valueOf(p.getLot().getId()); }
                catch (Exception ignored) {}
                try { sevCode = p.getSeverity() == null ? "—" : p.getSeverity().getCode(); }
                catch (Exception ignored) {}
                try { empInfo = p.getEmployee() == null ? "—" : String.valueOf(p.getEmployee().getId()); }
                catch (Exception ignored) {}
                tableModel.addRow(new Object[]{p.getId(), lotInfo, sevCode, empInfo, ""});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}
