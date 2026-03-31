package com.example.winfinal.view;

import com.example.winfinal.controller.AgriSupplyController;
import com.example.winfinal.dto.AgriSupplyDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Agricultural Supply (Vật tư) management: CRUD + low-stock alert badge.
 */
public class AgriSupplyView extends JPanel {

    private final AgriSupplyController ctrl = new AgriSupplyController();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel lblLowStock;

    public AgriSupplyView() {
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

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);
        titleRow.add(UiUtils.createSectionTitle("📦  Quản lý Vật Tư Nông Nghiệp"));

        lblLowStock = UiUtils.createBadge("⚠️ 0 sắp hết", new Color(0xFEF3C7), new Color(0x92400E));
        titleRow.add(lblLowStock);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);
        searchField = UiUtils.createSearchField("🔍  Tên, mã vật tư...");
        JButton btnSearch = UiUtils.createSecondaryButton("Tìm");
        JButton btnShowLow= UiUtils.createSecondaryButton("⚠️ Tồn kho thấp");
        JButton btnAdd    = UiUtils.createPrimaryButton("＋  Thêm vật tư");

        btnSearch.addActionListener(e -> refreshTable(searchField.getText().trim()));
        searchField.addActionListener(e -> refreshTable(searchField.getText().trim()));
        btnShowLow.addActionListener(e -> showLowStock());
        btnAdd.addActionListener(e -> openDialog(null));

        filterBar.add(searchField);
        filterBar.add(btnSearch);
        filterBar.add(btnShowLow);
        filterBar.add(btnAdd);

        p.add(titleRow,   BorderLayout.NORTH);
        p.add(filterBar,  BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildTable() {
        String[] cols = {"ID", "Mã vật tư", "Tên vật tư", "Đơn vị",
                         "Tồn kho", "Tồn tối thiểu", "Tình trạng", "Thao tác"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
        };
        table = new JTable(tableModel);
        UiUtils.styleTable(table);

        // Stock status column
        table.getColumnModel().getColumn(6).setCellRenderer((t, val, sel, foc, row, col) -> {
            String status = val == null ? "" : val.toString();
            Color bg = status.equals("Thấp") ? new Color(0xFEE2E2) : new Color(0xDCFCE7);
            Color fg = status.equals("Thấp") ? AppTheme.DANGER : new Color(0x166534);
            JLabel lbl = UiUtils.createBadge(status, bg, fg);
            lbl.setOpaque(true);
            lbl.setBackground(sel ? AppTheme.BG_TABLE_HEADER : AppTheme.BG_CARD);
            return lbl;
        });

        table.getColumn("Thao tác").setCellRenderer(new FarmView.ActionRenderer());
        table.getColumn("Thao tác").setCellEditor(new SupplyActionEditor(table));
        table.getColumn("Thao tác").setPreferredWidth(130);
        table.getColumn("ID").setPreferredWidth(50);
        table.getColumn("Mã vật tư").setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1, true));
        scroll.getViewport().setBackground(AppTheme.BG_CARD);

        JPanel card = UiUtils.createCard();
        card.setLayout(new BorderLayout());
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    void refreshTable(String keyword) {
        tableModel.setRowCount(0);
        int lowCount = 0;
        try {
            List<AgriSupplyDTO> list = keyword == null || keyword.isEmpty()
                    ? ctrl.getAllAgriSupplies() : ctrl.search(keyword);
            for (AgriSupplyDTO s : list) {
                boolean isLow = s.getStockQty() != null && s.getMinStock() != null
                        && s.getStockQty() <= s.getMinStock();
                if (isLow) lowCount++;
                tableModel.addRow(new Object[]{
                    s.getId(), s.getSupplyCode(), s.getName(), s.getUnit(),
                    s.getStockQty(), s.getMinStock(),
                    isLow ? "Thấp" : "Đủ",
                    "edit|delete"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
        lblLowStock.setText("⚠️ " + lowCount + " sắp hết");
        lblLowStock.setVisible(lowCount > 0);
    }

    void showLowStock() {
        tableModel.setRowCount(0);
        try {
            List<AgriSupplyDTO> list = ctrl.getLowStockSupplies();
            for (AgriSupplyDTO s : list) {
                tableModel.addRow(new Object[]{
                    s.getId(), s.getSupplyCode(), s.getName(), s.getUnit(),
                    s.getStockQty(), s.getMinStock(), "Thấp", "edit|delete"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    void openDialog(AgriSupplyDTO existing) {
        boolean isEdit = existing != null;
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                isEdit ? "✏️  Sửa vật tư" : "➕  Thêm vật tư",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(420, 380);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 12));
        form.setBackground(AppTheme.BG_CARD);
        form.setBorder(new EmptyBorder(24, 24, 16, 24));

        JTextField txtCode    = UiUtils.addFormField(form, "Mã vật tư *");
        JTextField txtName    = UiUtils.addFormField(form, "Tên vật tư *");
        JTextField txtUnit    = UiUtils.addFormField(form, "Đơn vị");
        JTextField txtStock   = UiUtils.addFormField(form, "Tồn kho hiện tại");
        JTextField txtMin     = UiUtils.addFormField(form, "Tồn kho tối thiểu");
        JTextField txtCat     = UiUtils.addFormField(form, "Category ID");
        JTextField txtSup     = UiUtils.addFormField(form, "Supplier ID");

        if (isEdit) {
            txtCode.setText(existing.getSupplyCode());
            txtName.setText(existing.getName());
            txtUnit.setText(existing.getUnit());
            txtStock.setText(existing.getStockQty() == null ? "" : existing.getStockQty().toString());
            txtMin.setText(existing.getMinStock() == null ? "" : existing.getMinStock().toString());
            txtCat.setText(existing.getCategoryId() == null ? "" : existing.getCategoryId().toString());
            txtSup.setText(existing.getSupplierId() == null ? "" : existing.getSupplierId().toString());
        }

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnBar.setBackground(AppTheme.BG_CARD);
        JButton btnCancel = UiUtils.createSecondaryButton("Hủy");
        JButton btnSave   = UiUtils.createPrimaryButton("💾  Lưu");

        btnCancel.addActionListener(e -> dlg.dispose());
        btnSave.addActionListener(e -> {
            try {
                AgriSupplyDTO dto = isEdit ? existing : new AgriSupplyDTO();
                dto.setSupplyCode(txtCode.getText().trim());
                dto.setName(txtName.getText().trim());
                dto.setUnit(txtUnit.getText().trim());
                dto.setStockQty(txtStock.getText().trim().isEmpty() ? null
                        : Double.parseDouble(txtStock.getText().trim()));
                dto.setMinStock(txtMin.getText().trim().isEmpty() ? null
                        : Double.parseDouble(txtMin.getText().trim()));
                dto.setCategoryId(txtCat.getText().trim().isEmpty() ? null
                        : Long.parseLong(txtCat.getText().trim()));
                dto.setSupplierId(txtSup.getText().trim().isEmpty() ? null
                        : Long.parseLong(txtSup.getText().trim()));

                if (isEdit) ctrl.updateAgriSupply(dto);
                else        ctrl.createAgriSupply(dto);

                dlg.dispose();
                refreshTable(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi: " + ex.getMessage());
            }
        });

        btnBar.add(btnCancel);
        btnBar.add(btnSave);
        dlg.add(form,   BorderLayout.CENTER);
        dlg.add(btnBar, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    class SupplyActionEditor extends DefaultCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        private final JButton btnEdit   = UiUtils.createSecondaryButton("✏️ Sửa");
        private final JButton btnDelete = UiUtils.createDangerButton("🗑 Xóa");
        private int currentRow;

        SupplyActionEditor(JTable t) {
            super(new JCheckBox());
            panel.setOpaque(true);
            panel.setBackground(AppTheme.BG_CARD);
            panel.add(btnEdit);
            panel.add(btnDelete);

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                try {
                    AgriSupplyDTO dto = new AgriSupplyDTO();
                    dto.setId(Long.parseLong(tableModel.getValueAt(currentRow, 0).toString()));
                    dto.setSupplyCode(tableModel.getValueAt(currentRow, 1).toString());
                    dto.setName(tableModel.getValueAt(currentRow, 2).toString());
                    dto.setUnit(tableModel.getValueAt(currentRow, 3) == null ? null
                            : tableModel.getValueAt(currentRow, 3).toString());
                    Object stk = tableModel.getValueAt(currentRow, 4);
                    dto.setStockQty(stk == null ? null : Double.parseDouble(stk.toString()));
                    Object min = tableModel.getValueAt(currentRow, 5);
                    dto.setMinStock(min == null ? null : Double.parseDouble(min.toString()));
                    openDialog(dto);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AgriSupplyView.this, "Lỗi: " + ex.getMessage());
                }
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                int cf = JOptionPane.showConfirmDialog(AgriSupplyView.this, "Xóa vật tư này?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (cf == JOptionPane.YES_OPTION) {
                    try {
                        ctrl.deleteAgriSupply(Long.parseLong(tableModel.getValueAt(currentRow, 0).toString()));
                        refreshTable(null);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(AgriSupplyView.this, "Lỗi: " + ex.getMessage());
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
