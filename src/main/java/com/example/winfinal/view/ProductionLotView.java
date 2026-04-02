package com.example.winfinal.view;

import com.example.winfinal.controller.ProductionLotController;
import com.example.winfinal.dto.ProductionLotDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Production Lot – matches reference: filter bar + table with alert row highlighting.
 */
public class ProductionLotView extends JPanel {

    private final ProductionLotController ctrl = new ProductionLotController();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<FilterItem> cboFarm, cboStatus, cboCrop, cboSeason;

    static class FilterItem {
        Long numId;  String strId;  String name;
        public FilterItem(Long numId, String name) { this.numId = numId; this.name = name; }
        public FilterItem(String strId, String name) { this.strId = strId; this.name = name; }
        @Override public String toString() { return name == null ? "" : name; }
    }

    public static String translateStatus(String status) {
        if (status == null) return "";
        return switch (status) {
            case "GROWING"    -> "Đang sinh trưởng";
            case "PLANNING"   -> "Lên kế hoạch";
            case "PLANTED"    -> "Đã gieo trồng";
            case "FLOWERING"  -> "Ra hoa";
            case "HARVESTING" -> "Đang thu hoạch";
            case "HARVESTED"  -> "Đã thu hoạch";
            case "DONE"       -> "Hoàn thành";
            case "CANCELLED"  -> "Đã hủy";
            case "IDLE"       -> "Tạm ngưng";
            default -> status;
        };
    }

    private void loadFilters() {
        jakarta.persistence.EntityManager em = null;
        try {
            em = com.example.winfinal.dao.BaseDAO.getEntityManagerFactory().createEntityManager();
            
            FilterItem selFarm = (FilterItem) cboFarm.getSelectedItem();
            FilterItem selStatus = (FilterItem) cboStatus.getSelectedItem();
            FilterItem selCrop = (FilterItem) cboCrop.getSelectedItem();
            FilterItem selSeason = (FilterItem) cboSeason.getSelectedItem();

            cboFarm.removeAllItems(); cboFarm.addItem(new FilterItem((Long)null, "Tất cả trang trại"));
            List<Object[]> farms = em.createQuery("SELECT DISTINCT l.farm.id, l.farm.name FROM ProductionLot l WHERE l.farm IS NOT NULL", Object[].class).getResultList();
            for (Object[] r : farms) { FilterItem fi = new FilterItem((Long)r[0], (String)r[1]); cboFarm.addItem(fi); if(selFarm!=null && fi.numId.equals(selFarm.numId)) cboFarm.setSelectedItem(fi); }

            cboStatus.removeAllItems(); cboStatus.addItem(new FilterItem((String)null, "Tất cả trạng thái"));
            List<String> statuses = em.createQuery("SELECT DISTINCT l.status.code FROM ProductionLot l WHERE l.status IS NOT NULL", String.class).getResultList();
            for (String code : statuses) { FilterItem fi = new FilterItem(code, translateStatus(code)); cboStatus.addItem(fi); if(selStatus!=null && fi.strId.equals(selStatus.strId)) cboStatus.setSelectedItem(fi); }

            cboCrop.removeAllItems(); cboCrop.addItem(new FilterItem((Long)null, "Tất cả loại cây"));
            List<Object[]> crops = em.createQuery("SELECT DISTINCT l.cropType.id, l.cropType.name FROM ProductionLot l WHERE l.cropType IS NOT NULL", Object[].class).getResultList();
            for (Object[] r : crops) { FilterItem fi = new FilterItem((Long)r[0], (String)r[1]); cboCrop.addItem(fi); if(selCrop!=null && fi.numId.equals(selCrop.numId)) cboCrop.setSelectedItem(fi); }

            cboSeason.removeAllItems(); cboSeason.addItem(new FilterItem((Long)null, "Tất cả mùa vụ"));
            List<Object[]> seasons = em.createQuery("SELECT DISTINCT l.season.id, l.season.name FROM ProductionLot l WHERE l.season IS NOT NULL", Object[].class).getResultList();
            for (Object[] r : seasons) { FilterItem fi = new FilterItem((Long)r[0], (String)r[1]); cboSeason.addItem(fi); if(selSeason!=null && fi.numId.equals(selSeason.numId)) cboSeason.setSelectedItem(fi); }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }

    public ProductionLotView() {
        setLayout(new BorderLayout(0, 12));
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildTitleBar(),   BorderLayout.NORTH);
        add(buildFilterArea(), BorderLayout.CENTER);
    }

    // ── Title ─────────────────────────────────────────────────

    private JPanel buildTitleBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0,0,14,0));

        JLabel title = new JLabel("Quản lý Lô Sản Xuất");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Lưới quản lý trang trại và lô sản xuất");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(title);
        left.add(sub);

        p.add(left, BorderLayout.WEST);
        return p;
    }

    // ── Filter + table area ───────────────────────────────────

    private JPanel buildFilterArea() {
        JPanel wrap = new JPanel(new BorderLayout(0, 10));
        wrap.setOpaque(false);

        // Filter card - use centralized UiUtils for clean UI
        JPanel filterCard = UiUtils.createCard();
        filterCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 16);
        gbc.weighty = 1.0;

        cboFarm   = createStyledCombo();
        cboStatus = createStyledCombo();
        cboCrop   = createStyledCombo();
        cboSeason = createStyledCombo();
        loadFilters();

        JButton btnFilter = UiUtils.createPrimaryButton("Lọc");
        btnFilter.setPreferredSize(new Dimension(100, AppTheme.BUTTON_HEIGHT));
        btnFilter.addActionListener(e -> refreshTable());

        int col = 0;
        gbc.gridy = 0; gbc.weightx = 1.0;
        
        gbc.gridx = col++; filterCard.add(createFilterGroup("Trang trại", cboFarm), gbc);
        gbc.gridx = col++; filterCard.add(createFilterGroup("Trạng thái", cboStatus), gbc);
        gbc.gridx = col++; filterCard.add(createFilterGroup("Loại cây", cboCrop), gbc);
        gbc.gridx = col++; filterCard.add(createFilterGroup("Mùa vụ", cboSeason), gbc);

        // Add filter button aligned to the bottom of the combobox
        gbc.gridx = col++;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 8, 0, 0);
        gbc.anchor = GridBagConstraints.SOUTH;
        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setOpaque(false);
        btnWrap.setBorder(new EmptyBorder(22, 0, 0, 0)); // push down to align with combo boxes
        btnWrap.add(btnFilter, BorderLayout.SOUTH);
        filterCard.add(btnWrap, gbc);

        // Search row
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        searchRow.setPreferredSize(new Dimension(0, 38));
        txtSearch = UiUtils.createSearchField("Tìm theo mã lô...");
        txtSearch.setPreferredSize(new Dimension(240, 34));

        JButton btnSearch = UiUtils.createPrimaryButton("Tìm");
        btnSearch.addActionListener(e -> refreshTable());
        txtSearch.addActionListener(e -> refreshTable());

        JButton btnAdd = UiUtils.createSecondaryButton("+ Thêm lô");
        btnAdd.addActionListener(e -> openDialog(null));

        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchWrap.setOpaque(false);
        searchWrap.add(txtSearch);
        searchWrap.add(btnSearch);
        searchWrap.add(btnAdd);
        searchRow.add(searchWrap, BorderLayout.EAST);

        // Table card
        JPanel tableCard = UiUtils.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(buildTable(), BorderLayout.CENTER);

        wrap.add(filterCard, BorderLayout.NORTH);
        JPanel mid = new JPanel(new BorderLayout(0, 6));
        mid.setOpaque(false);
        mid.add(searchRow, BorderLayout.NORTH);
        mid.add(tableCard, BorderLayout.CENTER);
        wrap.add(mid, BorderLayout.CENTER);

        refreshTable();
        return wrap;
    }

    private JPanel createFilterGroup(String labelText, JComboBox<FilterItem> combo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        p.add(lbl, BorderLayout.NORTH);
        p.add(combo, BorderLayout.CENTER);
        return p;
    }

    private JComboBox<FilterItem> createStyledCombo() {
        JComboBox<FilterItem> cbo = new JComboBox<>();
        cbo.setFont(AppTheme.FONT_BODY);
        cbo.setBackground(Color.WHITE);
        cbo.setPreferredSize(new Dimension(150, AppTheme.BUTTON_HEIGHT));
        return cbo;
    }

    private JScrollPane buildTable() {
        String[] cols = {"Mã Lô", "Ngày Trồng", "Trạng Thái", "Diện Tích", "Thao Tác"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c==4; }
        };
        table = new JTable(tableModel);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(38);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(AppTheme.BORDER_LIGHT);
        table.setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(AppTheme.BG_TABLE_HEADER);
        table.getTableHeader().setForeground(AppTheme.TEXT_PRIMARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,2,0, AppTheme.PRIMARY));

        // Status column renderer
        table.getColumnModel().getColumn(2).setCellRenderer(new LotStatusRenderer());
        // Row highlight renderer for others
        table.setDefaultRenderer(Object.class, new LotRowRenderer());

        // Action column – Sửa + Xóa only
        table.getColumnModel().getColumn(4).setCellRenderer(new LotActionRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new LotActionEditor(table));
        table.getColumnModel().getColumn(4).setMinWidth(200);
        table.getColumnModel().getColumn(4).setMaxWidth(220);
        table.getColumnModel().getColumn(4).setPreferredWidth(220);
        table.getColumnModel().getColumn(4).setResizable(false);
        
        table.getColumnModel().getColumn(0).setMinWidth(110);
        table.getColumnModel().getColumn(0).setMaxWidth(110);
        table.getColumnModel().getColumn(0).setPreferredWidth(110);
        
        table.getColumnModel().getColumn(1).setMinWidth(110);
        table.getColumnModel().getColumn(1).setMaxWidth(110);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        
        table.getColumnModel().getColumn(3).setMinWidth(110);
        table.getColumnModel().getColumn(3).setMaxWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);

        // Row click → open detail
        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                if (col == 4) return; // action column handled by editor
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;
                showLotDetail(row);
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    /** Navigate to the new Traceability Detail page */
    private void showLotDetail(int row) {
        String lotCode = tableModel.getValueAt(row, 0) == null ? ""
                : tableModel.getValueAt(row, 0).toString();

        Window win = SwingUtilities.getWindowAncestor(this);
        if (win instanceof MainView main) {
            main.showPage(MainView.PAGE_TRACEABILITY);
            // We need to find the instance of TraceabilityDetailView inside MainView's contentPane
            // For simplicity, let's search for it if we don't have a direct reference.
            // Navigate and search
            // Actually, find the TraceabilityDetailView inside the contentPane card layout
            // MainView has a contentPane field, but it's private.
            // I'll make it public or find another way.
            // Let's assume MainView can expose its pages.
            main.getTraceabilityView().performSearch(lotCode);
        }
    }

    // ── Data ──────────────────────────────────────────────────

    void refreshTable() {
        tableModel.setRowCount(0);
        try {
            String kw = txtSearch.getText().trim().toLowerCase();
            
            FilterItem fFarm = (FilterItem) cboFarm.getSelectedItem();
            Long farmId = fFarm != null ? fFarm.numId : null;

            FilterItem fStatus = (FilterItem) cboStatus.getSelectedItem();
            String st = fStatus != null ? fStatus.strId : null;

            FilterItem fCrop = (FilterItem) cboCrop.getSelectedItem();
            Long cropId = fCrop != null ? fCrop.numId : null;

            FilterItem fSeason = (FilterItem) cboSeason.getSelectedItem();
            Long seasonId = fSeason != null ? fSeason.numId : null;

            // Sử dụng DAO query [1.8] Tìm kiếm động thay vì lọc bộ nhớ danh sách đầy đủ
            List<ProductionLotDTO> lots = ctrl.search(farmId, st, cropId, seasonId);

            for (ProductionLotDTO l : lots) {
                String code = l.getLotCode() == null ? "" : l.getLotCode();
                if (!kw.isEmpty() && !code.toLowerCase().contains(kw)) continue;

                tableModel.addRow(new Object[]{
                    l.getLotCode(),
                    l.getPlantDate() == null ? "" : sdf.format(l.getPlantDate()),
                    l.getStatusCode(),
                    l.getAreaM2() == null ? "" : l.getAreaM2() + " ha",
                    "view"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    void openDialog(ProductionLotDTO existing) {
        boolean isEdit = (existing != null);
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                isEdit ? "Sửa lô" : "Thêm lô sản xuất", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(440, 460);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(8, 2, 10, 12));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(24, 24, 16, 24));

        JTextField txtCode    = UiUtils.addFormField(form, "Mã lô *");
        JTextField txtArea    = UiUtils.addFormField(form, "Diện tích (ha)");
        JTextField txtLoc     = UiUtils.addFormField(form, "Vị trí");
        JTextField txtPlant   = UiUtils.addFormField(form, "Ngày trồng (dd/MM/yyyy)");
        JTextField txtHarvest = UiUtils.addFormField(form, "Thu hoạch dự kiến");
        JTextField txtFarm    = UiUtils.addFormField(form, "ID Trang trại");
        JTextField txtCrop    = UiUtils.addFormField(form, "ID Loại cây");

        JLabel lSt = new JLabel("Trạng thái");
        lSt.setFont(AppTheme.FONT_BODY); lSt.setForeground(AppTheme.TEXT_SECONDARY);
        JComboBox<FilterItem> cboSt = new JComboBox<>();
        cboSt.setFont(AppTheme.FONT_BODY);
        cboSt.addItem(new FilterItem("PLANNING", "Lên kế hoạch"));
        cboSt.addItem(new FilterItem("PLANTED", "Đã gieo trồng"));
        cboSt.addItem(new FilterItem("GROWING", "Đang sinh trưởng"));
        cboSt.addItem(new FilterItem("FLOWERING", "Ra hoa"));
        cboSt.addItem(new FilterItem("HARVESTING", "Đang thu hoạch"));
        cboSt.addItem(new FilterItem("HARVESTED", "Đã thu hoạch"));
        cboSt.addItem(new FilterItem("DONE", "Hoàn thành"));
        cboSt.addItem(new FilterItem("CANCELLED", "Đã hủy"));
        cboSt.addItem(new FilterItem("IDLE", "Tạm ngưng"));
        form.add(lSt); form.add(cboSt);

        if (isEdit && existing.getStatusCode() != null) {
            for (int i = 0; i < cboSt.getItemCount(); i++) {
                if (cboSt.getItemAt(i).strId.equals(existing.getStatusCode())) {
                    cboSt.setSelectedIndex(i);
                    break;
                }
            }
        }

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnBar.setBackground(Color.WHITE);
        JButton bCancel = UiUtils.createSecondaryButton("Hủy");
        JButton bSave   = UiUtils.createPrimaryButton("Lưu");
        bCancel.addActionListener(e -> dlg.dispose());
        bSave.addActionListener(e -> {
            try {
                ProductionLotDTO dto = isEdit ? existing : new ProductionLotDTO();
                dto.setLotCode(txtCode.getText().trim());
                dto.setAreaM2(txtArea.getText().trim().isEmpty() ? null : Double.parseDouble(txtArea.getText().trim()));
                dto.setLocationDesc(txtLoc.getText().trim());
                FilterItem curSt = (FilterItem) cboSt.getSelectedItem();
                dto.setStatusCode(curSt != null ? curSt.strId : "PLANNING");
                dto.setPlantDate(txtPlant.getText().trim().isEmpty() ? null : sdf.parse(txtPlant.getText().trim()));
                dto.setExpectedHarvestDate(txtHarvest.getText().trim().isEmpty() ? null : sdf.parse(txtHarvest.getText().trim()));
                dto.setFarmId(txtFarm.getText().trim().isEmpty() ? null : Long.parseLong(txtFarm.getText().trim()));
                dto.setCropTypeId(txtCrop.getText().trim().isEmpty() ? null : Long.parseLong(txtCrop.getText().trim()));
                if (isEdit) ctrl.updateLot(dto); else ctrl.createLot(dto);
                dlg.dispose();
                loadFilters();
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi lưu lô sản xuất: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnBar.add(bCancel); btnBar.add(bSave);
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnBar, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── Renderers ─────────────────────────────────────────────

    /** Highlights CANCELLED/HARVESTING rows in red, others normal */
    static class LotRowRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            String status = t.getModel().getValueAt(row, 2) == null ? "" : t.getModel().getValueAt(row, 2).toString();
            boolean isAlert = status.equals("CANCELLED");
            if (!sel) {
                c.setBackground(isAlert ? new Color(0xFFE4E6) : (row%2==0 ? Color.WHITE : AppTheme.BG_TABLE_ROW_ALT));
                c.setForeground(isAlert ? AppTheme.DANGER : AppTheme.TEXT_PRIMARY);
                if (isAlert) c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                else c.setFont(AppTheme.FONT_BODY);
            }
            c.setBorder(new EmptyBorder(0, 12, 0, 12));
            return c;
        }
    }

    /** Status column with colored text */
    static class LotStatusRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            String status = val == null ? "" : val.toString();
            String display = translateStatus(status);
            Color fg = switch (status) {
                case "GROWING"    -> new Color(0x16A34A);
                case "PLANNING"   -> new Color(0x2563EB);
                case "HARVESTING" -> new Color(0xD97706);
                case "DONE"       -> AppTheme.TEXT_SECONDARY;
                case "CANCELLED"  -> AppTheme.DANGER;
                default -> AppTheme.TEXT_PRIMARY;
            };
            c.setText(display);
            c.setForeground(sel ? Color.WHITE : fg);
            c.setFont(AppTheme.FONT_BODY);
            c.setBorder(new EmptyBorder(0, 12, 0, 12));
            String rowStatus = t.getModel().getValueAt(row, 2) == null ? "" : t.getModel().getValueAt(row, 2).toString();
            if (!sel) c.setBackground(rowStatus.equals("CANCELLED") ? new Color(0xFFE4E6)
                    : row%2==0 ? Color.WHITE : AppTheme.BG_TABLE_ROW_ALT);
            return c;
        }
    }

    static class LotActionRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnEdit   = UiUtils.createSecondaryButton("Sửa");
        private final JButton btnDelete = UiUtils.createDangerButton("Xóa");
        LotActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 4));
            setOpaque(true);
            btnEdit.setPreferredSize(new Dimension(80, 28));
            btnDelete.setPreferredSize(new Dimension(80, 28));
            add(btnEdit);
            add(btnDelete);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            String status = t.getModel().getValueAt(r, 2) == null ? "" : t.getModel().getValueAt(r, 2).toString();
            setBackground(status.equals("CANCELLED") ? new Color(0xFFE4E6) : r%2==0 ? Color.WHITE : AppTheme.BG_TABLE_ROW_ALT);
            return this;
        }
    }

    class LotActionEditor extends DefaultCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        private final JButton btnEdit   = UiUtils.createSecondaryButton("Sửa");
        private final JButton btnDelete = UiUtils.createDangerButton("Xóa");
        private int currentRow;

        LotActionEditor(JTable t) {
            super(new JCheckBox());
            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);
            btnEdit.setPreferredSize(new Dimension(80, 28));
            btnDelete.setPreferredSize(new Dimension(80, 28));
            panel.add(btnEdit);
            panel.add(btnDelete);

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                try {
                    String lotCode = tableModel.getValueAt(currentRow, 0) == null ? ""
                            : tableModel.getValueAt(currentRow, 0).toString();
                    ProductionLotDTO dto = ctrl.findByLotCode(lotCode);
                    if (dto == null) dto = new ProductionLotDTO();
                    dto.setLotCode(lotCode);
                    openDialog(dto);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ProductionLotView.this, "Lỗi: " + ex.getMessage());
                }
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                String lotCode = tableModel.getValueAt(currentRow, 0) == null ? ""
                        : tableModel.getValueAt(currentRow, 0).toString();
                int cf = JOptionPane.showConfirmDialog(ProductionLotView.this,
                        "Xóa lô " + lotCode + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (cf == JOptionPane.YES_OPTION) {
                    try {
                        ProductionLotDTO dto = ctrl.findByLotCode(lotCode);
                        if (dto != null && dto.getId() != null) {
                            ctrl.deleteLot(dto.getId());
                            loadFilters();
                            refreshTable();
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ProductionLotView.this, "Lỗi: " + ex.getMessage());
                    }
                }
            });
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            currentRow = r;
            return panel;
        }
        @Override public Object getCellEditorValue() { return "view"; }
    }

    // ── Data helpers (Removed duplicate UI helpers) ────────────────────────────────────────────

}
