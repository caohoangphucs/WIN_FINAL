package com.example.winfinal.view;

import com.example.winfinal.controller.FarmController;
import com.example.winfinal.dto.FarmDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Farm management screen (CRUD).
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

        JLabel title = UiUtils.createSectionTitle("Quan ly Trang Trai");

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        searchField = UiUtils.createSearchField("Tim theo ten, dia chi...");
        JButton btnSearch = UiUtils.createSecondaryButton("Tim");
        JButton btnAdd    = UiUtils.createPrimaryButton("+ Them trang trai");

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
        String[] cols = {"ID", "Ma trang trai", "Ten trang trai", "Dia chi",
                         "Dien tich (ha)", "Chu so huu", "So dien thoai", "Thao tac"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
            @Override public Class<?> getColumnClass(int c) { return String.class; }
        };
        table = new JTable(tableModel);
        UiUtils.styleTable(table);

        // Row hover & click
        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                if (col == 7) return; // action column handled by editor
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;
                try {
                    Long id   = Long.parseLong(tableModel.getValueAt(row, 0).toString());
                    String nm = tableModel.getValueAt(row, 2).toString();
                    showFarmSummary(id, nm);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FarmView.this, "Lỗi: " + ex.getMessage());
                }
            }
        });
        // Hover highlight
        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                table.setSelectionBackground(row >= 0 ? new Color(0xD8F3DC) : AppTheme.BG_CARD);
            }
        });

        table.getColumn("Thao tac").setCellRenderer(new ActionRenderer());
        table.getColumn("Thao tac").setCellEditor(new ActionEditor(table));
        table.getColumn("Thao tac").setMinWidth(200);
        table.getColumn("Thao tac").setMaxWidth(220);
        table.getColumn("Thao tac").setPreferredWidth(220);
        table.getColumn("Thao tac").setResizable(false);

        table.getColumn("ID").setMinWidth(45);
        table.getColumn("ID").setMaxWidth(45);
        table.getColumn("ID").setPreferredWidth(45);

        table.getColumn("Ma trang trai").setMinWidth(110);
        table.getColumn("Ma trang trai").setMaxWidth(110);
        table.getColumn("Ma trang trai").setPreferredWidth(110);

        table.getColumn("Dien tich (ha)").setMinWidth(110);
        table.getColumn("Dien tich (ha)").setMaxWidth(110);
        table.getColumn("Dien tich (ha)").setPreferredWidth(110);

        table.getColumn("So dien thoai").setMinWidth(120);
        table.getColumn("So dien thoai").setMaxWidth(120);
        table.getColumn("So dien thoai").setPreferredWidth(120);
        
        table.getColumn("Ten trang trai").setMaxWidth(160);
        table.getColumn("Dia chi").setMaxWidth(160);
        table.getColumn("Chu so huu").setMaxWidth(160);

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
            JOptionPane.showMessageDialog(this, "Loi tai du lieu: " + ex.getMessage());
        }
    }

    // ── Add/Edit Dialog ───────────────────────────────────────

    void openDialog(FarmDTO existing) {
        boolean isEdit = (existing != null);
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                isEdit ? "Sua trang trai" : "Them trang trai",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(420, 400);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(0, 0));

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 12));
        form.setBackground(AppTheme.BG_CARD);
        form.setBorder(new EmptyBorder(24, 24, 16, 24));

        JTextField txtCode  = UiUtils.addFormField(form, "Ma trang trai *");
        JTextField txtName  = UiUtils.addFormField(form, "Ten trang trai *");
        JTextField txtAddr  = UiUtils.addFormField(form, "Dia chi");
        JTextField txtArea  = UiUtils.addFormField(form, "Dien tich (ha)");
        JTextField txtOwner = UiUtils.addFormField(form, "Chu so huu");
        JTextField txtPhone = UiUtils.addFormField(form, "So dien thoai");
        form.add(new JLabel());

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
        JButton btnCancel = UiUtils.createSecondaryButton("Huy");
        JButton btnSave   = UiUtils.createPrimaryButton("Luu");

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
                        isEdit ? "Cap nhat thanh cong!" : "Them trang trai thanh cong!",
                        "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dlg, "Dien tich phai la so thuc.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Loi: " + ex.getMessage());
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
                "Ban chac chan muon xoa trang trai nay?",
                "Xac nhan xoa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            try {
                farmController.deleteFarm(id);
                refreshTable(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Loi xoa: " + ex.getMessage());
            }
        }
    }

    // ── Inner: Action column renderer ─────────────────────────

    public static class ActionRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JButton btnEdit   = UiUtils.createSecondaryButton("Sửa");
        private final JButton btnDelete = UiUtils.createDangerButton("Xóa");

        public ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 4));
            setOpaque(true);
            btnEdit.setPreferredSize(new Dimension(80, 28));
            btnDelete.setPreferredSize(new Dimension(80, 28));
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
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        private final JButton btnEdit   = UiUtils.createSecondaryButton("Sửa");
        private final JButton btnDelete = UiUtils.createDangerButton("Xóa");
        private int currentRow;

        ActionEditor(JTable table) {
            super(new JCheckBox());
            panel.setOpaque(true);
            panel.setBackground(AppTheme.BG_CARD);
            btnEdit.setPreferredSize(new Dimension(80, 28));
            btnDelete.setPreferredSize(new Dimension(80, 28));
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
                    JOptionPane.showMessageDialog(FarmView.this, "Loi: " + ex.getMessage());
                }
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                try {
                    Long id = Long.parseLong(tableModel.getValueAt(currentRow, 0).toString());
                    deleteFarm(id);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FarmView.this, "Loi: " + ex.getMessage());
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

    private void showFarmSummary(Long farmId, String farmName) {
        List<Object[]> summary = farmController.getSeasonalSummary(farmId);
        if (summary == null || summary.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có dữ liệu sản xuất cho trang trại này.");
            return;
        }
        
        String[] cols = {"Mùa vụ", "Số lô", "Tổng D.Tích (m2)", "Tổng Sản Lượng (kg)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Object[] r : summary) {
            model.addRow(new Object[]{
                r[0] == null ? "N/A" : r[0],
                r[1] == null ? "0" : r[1],
                r[2] == null ? "0" : r[2],
                r[3] == null ? "0" : r[3]
            });
        }
        
        JTable tbl = new JTable(model);
        UiUtils.styleTable(tbl);
        JScrollPane sp = new JScrollPane(tbl);
        sp.setPreferredSize(new Dimension(500, 200));
        
        JOptionPane.showMessageDialog(this, sp, "Tóm tắt mùa vụ - " + farmName, JOptionPane.PLAIN_MESSAGE);
    }
}
