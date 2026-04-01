package com.example.winfinal.view;

import com.example.winfinal.controller.CultivationLogController;
import com.example.winfinal.dto.CultivationLogDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Cultivation Log management screen.
 */
public class CultivationLogView extends JPanel {

    private final CultivationLogController ctrl = new CultivationLogController();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private JTable table;
    private DefaultTableModel tableModel;

    public CultivationLogView() {
        setLayout(new BorderLayout(0, 16));
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(),  BorderLayout.CENTER);

        refreshTable();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(12, 8));
        p.setOpaque(false);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.add(UiUtils.createSectionTitle("Nhật Ký Canh Tác"), BorderLayout.WEST);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);

        // Filter Activity Type
        JLabel lblType = new JLabel("Lọc theo loại:");
        lblType.setFont(AppTheme.FONT_BODY);
        String[] types = {"Tất cả", "FERTILIZE", "PESTICIDE", "FUNGICIDE", "FOLIAR"};
        JComboBox<String> cboFilter = new JComboBox<>(types);
        cboFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                String translated = "Tất cả".equals(value) ? "Tất cả" : translateActivity(String.valueOf(value));
                super.getListCellRendererComponent(list, translated, index, isSelected, cellHasFocus);
                return this;
            }
        });
        cboFilter.addActionListener(e -> refreshTable((String) cboFilter.getSelectedItem()));

        // Search Field
        JTextField txtSearch = UiUtils.createSearchField("Tìm lô/vật tư...");
        txtSearch.setPreferredSize(new Dimension(180, 32));
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                refreshTable((String) cboFilter.getSelectedItem(), txtSearch.getText());
            }
        });

        JButton btnAdd     = UiUtils.createPrimaryButton("+ Ghi hoạt động");
        JButton btnRefresh = UiUtils.createSecondaryButton("Làm mới");

        btnAdd.addActionListener(e -> openDialog(null));
        btnRefresh.addActionListener(e -> {
            cboFilter.setSelectedIndex(0);
            txtSearch.setText("");
            refreshTable(null, null);
        });

        filterBar.add(lblType);
        filterBar.add(cboFilter);
        filterBar.add(txtSearch);
        filterBar.add(btnRefresh);
        filterBar.add(btnAdd);

        p.add(titleRow,   BorderLayout.NORTH);
        p.add(filterBar,  BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildTable() {
        String[] cols = {"ID", "Lô sản xuất", "Ngày thực hiện", "Hoạt động",
                         "Vật tư", "Liều lượng", "Nhân viên", "Thao tác"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
        };
        table = new JTable(tableModel);
        UiUtils.styleTable(table);
        table.setAutoCreateRowSorter(true);

        // Action column: renderer, editor, and disable sorting
        table.getColumn("Thao tác").setCellRenderer(new FarmView.ActionRenderer());
        table.getColumn("Thao tác").setCellEditor(new LogActionEditor(table));
        if (table.getRowSorter() instanceof javax.swing.table.TableRowSorter<?> sorter) {
            sorter.setSortable(7, false); // Col 7 is "Thao tác"
        }
        table.getColumn("Thao tác").setMinWidth(200);
        table.getColumn("Thao tác").setMaxWidth(220);
        table.getColumn("Thao tác").setPreferredWidth(220);
        table.getColumn("Thao tác").setResizable(false);

        table.getColumn("ID").setMinWidth(45);
        table.getColumn("ID").setMaxWidth(45);
        table.getColumn("ID").setPreferredWidth(45);
        
        table.getColumn("Lô sản xuất").setPreferredWidth(100);
        table.getColumn("Ngày thực hiện").setPreferredWidth(110);
        table.getColumn("Hoạt động").setPreferredWidth(120);
        table.getColumn("Vật tư").setPreferredWidth(150);
        table.getColumn("Liều lượng").setPreferredWidth(100);
        table.getColumn("Nhân viên").setPreferredWidth(130);

        // Center align data
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 7; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // Row click → show detail
        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                if (col == 7) return;
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;
                showLogDetail(row);
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

    private void showLogDetail(int row) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><b style='font-size:13px;'>Chi tiết nhật ký canh tác</b><br><br>");
        sb.append("<b>ID:</b> ").append(tableModel.getValueAt(row, 0)).append("<br>");
        sb.append("<b>Lô:</b> ").append(tableModel.getValueAt(row, 1)).append("<br>");
        sb.append("<b>Ngày:</b> ").append(tableModel.getValueAt(row, 2)).append("<br>");
        sb.append("<b>Hoạt động:</b> ").append(tableModel.getValueAt(row, 3)).append("<br>");
        sb.append("<b>Vật tư:</b> ").append(tableModel.getValueAt(row, 4)).append("<br>");
        sb.append("<b>Liều lượng:</b> ").append(tableModel.getValueAt(row, 5)).append("<br>");
        sb.append("<b>Nhân viên:</b> ").append(tableModel.getValueAt(row, 6)).append("</html>");
        JOptionPane.showMessageDialog(this, sb.toString(),
                "Chi tiết nhật ký", JOptionPane.PLAIN_MESSAGE);
    }

    private String translateActivity(String code) {
        if (code == null) return "--";
        return switch (code.toUpperCase()) {
            case "FERTILIZE" -> "Bón phân";
            case "PESTICIDE" -> "Phun thuốc sâu";
            case "FUNGICIDE" -> "Phun thuốc bệnh";
            case "FOLIAR"    -> "Bón phân lá";
            case "KHAC"      -> "Khác";
            default          -> code;
        };
    }

    void refreshTable() {
        refreshTable(null, null);
    }

    void refreshTable(String filterType) {
        refreshTable(filterType, null);
    }

    void refreshTable(String filterType, String searchText) {
        tableModel.setRowCount(0);
        try {
            List<CultivationLogDTO> list = ctrl.getAllLogs();
            String search = (searchText == null) ? "" : searchText.toLowerCase().trim();

            for (CultivationLogDTO l : list) {
                // 1. Apply Type Filter
                if (filterType != null && !"Tất cả".equals(filterType)) {
                    if (!filterType.equalsIgnoreCase(l.getActivityTypeCode())) continue;
                }

                // 2. Apply Search Text Filter (Lot or Material)
                if (!search.isEmpty()) {
                    String lot = (l.getLotCode() != null ? l.getLotCode() : "").toLowerCase();
                    String mat = (l.getSupplyName() != null ? l.getSupplyName() : "").toLowerCase();
                    if (!lot.contains(search) && !mat.contains(search)) continue;
                }

                String dosageStr = (l.getDosageUsed() != null ? l.getDosageUsed() : "0") 
                                 + " " + (l.getSupplyUnit() != null ? l.getSupplyUnit() : "");
                
                tableModel.addRow(new Object[]{
                    l.getId(), 
                    l.getLotCode() != null ? l.getLotCode() : "--",
                    l.getAppliedAt() == null ? "" : sdf.format(l.getAppliedAt()),
                    translateActivity(l.getActivityTypeCode()),
                    l.getSupplyName() != null ? l.getSupplyName() : "--", 
                    dosageStr, 
                    l.getEmployeeFullName() != null ? l.getEmployeeFullName() : "--",
                    "edit|delete"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    void openDialog(CultivationLogDTO existing) {
        boolean isEdit = existing != null;
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                isEdit ? "Sửa nhật ký" : "Ghi hoạt động canh tác",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(440, 400);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 12));
        form.setBackground(AppTheme.BG_CARD);
        form.setBorder(new EmptyBorder(24, 24, 16, 24));

        JTextField txtLot  = UiUtils.addFormField(form, "ID Lô sản xuất *");
        JTextField txtDate = UiUtils.addFormField(form, "Ngày (dd/MM/yyyy) *");

        JLabel lblType = new JLabel("Loại hoạt động");
        lblType.setFont(AppTheme.FONT_BODY);
        lblType.setForeground(AppTheme.TEXT_SECONDARY);
        String[] types = {"FERTILIZE", "PESTICIDE", "FUNGICIDE", "FOLIAR", "KHAC"};
        JComboBox<String> cboType = new JComboBox<>(types);
        cboType.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, translateActivity(String.valueOf(value)), index, isSelected, cellHasFocus);
                return this;
            }
        });
        cboType.setFont(AppTheme.FONT_BODY);
        form.add(lblType);
        form.add(cboType);

        JTextField txtSup  = UiUtils.addFormField(form, "ID Vật tư");
        JTextField txtQty  = UiUtils.addFormField(form, "Liều lượng sử dụng");
        JTextField txtEmp  = UiUtils.addFormField(form, "ID Nhân viên");
        form.add(new JLabel());
        form.add(new JLabel());

        if (isEdit) {
            txtLot.setText(existing.getLotId() == null ? "" : existing.getLotId().toString());
            txtDate.setText(existing.getAppliedAt() == null ? "" : sdf.format(existing.getAppliedAt()));
            if (existing.getActivityTypeCode() != null) cboType.setSelectedItem(existing.getActivityTypeCode());
            txtSup.setText(existing.getSupplyId() == null ? "" : existing.getSupplyId().toString());
            txtQty.setText(existing.getDosageUsed() == null ? "" : existing.getDosageUsed().toString());
            txtEmp.setText(existing.getEmployeeId() == null ? "" : existing.getEmployeeId().toString());
        }

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnBar.setBackground(AppTheme.BG_CARD);
        JButton btnCancel = UiUtils.createSecondaryButton("Hủy");
        JButton btnSave   = UiUtils.createPrimaryButton("Lưu");

        btnCancel.addActionListener(e -> dlg.dispose());
        btnSave.addActionListener(e -> {
            try {
                CultivationLogDTO dto = isEdit ? existing : new CultivationLogDTO();
                dto.setLotId(Long.parseLong(txtLot.getText().trim()));
                dto.setAppliedAt(sdf.parse(txtDate.getText().trim()));
                dto.setActivityTypeCode((String) cboType.getSelectedItem());
                dto.setSupplyId(txtSup.getText().trim().isEmpty() ? null
                        : Long.parseLong(txtSup.getText().trim()));
                dto.setDosageUsed(txtQty.getText().trim().isEmpty() ? null
                        : Double.parseDouble(txtQty.getText().trim()));
                dto.setEmployeeId(txtEmp.getText().trim().isEmpty() ? null
                        : Long.parseLong(txtEmp.getText().trim()));

                if (isEdit) ctrl.updateLog(dto);
                else        ctrl.createLog(dto);

                dlg.dispose();
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi: " + ex.getMessage());
            }
        });

        btnBar.add(btnCancel);
        btnBar.add(btnSave);
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnBar, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    class LogActionEditor extends DefaultCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        private final JButton btnEdit   = UiUtils.createSecondaryButton("Sửa");
        private final JButton btnDelete = UiUtils.createDangerButton("Xóa");
        private int currentRow;

        LogActionEditor(JTable t) {
            super(new JCheckBox());
            panel.setOpaque(true);
            panel.setBackground(AppTheme.BG_CARD);
            btnEdit.setPreferredSize(new Dimension(80, 28));
            btnDelete.setPreferredSize(new Dimension(80, 28));
            panel.add(btnEdit);
            panel.add(btnDelete);

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                try {
                    CultivationLogDTO dto = new CultivationLogDTO();
                    dto.setId(Long.parseLong(tableModel.getValueAt(currentRow, 0).toString()));
                    openDialog(dto);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CultivationLogView.this, "Lỗi: " + ex.getMessage());
                }
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                int cf = JOptionPane.showConfirmDialog(CultivationLogView.this,
                        "Xóa nhật ký này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (cf == JOptionPane.YES_OPTION) {
                    try {
                        ctrl.deleteLog(Long.parseLong(tableModel.getValueAt(currentRow, 0).toString()));
                        refreshTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(CultivationLogView.this, "Lỗi: " + ex.getMessage());
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            currentRow = r;
            return panel;
        }
        @Override public Object getCellEditorValue() { return "edit|delete"; }
    }
}
