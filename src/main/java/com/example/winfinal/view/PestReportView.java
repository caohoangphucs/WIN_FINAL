package com.example.winfinal.view;

import com.example.winfinal.controller.PestReportController;
import com.example.winfinal.entity.operation.PestReport;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Pest Report screen with severity-color coding.
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
        p.add(UiUtils.createSectionTitle("Báo Cáo Sâu Bệnh"), BorderLayout.NORTH);

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setOpaque(false);

        JButton btnAll      = UiUtils.createSecondaryButton("Tất cả");
        JButton btnCritical = UiUtils.createDangerButton("NGUY CẤP / CAO");
        JButton btnAdd      = UiUtils.createPrimaryButton("+ Báo cáo mới");

        btnAll.addActionListener(e -> refreshTable(false));
        btnCritical.addActionListener(e -> refreshTable(true));
        btnAdd.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Chức năng thêm mới cần nhập qua Entity (lot, employee, severity JPA relations).",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE));

        bar.add(btnAll);
        bar.add(btnCritical);
        bar.add(btnAdd);
        p.add(bar, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildTable() {
        String[] cols = {"ID", "Lô sản xuất", "Tên sâu bệnh", "Mức độ", "Nhân viên", "Biện pháp"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UiUtils.styleTable(table);

        // Custom renderer for row coloring and translation
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, isSelected, hasFocus, row, col);
                
                // Get raw severity from model
                String sev = String.valueOf(t.getValueAt(row, 2));
                Color[] clrs = severityColor(sev);

                if (!isSelected) {
                    c.setBackground(clrs[0]);
                }

                // Data alignment
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 12, 0, 12));

                if (col == 2) {
                    setText(translateSeverity(sev));
                    setForeground(clrs[1]);
                    setFont(AppTheme.FONT_SIDEBAR_ACTIVE);
                } else {
                    setForeground(AppTheme.TEXT_PRIMARY);
                    setFont(AppTheme.FONT_BODY);
                }

                return c;
            }
        });

        table.getColumn("ID").setMinWidth(50);
        table.getColumn("ID").setMaxWidth(50);
        table.getColumn("ID").setPreferredWidth(50);
        
        table.getColumn("Lô sản xuất").setPreferredWidth(120);
        table.getColumn("Tên sâu bệnh").setPreferredWidth(130);
        table.getColumn("Mức độ").setPreferredWidth(100);
        table.getColumn("Nhân viên").setPreferredWidth(150);
        table.getColumn("Biện pháp").setPreferredWidth(200);

        // Center alignment for all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object val, boolean isSelected,
                                                               boolean hasFocus, int row, int col) {
                    Component c = super.getTableCellRendererComponent(t, val, isSelected, hasFocus, row, col);
                    
                    String sev = String.valueOf(t.getValueAt(row, 3)); // Severity is at col 3 now
                    Color[] clrs = severityColor(sev);

                    if (!isSelected) {
                        c.setBackground(clrs[0]);
                    }

                    setHorizontalAlignment(SwingConstants.CENTER);
                    setBorder(new EmptyBorder(0, 12, 0, 12));

                    if (col == 3) {
                        setText(translateSeverity(sev));
                        setForeground(clrs[1]);
                        setFont(AppTheme.FONT_SIDEBAR_ACTIVE);
                    } else {
                        setForeground(AppTheme.TEXT_PRIMARY);
                        setFont(AppTheme.FONT_BODY);
                    }
                    return c;
                }
            });
        }

        // Row click → show detail
        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;
                showPestDetail(row);
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1, true));
        scroll.getViewport().setBackground(AppTheme.BG_CARD);

        JPanel card = UiUtils.createCard();
        card.setLayout(new BorderLayout());
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private void showPestDetail(int row) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><b style='font-size:13px;'>Chi tiết báo cáo sâu bệnh</b><br><br>");
        sb.append("<b>ID:</b> ").append(tableModel.getValueAt(row, 0)).append("<br>");
        sb.append("<b>Lô sản xuất:</b> ").append(tableModel.getValueAt(row, 1)).append("<br>");
        sb.append("<b>Tên sâu bệnh:</b> ").append(tableModel.getValueAt(row, 2)).append("<br>");
        sb.append("<b>Mức độ:</b> ").append(translateSeverity(String.valueOf(tableModel.getValueAt(row, 3)))).append("<br>");
        sb.append("<b>Nhân viên:</b> ").append(tableModel.getValueAt(row, 4)).append("<br>");
        sb.append("<b>Biện pháp xử lý:</b> ").append(tableModel.getValueAt(row, 5)).append("</html>");
        JOptionPane.showMessageDialog(this, sb.toString(),
                "Chi tiết sâu bệnh", JOptionPane.PLAIN_MESSAGE);
    }

    private String translateSeverity(String sev) {
        if (sev == null) return "--";
        return switch (sev.toUpperCase()) {
            case "CRITICAL" -> "NGUY CẤP";
            case "HIGH"     -> "CAO";
            case "MEDIUM"   -> "TRUNG BÌNH";
            case "LOW"      -> "THẤP";
            default         -> sev;
        };
    }

    private Color[] severityColor(String sev) {
        if (sev == null) return new Color[]{AppTheme.BG_CARD, AppTheme.TEXT_SECONDARY};
        return switch (sev.toUpperCase()) {
            case "CRITICAL" -> new Color[]{new Color(0xFFF1F2), AppTheme.DANGER};
            case "HIGH"     -> new Color[]{new Color(0xFFF7ED), new Color(0x9A3412)};
            case "MEDIUM"   -> new Color[]{new Color(0xFEFCE8), new Color(0x854D0E)};
            case "LOW"      -> new Color[]{new Color(0xF0FDF4), new Color(0x166534)};
            default         -> new Color[]{AppTheme.BG_CARD, AppTheme.TEXT_SECONDARY};
        };
    }

    void refreshTable(boolean criticalOnly) {
        tableModel.setRowCount(0);
        try {
            List<PestReport> list = criticalOnly
                    ? ctrl.getHighSeverityReports()
                    : ctrl.getAllReports();
            for (PestReport p : list) {
                String lotInfo = "--", sevCode = "--", empInfo = "--";
                try { lotInfo = p.getLot()      == null ? "--" : p.getLot().getLotCode();      } catch (Exception ignored) {}
                try { sevCode = p.getSeverity() == null ? "--" : p.getSeverity().getCode();    } catch (Exception ignored) {}
                try { empInfo = p.getEmployee() == null ? "--" : p.getEmployee().getFullName(); } catch (Exception ignored) {}
                
                String pestName = p.getPestName() != null ? p.getPestName() : "--";
                String treatment = p.getTreatment() != null ? p.getTreatment() : "--";

                // col 0: ID, col 1: Lot, col 2: Pest, col 3: Severity, col 4: Employee, col 5: Treatment
                tableModel.addRow(new Object[]{p.getId(), lotInfo, pestName, sevCode, empInfo, treatment});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}
