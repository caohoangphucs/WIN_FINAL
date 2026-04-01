package com.example.winfinal.view;

import com.example.winfinal.controller.HarvestRecordController;
import com.example.winfinal.dto.HarvestRecordDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Harvest Record management with quality-grade colored badges.
 */
public class HarvestRecordView extends JPanel {

    private final HarvestRecordController ctrl = new HarvestRecordController();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public HarvestRecordView() {
        setLayout(new BorderLayout(0, 16));
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(),  BorderLayout.CENTER);

        refreshTable(null);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(12, 8));
        p.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);
        titleRow.add(UiUtils.createSectionTitle("Nhat Ky Thu Hoach"));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);

        searchField = UiUtils.createSearchField("Tim theo ma lo...");
        JButton btnSearch = UiUtils.createSecondaryButton("Tim");
        JButton btnAdd    = UiUtils.createPrimaryButton("+ Ghi thu hoach");

        btnSearch.addActionListener(e -> refreshTable(searchField.getText().trim()));
        searchField.addActionListener(e -> refreshTable(searchField.getText().trim()));
        btnAdd.addActionListener(e -> openDialog(null));

        filterBar.add(searchField);
        filterBar.add(btnSearch);
        filterBar.add(btnAdd);

        p.add(titleRow,   BorderLayout.NORTH);
        p.add(filterBar,  BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildTable() {
        String[] cols = {"ID", "Lo ID", "Ngay thu hoach", "Nang suat (kg)",
                         "Chat luong", "Nhan vien ID", "Khach hang ID", "Thao tac"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
        };
        table = new JTable(tableModel);
        UiUtils.styleTable(table);

        // Quality grade badge
        table.getColumnModel().getColumn(4).setCellRenderer((t, val, sel, foc, row, col) -> {
            String grade = val == null ? "" : val.toString();
            Color[] clrs = gradeColor(grade);
            JLabel lbl = UiUtils.createBadge(grade, clrs[0], clrs[1]);
            lbl.setOpaque(true);
            lbl.setBackground(sel ? AppTheme.BG_TABLE_HEADER : AppTheme.BG_CARD);
            return lbl;
        });

        table.getColumn("Thao tac").setCellRenderer(new FarmView.ActionRenderer());
        table.getColumn("Thao tac").setCellEditor(new HarvestActionEditor(table));
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
                showHarvestDetail(row);
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

    private void showHarvestDetail(int row) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><b>Chi tiết bản ghi thu hoạch</b><br><br>");
        sb.append("ID: ").append(tableModel.getValueAt(row, 0)).append("<br>");
        sb.append("Lô ID: ").append(tableModel.getValueAt(row, 1)).append("<br>");
        sb.append("Ngày thu hoạch: ").append(tableModel.getValueAt(row, 2)).append("<br>");
        sb.append("Năng suất (kg): ").append(tableModel.getValueAt(row, 3)).append("<br>");
        sb.append("Chất lượng: ").append(tableModel.getValueAt(row, 4)).append("<br>");
        sb.append("Nhân viên ID: ").append(tableModel.getValueAt(row, 5)).append("<br>");
        sb.append("Khách hàng ID: ").append(tableModel.getValueAt(row, 6)).append("</html>");
        JOptionPane.showMessageDialog(this, sb.toString(),
                "Chi tiết thu hoạch", JOptionPane.PLAIN_MESSAGE);
    }

    private Color[] gradeColor(String grade) {
        return switch (grade.toUpperCase()) {
            case "A" -> new Color[]{new Color(0xDCFCE7), new Color(0x166534)};
            case "B" -> new Color[]{new Color(0xEFF6FF), new Color(0x1E40AF)};
            case "C" -> new Color[]{new Color(0xFEF9C3), new Color(0x854D0E)};
            default  -> new Color[]{new Color(0xF3F4F6), AppTheme.TEXT_SECONDARY};
        };
    }

    void refreshTable(String lotCode) {
        tableModel.setRowCount(0);
        try {
            List<HarvestRecordDTO> list = (lotCode == null || lotCode.isEmpty())
                    ? ctrl.getAllHarvestRecords()
                    : ctrl.findByLotCode(lotCode);
            for (HarvestRecordDTO h : list) {
                tableModel.addRow(new Object[]{
                    h.getId(), h.getLotId(),
                    h.getHarvestDate() == null ? "" : sdf.format(h.getHarvestDate()),
                    h.getYieldKg(), h.getQualityGradeCode(),
                    h.getEmployeeId(), h.getCustomerId(), "edit|delete"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Loi: " + ex.getMessage());
        }
    }

    void openDialog(HarvestRecordDTO existing) {
        boolean isEdit = existing != null;
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                isEdit ? "Sua ban ghi thu hoach" : "Ghi thu hoach moi",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(420, 360);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 12));
        form.setBackground(AppTheme.BG_CARD);
        form.setBorder(new EmptyBorder(24, 24, 16, 24));

        JTextField txtLot   = UiUtils.addFormField(form, "Lo ID *");
        JTextField txtDate  = UiUtils.addFormField(form, "Ngay thu hoach (dd/MM/yyyy) *");
        JTextField txtYield = UiUtils.addFormField(form, "Nang suat (kg)");
        JTextField txtEmp   = UiUtils.addFormField(form, "Nhan vien ID");
        JTextField txtCust  = UiUtils.addFormField(form, "Khach hang ID");

        JLabel lblGrade = new JLabel("Chat luong");
        lblGrade.setFont(AppTheme.FONT_BODY);
        lblGrade.setForeground(AppTheme.TEXT_SECONDARY);
        JComboBox<String> cboGrade = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        cboGrade.setFont(AppTheme.FONT_BODY);
        form.add(lblGrade);
        form.add(cboGrade);

        if (isEdit) {
            txtLot.setText(existing.getLotId() == null ? "" : existing.getLotId().toString());
            txtDate.setText(existing.getHarvestDate() == null ? "" : sdf.format(existing.getHarvestDate()));
            txtYield.setText(existing.getYieldKg() == null ? "" : existing.getYieldKg().toString());
            txtEmp.setText(existing.getEmployeeId() == null ? "" : existing.getEmployeeId().toString());
            txtCust.setText(existing.getCustomerId() == null ? "" : existing.getCustomerId().toString());
            if (existing.getQualityGradeCode() != null) cboGrade.setSelectedItem(existing.getQualityGradeCode());
        }

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnBar.setBackground(AppTheme.BG_CARD);
        JButton btnCancel = UiUtils.createSecondaryButton("Huy");
        JButton btnSave   = UiUtils.createPrimaryButton("Luu");

        btnCancel.addActionListener(e -> dlg.dispose());
        btnSave.addActionListener(e -> {
            try {
                HarvestRecordDTO dto = isEdit ? existing : new HarvestRecordDTO();
                dto.setLotId(Long.parseLong(txtLot.getText().trim()));
                dto.setHarvestDate(sdf.parse(txtDate.getText().trim()));
                dto.setYieldKg(txtYield.getText().trim().isEmpty() ? null
                        : Double.parseDouble(txtYield.getText().trim()));
                dto.setQualityGradeCode((String) cboGrade.getSelectedItem());
                dto.setEmployeeId(txtEmp.getText().trim().isEmpty() ? null
                        : Long.parseLong(txtEmp.getText().trim()));
                dto.setCustomerId(txtCust.getText().trim().isEmpty() ? null
                        : Long.parseLong(txtCust.getText().trim()));

                if (isEdit) ctrl.updateHarvestRecord(dto);
                else        ctrl.createHarvestRecord(dto);

                dlg.dispose();
                refreshTable(null);
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

    class HarvestActionEditor extends DefaultCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        private final JButton btnEdit   = UiUtils.createSecondaryButton("Sửa");
        private final JButton btnDelete = UiUtils.createDangerButton("Xóa");
        private int currentRow;

        HarvestActionEditor(JTable t) {
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
                    HarvestRecordDTO dto = new HarvestRecordDTO();
                    dto.setId(Long.parseLong(tableModel.getValueAt(currentRow, 0).toString()));
                    openDialog(dto);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(HarvestRecordView.this, "Loi: " + ex.getMessage());
                }
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                int cf = JOptionPane.showConfirmDialog(HarvestRecordView.this, "Xoa ban ghi nay?",
                        "Xac nhan", JOptionPane.YES_NO_OPTION);
                if (cf == JOptionPane.YES_OPTION) {
                    try {
                        ctrl.deleteHarvestRecord(Long.parseLong(
                                tableModel.getValueAt(currentRow, 0).toString()));
                        refreshTable(null);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(HarvestRecordView.this, "Loi: " + ex.getMessage());
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
