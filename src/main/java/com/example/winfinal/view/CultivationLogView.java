package com.example.winfinal.view;

import com.example.winfinal.controller.CultivationLogController;
import com.example.winfinal.dto.CultivationLogDTO;

import javax.swing.*;
import javax.swing.border.*;
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

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);
        titleRow.add(UiUtils.createSectionTitle("Nhat Ky Canh Tac"));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);

        JButton btnAdd     = UiUtils.createPrimaryButton("+ Ghi hoat dong");
        JButton btnRefresh = UiUtils.createSecondaryButton("Lam moi");

        btnAdd.addActionListener(e -> openDialog(null));
        btnRefresh.addActionListener(e -> refreshTable());

        filterBar.add(btnRefresh);
        filterBar.add(btnAdd);

        p.add(titleRow,   BorderLayout.NORTH);
        p.add(filterBar,  BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildTable() {
        String[] cols = {"ID", "Lo ID", "Ngay thuc hien", "Loai hoat dong",
                         "Vat tu ID", "Lieu luong", "Nhan vien ID", "Thao tac"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
        };
        table = new JTable(tableModel);
        UiUtils.styleTable(table);

        table.getColumn("Thao tac").setCellRenderer(new FarmView.ActionRenderer());
        table.getColumn("Thao tac").setCellEditor(new LogActionEditor(table));
        table.getColumn("Thao tac").setMinWidth(200);
        table.getColumn("Thao tac").setMaxWidth(220);
        table.getColumn("Thao tac").setPreferredWidth(220);
        table.getColumn("Thao tac").setResizable(false);

        table.getColumn("ID").setMinWidth(45);
        table.getColumn("ID").setMaxWidth(45);
        table.getColumn("ID").setPreferredWidth(45);
        
        table.getColumn("Lo ID").setMinWidth(110);
        table.getColumn("Lo ID").setMaxWidth(110);
        table.getColumn("Lo ID").setPreferredWidth(110);

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
        sb.append("<html><b>Chi tiết nhật ký canh tác</b><br><br>");
        sb.append("ID: ").append(tableModel.getValueAt(row, 0)).append("<br>");
        sb.append("Lô ID: ").append(tableModel.getValueAt(row, 1)).append("<br>");
        sb.append("Ngày thực hiện: ").append(tableModel.getValueAt(row, 2)).append("<br>");
        sb.append("Loại hoạt động: ").append(tableModel.getValueAt(row, 3)).append("<br>");
        sb.append("Vật tư ID: ").append(tableModel.getValueAt(row, 4)).append("<br>");
        sb.append("Liều lượng: ").append(tableModel.getValueAt(row, 5)).append("<br>");
        sb.append("Nhân viên ID: ").append(tableModel.getValueAt(row, 6)).append("</html>");
        JOptionPane.showMessageDialog(this, sb.toString(),
                "Chi tiết nhật ký", JOptionPane.PLAIN_MESSAGE);
    }

    void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<CultivationLogDTO> list = ctrl.getAllLogs();
            for (CultivationLogDTO l : list) {
                tableModel.addRow(new Object[]{
                    l.getId(), l.getLotId(),
                    l.getAppliedAt() == null ? "" : sdf.format(l.getAppliedAt()),
                    l.getActivityTypeCode(),
                    l.getSupplyId(), l.getDosageUsed(), l.getEmployeeId(),
                    "edit|delete"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Loi: " + ex.getMessage());
        }
    }

    void openDialog(CultivationLogDTO existing) {
        boolean isEdit = existing != null;
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                isEdit ? "Sua nhat ky" : "Ghi hoat dong canh tac",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(440, 400);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 12));
        form.setBackground(AppTheme.BG_CARD);
        form.setBorder(new EmptyBorder(24, 24, 16, 24));

        JTextField txtLot  = UiUtils.addFormField(form, "Lo ID *");
        JTextField txtDate = UiUtils.addFormField(form, "Ngay (dd/MM/yyyy) *");

        JLabel lblType = new JLabel("Loai hoat dong");
        lblType.setFont(AppTheme.FONT_BODY);
        lblType.setForeground(AppTheme.TEXT_SECONDARY);
        String[] types = {"BON_PHAN", "PHUN_THUOC", "TUOI_NUOC", "LAM_CO",
                          "KIEM_TRA", "THU_HOACH", "KHAC"};
        JComboBox<String> cboType = new JComboBox<>(types);
        cboType.setFont(AppTheme.FONT_BODY);
        form.add(lblType);
        form.add(cboType);

        JTextField txtSup  = UiUtils.addFormField(form, "Vat tu ID");
        JTextField txtQty  = UiUtils.addFormField(form, "Lieu luong su dung");
        JTextField txtEmp  = UiUtils.addFormField(form, "Nhan vien ID");
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
        JButton btnCancel = UiUtils.createSecondaryButton("Huy");
        JButton btnSave   = UiUtils.createPrimaryButton("Luu");

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
                JOptionPane.showMessageDialog(dlg, "Loi: " + ex.getMessage());
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
                    JOptionPane.showMessageDialog(CultivationLogView.this, "Loi: " + ex.getMessage());
                }
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                int cf = JOptionPane.showConfirmDialog(CultivationLogView.this,
                        "Xoa nhat ky nay?", "Xac nhan", JOptionPane.YES_NO_OPTION);
                if (cf == JOptionPane.YES_OPTION) {
                    try {
                        ctrl.deleteLog(Long.parseLong(tableModel.getValueAt(currentRow, 0).toString()));
                        refreshTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(CultivationLogView.this, "Loi: " + ex.getMessage());
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
