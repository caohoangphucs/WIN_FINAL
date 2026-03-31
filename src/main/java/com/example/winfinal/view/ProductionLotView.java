package com.example.winfinal.view;

import com.example.winfinal.controller.ProductionLotController;
import com.example.winfinal.dto.ProductionLotDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
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
    private JComboBox<String> cboFarm, cboStatus, cboCrop, cboSeason;

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

        JLabel sub = new JLabel("Farm & Production Grid");
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

        // Filter card
        JPanel filterCard = makeCard();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));

        cboFarm   = makeCombo("Trang trại", new String[]{"Trang trại A","Trang trại B","Trang trại C"});
        cboStatus = makeCombo("Trạng thái", new String[]{"Tất cả trạng thái","PLANNING","GROWING","HARVESTING","DONE","CANCELLED"});
        cboCrop   = makeCombo("Loại cây",   new String[]{"Tất cả loại cây","Rau","Lúa","Trái Cây","Hoa"});
        cboSeason = makeCombo("Mùa vụ",     new String[]{"Tất cả mùa vụ","Xuân","Hạ","Thu","Đông"});
        JButton btnFilter = makePrimaryBtn("Lọc");

        filterCard.add(makeComboLabel("Trang trại:")); filterCard.add(cboFarm);
        filterCard.add(makeComboLabel("Trạng thái:")); filterCard.add(cboStatus);
        filterCard.add(makeComboLabel("Loại cây:"));  filterCard.add(cboCrop);
        filterCard.add(makeComboLabel("Mùa vụ."));    filterCard.add(cboSeason);
        filterCard.add(btnFilter);

        btnFilter.addActionListener(e -> refreshTable());

        // Search row
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        searchRow.setPreferredSize(new Dimension(0, 38));
        txtSearch = UiUtils.createSearchField("Tìm theo mã lô...");
        txtSearch.setPreferredSize(new Dimension(240, 34));

        JButton btnSearch = makePrimaryBtn("Tìm");
        btnSearch.addActionListener(e -> refreshTable());
        txtSearch.addActionListener(e -> refreshTable());

        JButton btnAdd = makeOutlineBtn("+ Thêm lô");
        btnAdd.addActionListener(e -> openDialog(null));

        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchWrap.setOpaque(false);
        searchWrap.add(txtSearch);
        searchWrap.add(btnSearch);
        searchWrap.add(btnAdd);
        searchRow.add(searchWrap, BorderLayout.EAST);

        // Table card
        JPanel tableCard = makeCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(buildTable(), BorderLayout.CENTER);

        wrap.add(filterCard,  BorderLayout.NORTH);
        JPanel mid = new JPanel(new BorderLayout(0,6));
        mid.setOpaque(false);
        mid.add(searchRow, BorderLayout.NORTH);
        mid.add(tableCard, BorderLayout.CENTER);
        wrap.add(mid, BorderLayout.CENTER);

        refreshTable();
        return wrap;
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

        // Action column
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionBtnRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new LotActionEditor(table));
        table.getColumnModel().getColumn(4).setPreferredWidth(170);
        table.getColumnModel().getColumn(0).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ── Data ──────────────────────────────────────────────────

    void refreshTable() {
        tableModel.setRowCount(0);
        try {
            String kw = txtSearch.getText().trim().toLowerCase();
            String st = (String) cboStatus.getSelectedItem();
            if (st != null && st.startsWith("Tất cả")) st = null;

            // Sử dụng DAO query [1.8] Tìm kiếm động thay vì lọc bộ nhớ danh sách đầy đủ
            List<ProductionLotDTO> lots = ctrl.search(null, st, null, null);

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
        JTextField txtFarm    = UiUtils.addFormField(form, "Farm ID");
        JTextField txtCrop    = UiUtils.addFormField(form, "Loại cây ID");

        JLabel lSt = new JLabel("Trạng thái");
        lSt.setFont(AppTheme.FONT_BODY); lSt.setForeground(AppTheme.TEXT_SECONDARY);
        JComboBox<String> cboSt = new JComboBox<>(new String[]{"PLANNING","GROWING","HARVESTING","DONE","CANCELLED"});
        cboSt.setFont(AppTheme.FONT_BODY);
        form.add(lSt); form.add(cboSt);

        if (isEdit && existing.getStatusCode()!=null) cboSt.setSelectedItem(existing.getStatusCode());

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnBar.setBackground(Color.WHITE);
        JButton bCancel = UiUtils.createSecondaryButton("Hủy");
        JButton bSave   = makePrimaryBtn("Lưu");
        bCancel.addActionListener(e -> dlg.dispose());
        bSave.addActionListener(e -> {
            try {
                ProductionLotDTO dto = isEdit ? existing : new ProductionLotDTO();
                dto.setLotCode(txtCode.getText().trim());
                dto.setAreaM2(txtArea.getText().trim().isEmpty() ? null : Double.parseDouble(txtArea.getText().trim()));
                dto.setLocationDesc(txtLoc.getText().trim());
                dto.setStatusCode((String) cboSt.getSelectedItem());
                dto.setPlantDate(txtPlant.getText().trim().isEmpty() ? null : sdf.parse(txtPlant.getText().trim()));
                dto.setExpectedHarvestDate(txtHarvest.getText().trim().isEmpty() ? null : sdf.parse(txtHarvest.getText().trim()));
                dto.setFarmId(txtFarm.getText().trim().isEmpty() ? null : Long.parseLong(txtFarm.getText().trim()));
                dto.setCropTypeId(txtCrop.getText().trim().isEmpty() ? null : Long.parseLong(txtCrop.getText().trim()));
                if (isEdit) ctrl.updateLot(dto); else ctrl.createLot(dto);
                dlg.dispose(); refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi: " + ex.getMessage());
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
            String display = switch (status) {
                case "GROWING"    -> "Đang Sinh Trưởng";
                case "PLANNING"   -> "Đã Trồng";
                case "HARVESTING" -> "Đang Thu Hoạch";
                case "DONE"       -> "Đã Thu Hoạch";
                case "CANCELLED"  -> "Sâu Bệnh";
                default -> status;
            };
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

    static class ActionBtnRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnTrace = new JButton("Truy xuất");
        private final JButton btnStats = new JButton("Thống kê");
        ActionBtnRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 0));
            setOpaque(true);
            
            btnTrace.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnTrace.setForeground(Color.WHITE);
            btnTrace.setBackground(new Color(0x2563EB));
            btnTrace.setBorderPainted(false); btnTrace.setFocusPainted(false);

            btnStats.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnStats.setForeground(Color.WHITE);
            btnStats.setBackground(new Color(0x059669));
            btnStats.setBorderPainted(false); btnStats.setFocusPainted(false);

            add(btnTrace);
            add(btnStats);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            String status = t.getModel().getValueAt(r, 2) == null ? "" : t.getModel().getValueAt(r, 2).toString();
            setBackground(status.equals("CANCELLED") ? new Color(0xFFE4E6) : r%2==0 ? Color.WHITE : AppTheme.BG_TABLE_ROW_ALT);
            return this;
        }
    }

    class LotActionEditor extends DefaultCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        private final JButton btnTrace = new JButton("Truy xuất");
        private final JButton btnStats = new JButton("Thống kê");
        private int currentRow;

        LotActionEditor(JTable t) {
            super(new JCheckBox());
            
            btnTrace.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnTrace.setForeground(Color.WHITE);
            btnTrace.setBackground(new Color(0x2563EB));
            btnTrace.setBorderPainted(false); btnTrace.setFocusPainted(false);
            
            btnStats.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnStats.setForeground(Color.WHITE);
            btnStats.setBackground(new Color(0x059669));
            btnStats.setBorderPainted(false); btnStats.setFocusPainted(false);

            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);
            panel.add(btnTrace);
            panel.add(btnStats);

            btnTrace.addActionListener(e -> {
                fireEditingStopped();
                String lotCode = tableModel.getValueAt(currentRow, 0) == null ? ""
                        : tableModel.getValueAt(currentRow, 0).toString();
                
                com.example.winfinal.controller.CultivationLogController logCtrl = new com.example.winfinal.controller.CultivationLogController();
                List<Object[]> logs = logCtrl.getTraceabilityLogs(lotCode);
                
                if (logs == null || logs.isEmpty()) {
                    JOptionPane.showMessageDialog(ProductionLotView.this, "Chưa có nhật ký canh tác cho lô này.", "Nhật ký", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                String[] cols = {"Thời gian", "Hoạt động", "Vật tư", "Liều lượng", "Người thực hiện"};
                DefaultTableModel m = new DefaultTableModel(cols, 0);
                for (Object[] r : logs) {
                    m.addRow(new Object[]{
                        r[0] == null ? "" : r[0].toString(),
                        r[1], r[2], r[3], r[4]
                    });
                }
                JTable logTbl = new JTable(m);
                UiUtils.styleTable(logTbl);
                logTbl.getColumnModel().getColumn(0).setPreferredWidth(140);
                JScrollPane sp = new JScrollPane(logTbl);
                sp.setPreferredSize(new Dimension(600, 300));
                
                JOptionPane.showMessageDialog(ProductionLotView.this, sp, "Nhật ký truy xuất nguồn gốc - Lô " + lotCode, JOptionPane.PLAIN_MESSAGE);
            });

            btnStats.addActionListener(e -> {
                fireEditingStopped();
                String lotCode = tableModel.getValueAt(currentRow, 0) == null ? "" : tableModel.getValueAt(currentRow, 0).toString();
                // Get Lot ID from controller based on code
                ProductionLotDTO currentLot = null;
                try { currentLot = ctrl.findByLotCode(lotCode); } catch(Exception ignored){}
                if (currentLot == null || currentLot.getId() == null) return;
                
                com.example.winfinal.controller.CultivationLogController logCtrl = new com.example.winfinal.controller.CultivationLogController();
                List<Object[]> stats = logCtrl.getActivityStatsByLot(currentLot.getId());
                
                if (stats == null || stats.isEmpty()) {
                    JOptionPane.showMessageDialog(ProductionLotView.this, "Chưa có hoạt động nào.", "Thống kê", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                String[] cols = {"Loại hoạt động", "Số lần thực hiện"};
                DefaultTableModel m = new DefaultTableModel(cols, 0);
                for (Object[] r : stats) {
                    m.addRow(new Object[]{ r[0], r[1] });
                }
                JTable statsTbl = new JTable(m);
                UiUtils.styleTable(statsTbl);
                JScrollPane sp = new JScrollPane(statsTbl);
                sp.setPreferredSize(new Dimension(300, 200));
                
                JOptionPane.showMessageDialog(ProductionLotView.this, sp, "Thống kê hoạt động canh tác - Lô " + lotCode, JOptionPane.PLAIN_MESSAGE);
            });
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            currentRow = r;
            return panel;
        }
        @Override public Object getCellEditorValue() { return "view"; }
    }

    // ── UI helpers ────────────────────────────────────────────

    private JComboBox<String> makeCombo(String title, String[] items) {
        String[] opts = new String[items.length+1];
        opts[0] = title;
        System.arraycopy(items, 0, opts, 1, items.length);
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(AppTheme.FONT_BODY);
        c.setPreferredSize(new Dimension(155, 30));
        return c;
    }

    private JLabel makeComboLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(AppTheme.TEXT_SECONDARY);
        return l;
    }

    private JButton makePrimaryBtn(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x1D4ED8) : new Color(0x2563EB));
                g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),8,8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(80, 30));
        return b;
    }

    private JButton makeOutlineBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(AppTheme.FONT_BODY);
        b.setForeground(new Color(0x2563EB));
        b.setBackground(Color.WHITE);
        b.setBorder(BorderFactory.createLineBorder(new Color(0x2563EB), 1, true));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(100, 30));
        return b;
    }

    private JPanel makeCard() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),10,10));
                g2.setColor(AppTheme.BORDER_LIGHT);
                g2.draw(new RoundRectangle2D.Double(0,0,getWidth()-1,getHeight()-1,10,10));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 14, 10, 14));
        return p;
    }
}
