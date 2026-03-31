package com.example.winfinal.view;

import com.example.winfinal.controller.FarmController;
import com.example.winfinal.dto.FarmDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Farm management screen (CRUD) with styled table and Add/Edit dialog.
 */
public class FarmView extends JPanel {

    private final FarmController farmController = new FarmController();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public FarmView() {
        setLayout(new BorderLayout(0, 16));
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);

        refreshTable(null);
    }

    // ── Header ────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel title = UiUtils.createSectionTitle("🏡  Quản lý Trang Trại");

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        searchField = UiUtils.createSearchField("🔍  Tìm theo tên, địa chỉ...");
        JButton btnSearch = UiUtils.createSecondaryButton("Tìm");
        JButton btnAdd    = UiUtils.createPrimaryButton("＋  Thêm trang trại");

        btnSearch.addActionListener(e -> refreshTable(searchField.getText().trim()));
        searchField.addActionListener(e -> refreshTable(searchField.getText().trim()));
        btnAdd.addActionListener(e -> openDialog(null));

        right.add(searchField);
        right.add(btnSearch);
        right.add(btnAdd);

        p.add(title, BorderLayout.WEST);
        p.add(right,  BorderLayout.EAST);
        return p;
    }

    // ── Table ─────────────────────────────────────────────────

    private JPanel buildTable() {
        String[] cols = {"ID", "Mã trang trại", "Tên trang trại", "Địa chỉ",
                         "Diện tích (ha)", "Chủ sở hữu", "Số điện thoại", "Thao tác"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
            @Override public Class<?> getColumnClass(int c) { return String.class; }
        };
        table = new JTable(tableModel);
        UiUtils.styleTable(table);

        // Action column
        table.getColumn("Thao tác").setCellRenderer(new ActionRenderer());
        table.getColumn("Thao tác").setCellEditor(new ActionEditor(table));
        table.getColumn("Thao tác").setPreferredWidth(120);
        table.getColumn("ID").setPreferredWidth(50);
        table.getColumn("Mã trang trại").setPreferredWidth(100);
        table.getColumn("Diện tích (ha)").setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1, true));
        scroll.getViewport().setBackground(AppTheme.BG_CARD);

        JPanel card = UiUtils.createCard();
        card.setLayout(new BorderLayout());
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ── Data ──────────────────────────────────────────────────

    void refreshTable(String keyword) {
        tableModel.setRowCount(0);
        try {
            List<FarmDTO> farms = farmController.getAllFarms();
            for (FarmDTO f : farms) {
                String name = f.getName() == null ? "" : f.getName();
                String addr = f.getAddress() == null ? "" : f.getAddress();
                if (keyword == null || keyword.isEmpty()
                        || name.toLowerCase().contains(keyword.toLowerCase())
                        || addr.toLowerCase().contains(keyword.toLowerCase())) {
                    tableModel.addRow(new Object[]{
                        f.getId(), f.getFarmCode(), f.getName(), f.getAddress(),
                        f.getTotalArea(), f.getOwnerName(), f.getPhone(), "edit|delete"
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    // ── Add/Edit Dialog ───────────────────────────────────────

    void openDialog(FarmDTO existing) {
        boolean isEdit = (existing != null);
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                isEdit ? "✏️  Sửa trang trại" : "➕  Thêm trang trại",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(420, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(0, 0));

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 12));
        form.setBackground(AppTheme.BG_CARD);
        form.setBorder(new EmptyBorder(24, 24, 16, 24));

        JTextField txtCode  = UiUtils.addFormField(form, "Mã trang trại *");
        JTextField txtName  = UiUtils.addFormField(form, "Tên trang trại *");
        JTextField txtAddr  = UiUtils.addFormField(form, "Địa chỉ");
        JTextField txtArea  = UiUtils.addFormField(form, "Diện tích (ha)");
        JTextField txtOwner = UiUtils.addFormField(form, "Chủ sở hữu");
        JTextField txtPhone = UiUtils.addFormField(form, "Số điện thoại");
        form.add(new JLabel()); // spacer

        if (isEdit) {
            txtCode.setText(existing.getFarmCode());
            txtName.setText(existing.getName());
            txtAddr.setText(existing.getAddress());
            txtArea.setText(existing.getTotalArea() == null ? "" : existing.getTotalArea().toString());
            txtOwner.setText(existing.getOwnerName());
            txtPhone.setText(existing.getPhone());
        }

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnBar.setBackground(AppTheme.BG_CARD);
        JButton btnCancel = UiUtils.createSecondaryButton("Hủy");
        JButton btnSave   = UiUtils.createPrimaryButton("💾  Lưu");

        btnCancel.addActionListener(e -> dlg.dispose());
        btnSave.addActionListener(e -> {
            try {
                FarmDTO dto = isEdit ? existing : new FarmDTO();
                dto.setFarmCode(txtCode.getText().trim());
                dto.setName(txtName.getText().trim());
                dto.setAddress(txtAddr.getText().trim());
                String areaText = txtArea.getText().trim();
                dto.setTotalArea(areaText.isEmpty() ? null : Double.parseDouble(areaText));
                dto.setOwnerName(txtOwner.getText().trim());
                dto.setPhone(txtPhone.getText().trim());

                if (isEdit) farmController.updateFarm(dto);
                else        farmController.createFarm(dto);

                dlg.dispose();
                refreshTable(null);
                JOptionPane.showMessageDialog(this,
                        isEdit ? "Cập nhật thành công!" : "Thêm trang trại thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dlg, "Diện tích phải là số thực.");
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

    void deleteFarm(long id) {
        int r = JOptionPane.showConfirmDialog(this,
                "Bạn chắc chắn muốn xóa trang trại này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            try {
                farmController.deleteFarm(id);
                refreshTable(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa: " + ex.getMessage());
            }
        }
    }

    // ── Inner: Action column renderer ─────────────────────────

    static class ActionRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JButton btnEdit   = UiUtils.createSecondaryButton("✏️ Sửa");
        private final JButton btnDelete = UiUtils.createDangerButton("🗑 Xóa");

        ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
            setOpaque(true);
            add(btnEdit);
            add(btnDelete);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean s, boolean f, int r, int c) {
            setBackground(r % 2 == 0 ? AppTheme.BG_CARD : AppTheme.BG_TABLE_ROW_ALT);
            return this;
        }
    }

    class ActionEditor extends DefaultCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        private final JButton btnEdit   = UiUtils.createSecondaryButton("✏️ Sửa");
        private final JButton btnDelete = UiUtils.createDangerButton("🗑 Xóa");
        private JTable sourceTable;
        private int currentRow;

        ActionEditor(JTable table) {
            super(new JCheckBox());
            this.sourceTable = table;
            panel.setOpaque(true);
            panel.setBackground(AppTheme.BG_CARD);
            panel.add(btnEdit);
            panel.add(btnDelete);

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                int row = currentRow;
                try {
                    Long id = Long.parseLong(tableModel.getValueAt(row, 0).toString());
                    FarmDTO dto = new FarmDTO();
                    dto.setId(id);
                    dto.setFarmCode(tableModel.getValueAt(row, 1).toString());
                    dto.setName(tableModel.getValueAt(row, 2).toString());
                    dto.setAddress(tableModel.getValueAt(row, 3).toString());
                    Object area = tableModel.getValueAt(row, 4);
                    dto.setTotalArea(area == null ? null : Double.parseDouble(area.toString()));
                    dto.setOwnerName(tableModel.getValueAt(row, 5).toString());
                    dto.setPhone(tableModel.getValueAt(row, 6).toString());
                    openDialog(dto);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FarmView.this, "Lỗi: " + ex.getMessage());
                }
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                try {
                    Long id = Long.parseLong(tableModel.getValueAt(currentRow, 0).toString());
                    deleteFarm(id);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FarmView.this, "Lỗi: " + ex.getMessage());
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v,
                boolean s, int row, int col) {
            currentRow = row;
            return panel;
        }

        @Override public Object getCellEditorValue() { return "edit|delete"; }
    }
}
