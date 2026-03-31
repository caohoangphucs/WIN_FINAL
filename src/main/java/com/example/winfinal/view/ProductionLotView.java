package com.example.winfinal.view;

import com.example.winfinal.controller.ProductionLotController;
import com.example.winfinal.dto.ProductionLotDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Production Lot management: search/filter + add/edit dialog.
 */
public class ProductionLotView extends JPanel {

    private final ProductionLotController ctrl = new ProductionLotController();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cboStatus;

    public ProductionLotView() {
        setLayout(new BorderLayout(0, 16));
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);

        refreshTable();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(12, 8));
        p.setOpaque(false);

        JLabel title = UiUtils.createSectionTitle("Quan ly Lo San Xuat");

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);

        txtSearch = UiUtils.createSearchField("Ma lo, vi tri...");
        cboStatus = new JComboBox<>(new String[]{"Tat ca trang thai",
                "PLANNING", "GROWING", "HARVESTING", "DONE", "CANCELLED"});
        cboStatus.setFont(AppTheme.FONT_BODY);
        cboStatus.setPreferredSize(new Dimension(170, AppTheme.BUTTON_HEIGHT));

        JButton btnFilter = UiUtils.createSecondaryButton("Loc");
        JButton btnAdd    = UiUtils.createPrimaryButton("+ Them lo");

        btnFilter.addActionListener(e -> refreshTable());
        btnAdd.addActionListener(e -> openDialog(null));

        filterBar.add(txtSearch);
        filterBar.add(cboStatus);
        filterBar.add(btnFilter);
        filterBar.add(Box.createHorizontalStrut(6));
        filterBar.add(btnAdd);

        p.add(title,     BorderLayout.NORTH);
        p.add(filterBar, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildTable() {
        String[] cols = {"ID", "Ma lo", "Trang thai", "Dien tich (m2)",
                         "Ngay trong", "Du kien thu hoach", "Farm ID", "Thao tac"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
        };
        table = new JTable(tableModel);
        UiUtils.styleTable(table);
        styleStatusColumn();

        table.getColumn("Thao tac").setCellRenderer(new FarmView.ActionRenderer());
        table.getColumn("Thao tac").setCellEditor(new LotActionEditor(table));
        table.getColumn("Thao tac").setPreferredWidth(130);
        table.getColumn("ID").setPreferredWidth(50);
        table.getColumn("Ma lo").setPreferredWidth(130);
        table.getColumn("Farm ID").setPreferredWidth(70);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1, true));
        scroll.getViewport().setBackground(AppTheme.BG_CARD);

        JPanel card = UiUtils.createCard();
        card.setLayout(new BorderLayout());
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private void styleStatusColumn() {
        table.getColumnModel().getColumn(2).setCellRenderer((t, val, sel, foc, row, col) -> {
            String status = val == null ? "" : val.toString();
            Color bg = switch (status) {
                case "GROWING"    -> new Color(0xDCFCE7);
                case "PLANNING"   -> new Color(0xEFF6FF);
                case "HARVESTING" -> new Color(0xFEF9C3);
                case "DONE"       -> new Color(0xF3F4F6);
                case "CANCELLED"  -> new Color(0xFEE2E2);
                default           -> AppTheme.BG_CARD;
            };
            Color fg = switch (status) {
                case "GROWING"    -> new Color(0x166534);
                case "PLANNING"   -> new Color(0x1E40AF);
                case "HARVESTING" -> new Color(0x854D0E);
                case "DONE"       -> AppTheme.TEXT_SECONDARY;
                case "CANCELLED"  -> AppTheme.DANGER;
                default           -> AppTheme.TEXT_PRIMARY;
            };
            JLabel lbl = UiUtils.createBadge(status, bg, fg);
            lbl.setOpaque(true);
            lbl.setBackground(sel ? AppTheme.BG_TABLE_HEADER : AppTheme.BG_CARD);
            return lbl;
        });
    }

    void refreshTable() {
        tableModel.setRowCount(0);
        try {
            String keyword = txtSearch.getText().trim();
            String status  = (String) cboStatus.getSelectedItem();
            boolean filterStatus = status != null && !status.startsWith("Tat ca");

            List<ProductionLotDTO> lots = ctrl.getAllLots();
            for (ProductionLotDTO l : lots) {
                if (!keyword.isEmpty()) {
                    String code = l.getLotCode() == null ? "" : l.getLotCode();
                    String loc  = l.getLocationDesc() == null ? "" : l.getLocationDesc();
                    if (!code.toLowerCase().contains(keyword.toLowerCase())
                            && !loc.toLowerCase().contains(keyword.toLowerCase())) continue;
                }
                if (filterStatus && !status.equals(l.getStatusCode())) continue;

                tableModel.addRow(new Object[]{
                    l.getId(), l.getLotCode(), l.getStatusCode(), l.getAreaM2(),
                    l.getPlantDate() == null ? "" : sdf.format(l.getPlantDate()),
                    l.getExpectedHarvestDate() == null ? "" : sdf.format(l.getExpectedHarvestDate()),
                    l.getFarmId(), "edit|delete"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Loi: " + ex.getMessage());
        }
    }

    void openDialog(ProductionLotDTO existing) {
        boolean isEdit = (existing != null);
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                isEdit ? "Sua lo san xuat" : "Them lo san xuat",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(440, 480);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(9, 2, 10, 12));
        form.setBackground(AppTheme.BG_CARD);
        form.setBorder(new EmptyBorder(24, 24, 16, 24));

        JTextField txtCode    = UiUtils.addFormField(form, "Ma lo *");
        JTextField txtArea    = UiUtils.addFormField(form, "Dien tich (m2)");
        JTextField txtLoc     = UiUtils.addFormField(form, "Mo ta vi tri");
        JTextField txtPlant   = UiUtils.addFormField(form, "Ngay trong (dd/MM/yyyy)");
        JTextField txtHarvest = UiUtils.addFormField(form, "Ngay thu hoach du kien");
        JTextField txtFarm    = UiUtils.addFormField(form, "Farm ID");
        JTextField txtCrop    = UiUtils.addFormField(form, "Loai cay ID");
        JTextField txtMgr     = UiUtils.addFormField(form, "Manager ID");

        JLabel lblStatus = new JLabel("Trang thai");
        lblStatus.setFont(AppTheme.FONT_BODY);
        lblStatus.setForeground(AppTheme.TEXT_SECONDARY);
        JComboBox<String> cboSt = new JComboBox<>(
                new String[]{"PLANNING", "GROWING", "HARVESTING", "DONE", "CANCELLED"});
        cboSt.setFont(AppTheme.FONT_BODY);
        form.add(lblStatus);
        form.add(cboSt);

        if (isEdit) {
            txtCode.setText(existing.getLotCode());
            txtArea.setText(existing.getAreaM2() == null ? "" : existing.getAreaM2().toString());
            txtLoc.setText(existing.getLocationDesc());
            txtPlant.setText(existing.getPlantDate() == null ? "" : sdf.format(existing.getPlantDate()));
            txtHarvest.setText(existing.getExpectedHarvestDate() == null ? ""
                    : sdf.format(existing.getExpectedHarvestDate()));
            txtFarm.setText(existing.getFarmId() == null ? "" : existing.getFarmId().toString());
            txtCrop.setText(existing.getCropTypeId() == null ? "" : existing.getCropTypeId().toString());
            txtMgr.setText(existing.getManagerId() == null ? "" : existing.getManagerId().toString());
            if (existing.getStatusCode() != null) cboSt.setSelectedItem(existing.getStatusCode());
        }

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnBar.setBackground(AppTheme.BG_CARD);
        JButton btnCancel = UiUtils.createSecondaryButton("Huy");
        JButton btnSave   = UiUtils.createPrimaryButton("Luu");

        btnCancel.addActionListener(e -> dlg.dispose());
        btnSave.addActionListener(e -> {
            try {
                ProductionLotDTO dto = isEdit ? existing : new ProductionLotDTO();
                dto.setLotCode(txtCode.getText().trim());
                dto.setAreaM2(txtArea.getText().trim().isEmpty() ? null
                        : Double.parseDouble(txtArea.getText().trim()));
                dto.setLocationDesc(txtLoc.getText().trim());
                dto.setStatusCode((String) cboSt.getSelectedItem());
                dto.setPlantDate(txtPlant.getText().trim().isEmpty() ? null
                        : sdf.parse(txtPlant.getText().trim()));
                dto.setExpectedHarvestDate(txtHarvest.getText().trim().isEmpty() ? null
                        : sdf.parse(txtHarvest.getText().trim()));
                dto.setFarmId(txtFarm.getText().trim().isEmpty() ? null
                        : Long.parseLong(txtFarm.getText().trim()));
                dto.setCropTypeId(txtCrop.getText().trim().isEmpty() ? null
                        : Long.parseLong(txtCrop.getText().trim()));
                dto.setManagerId(txtMgr.getText().trim().isEmpty() ? null
                        : Long.parseLong(txtMgr.getText().trim()));

                if (isEdit) ctrl.updateLot(dto);
                else        ctrl.createLot(dto);

                dlg.dispose();
                refreshTable();
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

    class LotActionEditor extends DefaultCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        private final JButton btnEdit   = UiUtils.createSecondaryButton("Sua");
        private final JButton btnDelete = UiUtils.createDangerButton("Xoa");
        private int currentRow;

        LotActionEditor(JTable t) {
            super(new JCheckBox());
            panel.setOpaque(true);
            panel.setBackground(AppTheme.BG_CARD);
            panel.add(btnEdit);
            panel.add(btnDelete);

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                try {
                    ProductionLotDTO dto = new ProductionLotDTO();
                    dto.setId(Long.parseLong(tableModel.getValueAt(currentRow, 0).toString()));
                    dto.setLotCode(tableModel.getValueAt(currentRow, 1).toString());
                    dto.setStatusCode(tableModel.getValueAt(currentRow, 2).toString());
                    Object area = tableModel.getValueAt(currentRow, 3);
                    dto.setAreaM2(area == null ? null : Double.parseDouble(area.toString()));
                    Object farm = tableModel.getValueAt(currentRow, 6);
                    dto.setFarmId(farm == null ? null : Long.parseLong(farm.toString()));
                    openDialog(dto);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ProductionLotView.this, "Loi: " + ex.getMessage());
                }
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                int confirm = JOptionPane.showConfirmDialog(ProductionLotView.this,
                        "Xoa lo nay?", "Xac nhan", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        ctrl.deleteLot(Long.parseLong(tableModel.getValueAt(currentRow, 0).toString()));
                        refreshTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ProductionLotView.this, "Loi: " + ex.getMessage());
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
