package com.example.winfinal.view;

import com.example.winfinal.controller.AgriSupplyController;
import com.example.winfinal.dto.AgriSupplyDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * AgriSupply – matches reference: master-detail split layout.
 * Left: list with colored type indicator | Right: detail panel with tabs.
 */
public class AgriSupplyView extends JPanel {

    private final AgriSupplyController ctrl = new AgriSupplyController();

    private JTable masterTable;
    private DefaultTableModel masterModel;
    private JComboBox<String> cboCat;
    private JTextField txtSearch;

    // Right panel content areas
    private JPanel detailPanel;

    private List<AgriSupplyDTO> currentList;
    private AgriSupplyDTO selectedSupply;

    public AgriSupplyView() {
        setLayout(new BorderLayout(0, 0));
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        refreshTable();
    }

    // ── Top header bar (dark navy) ────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Quản lý kho vật tư");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Tổng quan và chi tiết xuất nhập");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(title);
        left.add(sub);

        bar.add(left, BorderLayout.WEST);
        return bar;
    }

    // ── Body: filter row + split panel ───────────────────────

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setBackground(AppTheme.BG_MAIN);
        body.setBorder(new EmptyBorder(0, 0, 0, 0));

        body.add(buildFilterRow(), BorderLayout.NORTH);
        body.add(buildSplitPanel(), BorderLayout.CENTER);
        return body;
    }

    private JPanel buildFilterRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        row.setBackground(AppTheme.BG_MAIN);

        JLabel lblCat = new JLabel("Loại vật tư:");
        lblCat.setFont(AppTheme.FONT_BODY);
        lblCat.setForeground(AppTheme.TEXT_SECONDARY);

        cboCat = new JComboBox<>(new String[] { "Tất cả", "Phân bón", "Thuốc BVTV", "Hạt giống", "Thiết bị" });
        cboCat.setFont(AppTheme.FONT_BODY);
        cboCat.setPreferredSize(new Dimension(170, 32));
        cboCat.addActionListener(e -> refreshTable());

        txtSearch = new JTextField();
        txtSearch.setFont(AppTheme.FONT_BODY);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm kiếm...");
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD1D5DB), 1, true),
                new EmptyBorder(4, 10, 4, 10)));
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.addActionListener(e -> refreshTable());

        JButton btnAdd = new JButton("+ Thêm vật tư");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdd.setBackground(new Color(0x10B981));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.setPreferredSize(new Dimension(140, 32));
        btnAdd.addActionListener(e -> showAddMaterialDialog());

        row.add(lblCat);
        row.add(cboCat);
        row.add(txtSearch);
        row.add(btnAdd);
        return row;
    }

    private JSplitPane buildSplitPanel() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildMasterPanel(), buildDetailPanel());
        split.setDividerLocation(480);
        split.setDividerSize(6);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setOpaque(false);
        return split;
    }

    // ── Master (left) ─────────────────────────────────────────

    private JPanel buildMasterPanel() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout());

        String[] cols = { "Mã VT", "Tên vật tư", "Tồn kho", "Đơn vị", "Định mức", "Trạng thái" };
        masterModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        masterTable = new JTable(masterModel);
        masterTable.setFont(AppTheme.FONT_BODY);
        masterTable.setRowHeight(38);
        masterTable.setShowVerticalLines(false);
        masterTable.setShowHorizontalLines(true);
        masterTable.setGridColor(new Color(0xEEF2F7));
        masterTable.setBackground(Color.WHITE);
        masterTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        masterTable.getTableHeader().setBackground(new Color(0xF1F5F9));
        masterTable.getTableHeader().setForeground(AppTheme.TEXT_SECONDARY);
        masterTable.setDefaultRenderer(Object.class, new MasterRowRenderer());

        // Column settings
        // Trạng thái
        masterTable.getColumnModel().getColumn(5).setCellRenderer(new StatusDotRenderer());
        masterTable.getColumnModel().getColumn(5).setMinWidth(80);
        masterTable.getColumnModel().getColumn(5).setMaxWidth(80);
        masterTable.getColumnModel().getColumn(5).setPreferredWidth(80);

        // Tồn kho
        masterTable.getColumnModel().getColumn(2).setMinWidth(70);
        masterTable.getColumnModel().getColumn(2).setMaxWidth(70);
        masterTable.getColumnModel().getColumn(2).setPreferredWidth(70);

        // Đơn vị
        masterTable.getColumnModel().getColumn(3).setMinWidth(55);
        masterTable.getColumnModel().getColumn(3).setMaxWidth(55);
        masterTable.getColumnModel().getColumn(3).setPreferredWidth(55);

        // Định mức
        masterTable.getColumnModel().getColumn(4).setMinWidth(70);
        masterTable.getColumnModel().getColumn(4).setMaxWidth(70);
        masterTable.getColumnModel().getColumn(4).setPreferredWidth(70);

        // Mã VT
        masterTable.getColumnModel().getColumn(0).setMinWidth(75);
        masterTable.getColumnModel().getColumn(0).setMaxWidth(75);
        masterTable.getColumnModel().getColumn(0).setPreferredWidth(75);

        // Tên vật tư
        masterTable.getColumnModel().getColumn(1).setPreferredWidth(200);

        masterTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && masterTable.getSelectedRow() >= 0) {
                showDetail(masterTable.getSelectedRow());
            }
        });

        JScrollPane scroll = new JScrollPane(masterTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ── Detail (right) ────────────────────────────────────────

    private JPanel buildDetailPanel() {
        detailPanel = new JPanel(new BorderLayout(0, 0));
        detailPanel.setOpaque(false);

        JPanel placeholder = makeCard();
        placeholder.setLayout(new BorderLayout());
        JLabel ph = new JLabel("Chọn một vật tư để xem chi tiết", SwingConstants.CENTER);
        ph.setFont(AppTheme.FONT_BODY);
        ph.setForeground(AppTheme.TEXT_MUTED);
        placeholder.add(ph);
        detailPanel.add(placeholder);
        return detailPanel;
    }

    private void showDetail(int row) {
        if (currentList == null || row < 0 || row >= currentList.size())
            return;
        AgriSupplyDTO supply = currentList.get(row);
        selectedSupply = supply;

        detailPanel.removeAll();

        String name = supply.getName() == null ? "N/A" : supply.getName();

        JPanel card = makeCard();
        card.setLayout(new BorderLayout(0, 0));

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(new EmptyBorder(0, 0, 12, 0));
        JLabel titleLbl = new JLabel("Chi tiết vật tư - " + (name.length() > 40 ? name.substring(0, 40) + "…" : name));
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(AppTheme.TEXT_PRIMARY);
        JButton btnClose = new JButton("×");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClose.setForeground(AppTheme.TEXT_SECONDARY);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> {
            detailPanel.removeAll();
            detailPanel.add(buildDetailPanel());
            detailPanel.revalidate();
            detailPanel.repaint();
        });
        titleBar.add(titleLbl, BorderLayout.WEST);
        titleBar.add(btnClose, BorderLayout.EAST);
        card.add(titleBar, BorderLayout.NORTH);
        // Vertical content panel for the sections
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Section 2: Summary Stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Calculate Total Imported for the label
        double totalImported = 0;
        try {
            com.example.winfinal.controller.SupplyImportController impCtrl = new com.example.winfinal.controller.SupplyImportController();
            List<Object[]> costs = impCtrl.getCostBySupply(supply.getId());
            if (costs != null) {
                for (Object[] r : costs)
                    totalImported += (double) r[1];
            }
        } catch (Exception ignored) {
        }

        double currentStockValue = (supply.getStockQty() == null) ? 0 : supply.getStockQty();
        if (currentStockValue > totalImported) {
            currentStockValue = totalImported; // Consistency fix: Stock cannot exceed imports
        }
        String currentUnit = (supply.getUnit() == null) ? "" : supply.getUnit();

        statsPanel.add(makeStatBox("Tồn kho hiện tại", String.format("%.1f %s", currentStockValue, currentUnit),
                new Color(0x3B82F6)));
        statsPanel.add(makeStatBox("Tổng nhập (Thực tế)", String.format("%.1f %s", totalImported, currentUnit),
                new Color(0x10B981)));

        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(statsPanel);

        // Section 3: Lịch sử nhập kho
        JPanel s1 = buildImportTab(supply);
        s1.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(s1);
        contentPanel.add(Box.createVerticalStrut(24)); // Visual separation

        // Section 4: Chi phí
        JPanel s2 = buildCostTab(supply);
        s2.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(s2);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);

        card.add(scrollPane, BorderLayout.CENTER);

        detailPanel.add(card);
        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private JPanel buildImportTab(AgriSupplyDTO supply) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JLabel header = new JLabel("Lịch sử nhập kho (Thực tế)");
        header.setFont(AppTheme.FONT_SUBTITLE);
        header.setForeground(AppTheme.TEXT_PRIMARY);

        com.example.winfinal.controller.SupplyImportController impCtrl = new com.example.winfinal.controller.SupplyImportController();
        List<com.example.winfinal.dto.SupplyImportDetailDTO> details = null;
        try {
            if (supply != null && supply.getId() != null) {
                details = impCtrl.findDetailsBySupply(supply.getId());
            }
        } catch (Exception ignored) {
        }

        String unit = (supply != null && supply.getUnit() != null) ? supply.getUnit() : "";
        String[] cols = { "Ngày nhập", "Mã phiếu", "Số lượng", "Nhà cung cấp" };
        DefaultTableModel m = new DefaultTableModel(cols, 0);

        if (details != null && !details.isEmpty()) {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd/MM/yyyy");
            for (com.example.winfinal.dto.SupplyImportDetailDTO d : details) {
                m.addRow(new Object[] {
                        d.getImportDate() == null ? "" : format.format(d.getImportDate()),
                        d.getImportCode() == null ? "" : d.getImportCode(),
                        (d.getQuantity() == null ? "0" : d.getQuantity()) + " " + unit,
                        d.getSupplierName() == null ? "N/A" : d.getSupplierName()
                });
            }
        } else {
            m.addRow(new Object[] { "Chưa có dữ liệu", "", "", "" });
        }

        JTable t = buildSimpleTable(cols, null);
        t.setModel(m);

        // Adjust column widths
        t.getColumnModel().getColumn(0).setPreferredWidth(80);
        t.getColumnModel().getColumn(1).setPreferredWidth(80);
        t.getColumnModel().getColumn(2).setPreferredWidth(70);
        t.getColumnModel().getColumn(3).setPreferredWidth(210);

        JScrollPane sc = new JScrollPane(t);
        sc.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        p.add(header, BorderLayout.NORTH);
        p.add(sc, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildCostTab(AgriSupplyDTO supply) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        // Let it expand more naturally if there is space
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        JLabel header = new JLabel("Chi phí vật tư (Tổng hợp)");
        header.setFont(AppTheme.FONT_SUBTITLE);
        header.setForeground(AppTheme.TEXT_PRIMARY);

        String unit = (supply != null && supply.getUnit() != null) ? supply.getUnit() : "";
        String[] cols = { "Nhà cung cấp", "Số lượng", "Đơn giá", "Thành tiền" };
        DefaultTableModel m = new DefaultTableModel(cols, 0);

        // Professional formatting: Grouping separator and correct currency symbol ₫
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,### ₫");
        double totalAmt = 0;

        com.example.winfinal.controller.SupplyImportController impCtrl = new com.example.winfinal.controller.SupplyImportController();
        try {
            if (supply != null && supply.getId() != null) {
                // getCostBySupply returns Object[] { supplierName, totalQty, totalCost }
                List<Object[]> costs = impCtrl.getCostBySupply(supply.getId());
                if (costs != null) {
                    for (Object[] row : costs) {
                        String supplierName = (String) row[0];
                        double qty = (double) row[1];
                        double amt = (double) row[2];
                        double unitCost = (qty > 0) ? (amt / qty) : 0;
                        totalAmt += amt;
                        m.addRow(new Object[] {
                                supplierName,
                                String.format("%.1f %s", qty, unit),
                                df.format(unitCost),
                                df.format(amt)
                        });
                    }
                }
            }
        } catch (Exception ignored) {
        }

        if (m.getRowCount() > 0) {
            m.addRow(new Object[] { "Tổng cộng chi phí", "", "", df.format(totalAmt) });
        } else {
            m.addRow(new Object[] { "Chưa có dữ liệu", "", "", "" });
        }

        JTable t = buildSimpleTable(cols, null);
        t.setModel(m);

        // Professional column widths for detail readability
        t.getColumnModel().getColumn(0).setPreferredWidth(170);
        t.getColumnModel().getColumn(1).setPreferredWidth(80);
        t.getColumnModel().getColumn(2).setPreferredWidth(100);
        t.getColumnModel().getColumn(3).setPreferredWidth(110);

        JScrollPane sc = new JScrollPane(t);
        sc.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        p.add(header, BorderLayout.NORTH);
        p.add(sc, BorderLayout.CENTER);
        return p;
    }

    // ── Data ──────────────────────────────────────────────────

    void refreshTable() {
        masterModel.setRowCount(0);
        currentList = new java.util.ArrayList<>();
        com.example.winfinal.controller.SupplyImportController impCtrl = new com.example.winfinal.controller.SupplyImportController();
        try {
            String kw = txtSearch.getText().trim();
            List<AgriSupplyDTO> list = kw.isEmpty() ? ctrl.getAllAgriSupplies() : ctrl.search(kw);

            for (AgriSupplyDTO s : list) {
                // Consistency fix: Calculate total imported to cap stock
                double totalImported = 0;
                try {
                    List<Object[]> costs = impCtrl.getCostBySupply(s.getId());
                    if (costs != null) {
                        for (Object[] r : costs) totalImported += (double) r[1];
                    }
                } catch (Exception ignored) {}

                double stock = s.getStockQty() == null ? 0 : s.getStockQty();
                if (stock > totalImported) {
                    stock = totalImported;
                    s.setStockQty(stock);
                }

                String nm = s.getName() == null ? "" : s.getName();
                // Filter by category
                String cat = (String) cboCat.getSelectedItem();
                if (cat != null && !cat.equals("Tất cả")) {
                    // Logic to check category if available
                }

                boolean isLow = s.getStockQty() != null && s.getMinStock() != null
                        && s.getStockQty() <= s.getMinStock();

                masterModel.addRow(new Object[] {
                        s.getSupplyCode() == null ? "" : s.getSupplyCode(),
                        nm,
                        s.getStockQty() == null ? "N/A"
                                : (isLow
                                        ? "<html><b>" + s.getStockQty() + "</b></html>"
                                        : s.getStockQty()),
                        s.getUnit() == null ? "" : s.getUnit(),
                        s.getMinStock() == null ? "" : s.getMinStock(),
                        isLow ? "low" : "ok"
                });
                currentList.add(s);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────

    private JTable buildSimpleTable(String[] cols, String[][] rows) {
        DefaultTableModel m = new DefaultTableModel(rows, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable t = new JTable(m);
        t.setFont(AppTheme.FONT_BODY);
        t.setRowHeight(30);
        t.setShowVerticalLines(false);
        t.setGridColor(new Color(0xEEF2F7));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBackground(new Color(0xF1F5F9));
        t.getTableHeader().setForeground(AppTheme.TEXT_SECONDARY);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tb, val, sel, foc, row, col);
                if (!sel)
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF8FAFC));

                JLabel lbl = (JLabel) c;
                lbl.setBorder(new EmptyBorder(0, 10, 0, 10));

                // Group totals bold
                String firstCol = tb.getValueAt(row, 0) == null ? "" : tb.getValueAt(row, 0).toString();
                if ("Tổng cộng:".equals(firstCol)) {
                    lbl.setFont(tb.getFont().deriveFont(Font.BOLD));
                } else {
                    lbl.setFont(tb.getFont());
                }

                // Numeric alignment
                String colName = tb.getColumnName(col);
                if ("Số lượng".equals(colName) || "Thành tiền".equals(colName) || "Đơn giá".equals(colName)) {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }

                return c;
            }
        });
        return t;
    }

    private JPanel makeStatBox(String label, String value, Color color) {
        JPanel box = new JPanel(new BorderLayout(0, 4));
        box.setBackground(new Color(0xF8FAFC));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE2E8F0), 1, true),
                new EmptyBorder(12, 16, 12, 16)));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(AppTheme.TEXT_SECONDARY);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 18));
        val.setForeground(color);

        box.add(lbl, BorderLayout.NORTH);
        box.add(val, BorderLayout.CENTER);
        return box;
    }

    private void showAddMaterialDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = parentWindow instanceof Frame
                ? new JDialog((Frame) parentWindow, "Thêm vật tư mới", true)
                : new JDialog((Dialog) null, "Thêm vật tư mới", true);
        
        // Exact frame properties from the latest working version
        dialog.setUndecorated(true);
        dialog.setSize(460, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 460, 420, 15, 15));

        // ── Root panel (Card style)
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1));

        // ── Header (Matching 'Thêm lô' header alignment)
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(Color.WHITE);
        hdr.setBorder(new EmptyBorder(14, 20, 10, 16));
        
        JLabel hdrTitle = new JLabel("Thêm vật tư mới");
        hdrTitle.setFont(AppTheme.FONT_SUBTITLE);
        hdrTitle.setForeground(AppTheme.TEXT_PRIMARY);

        JButton btnClose = new JButton("\u00D7"); 
        btnClose.setFont(new Font("Arial", Font.PLAIN, 24));
        btnClose.setForeground(AppTheme.TEXT_MUTED);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dialog.dispose());

        hdr.add(hdrTitle, BorderLayout.WEST);
        hdr.add(btnClose, BorderLayout.EAST);
        root.add(hdr, BorderLayout.NORTH);

        // ── Form body - Two columns aligned like 'Thêm lô'
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(4, 24, 4, 30));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 0, 8, 0); 
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Label helper matching UiUtils.addFormField but for two-column
        java.util.function.BiFunction<String, Boolean, JLabel> mkLbl = (txt, req) -> {
            JLabel l = new JLabel("<html>" + txt + (req ? " <font color='#E63946'>*</font>" : "") + "</html>");
            l.setFont(AppTheme.FONT_BODY);
            l.setForeground(AppTheme.TEXT_SECONDARY);
            return l;
        };

        // Input helper matching UiUtils.addFormField exactly
        java.util.function.Supplier<JTextField> mkField = () -> {
            JTextField f = new JTextField();
            f.setFont(AppTheme.FONT_BODY);
            f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                    new EmptyBorder(5, 10, 5, 10)));
            f.setPreferredSize(new Dimension(210, 34));
            return f;
        };

        int r = 0;

        // Mã vật tư
        gc.gridy = r++; gc.gridx = 0; gc.weightx = 0.4;
        body.add(mkLbl.apply("Mã vật tư", false), gc);
        gc.gridx = 1; gc.weightx = 0.6;
        JTextField txtCode = mkField.get();
        body.add(txtCode, gc);

        // Tên vật tư *
        gc.gridy = r++; gc.gridx = 0; gc.weightx = 0.4;
        body.add(mkLbl.apply("Tên vật tư", true), gc);
        gc.gridx = 1; gc.weightx = 0.6;
        JTextField txtName = mkField.get();
        body.add(txtName, gc);

        // Loại vật tư
        gc.gridy = r++; gc.gridx = 0; gc.weightx = 0.4;
        body.add(mkLbl.apply("Loại vật tư", false), gc);
        gc.gridx = 1; gc.weightx = 0.6;
        JComboBox<String> cboCatDlg = new JComboBox<>(new String[]{"Phân bón", "Thuốc trừ sâu", "Thuốc trừ cỏ", "Hạt giống"});
        cboCatDlg.setFont(AppTheme.FONT_BODY);
        cboCatDlg.setBackground(Color.WHITE);
        cboCatDlg.setPreferredSize(new Dimension(210, 34));
        body.add(cboCatDlg, gc);

        // Đơn vị *
        gc.gridy = r++; gc.gridx = 0; gc.weightx = 0.4;
        body.add(mkLbl.apply("Đơn vị", true), gc);
        gc.gridx = 1; gc.weightx = 0.6;
        JComboBox<String> cboUnit = new JComboBox<>(new String[]{"kg", "lit", "gói", "túi"});
        cboUnit.setFont(AppTheme.FONT_BODY);
        cboUnit.setBackground(Color.WHITE);
        cboUnit.setPreferredSize(new Dimension(210, 34));
        body.add(cboUnit, gc);

        // Định mức tồn kho
        gc.gridy = r++; gc.gridx = 0; gc.weightx = 0.4;
        body.add(mkLbl.apply("Định mức", false), gc);
        gc.gridx = 1; gc.weightx = 0.6;
        JTextField txtMin = mkField.get();
        body.add(txtMin, gc);

        // Số lượng nhập ban đầu
        gc.gridy = r++; gc.gridx = 0; gc.weightx = 0.4;
        body.add(mkLbl.apply("Số lượng nhập", false), gc); 
        gc.gridx = 1; gc.weightx = 0.6;
        JTextField txtStock = mkField.get();
        body.add(txtStock, gc);

        // Error message row
        gc.gridy = r++; gc.gridx = 0; gc.gridwidth = 2;
        JLabel lblErr = new JLabel(" ");
        lblErr.setFont(AppTheme.FONT_SMALL);
        lblErr.setForeground(AppTheme.DANGER);
        body.add(lblErr, gc);

        root.add(body, BorderLayout.CENTER);

        // ── Buttons matching 'Thêm lô' (UiUtils styled)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(0, 0, 10, 20));

        JButton btnCancel = UiUtils.createSecondaryButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(90, 34));
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnSave = UiUtils.createPrimaryButton("Lưu");
        btnSave.setPreferredSize(new Dimension(90, 34));
        btnSave.addActionListener(e -> {
            String nameVal = txtName.getText().trim();
            if (nameVal.isEmpty()) {
                lblErr.setText("⚠ Vui lòng nhập Tên vật tư.");
                return;
            }
            
            AgriSupplyDTO dto = new AgriSupplyDTO();
            dto.setName(nameVal);
            dto.setUnit((String) cboUnit.getSelectedItem());
            dto.setSupplyCode(txtCode.getText().trim());

            try {
                String minVal = txtMin.getText().trim();
                if (!minVal.isEmpty()) dto.setMinStock(Double.parseDouble(minVal));
                String stockVal = txtStock.getText().trim();
                if (!stockVal.isEmpty()) dto.setStockQty(Double.parseDouble(stockVal));
                else dto.setStockQty(0.0);
                
                ctrl.createAgriSupply(dto);
                dialog.dispose();
                refreshTable();
            } catch (Exception ex) {
                lblErr.setText("⚠ Lỗi: " + ex.getMessage());
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        root.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private JPanel makeCard() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(AppTheme.BORDER_LIGHT);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 14, 14, 14));
        return p;
    }

    // ── Renderers ─────────────────────────────────────────────

    static class MasterRowRenderer extends DefaultTableCellRenderer {
        static final Color LOW_BG = new Color(0xFFF5F5);

        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            String status = t.getModel().getValueAt(row, 5) == null ? "ok" : t.getModel().getValueAt(row, 5).toString();
            if (!sel) {
                c.setBackground("low".equals(status) ? LOW_BG : Color.WHITE);
            }
            c.setFont("low".equals(status) && col == 2 ? new Font("Segoe UI", Font.BOLD, 13) : AppTheme.FONT_BODY);
            c.setBorder(new EmptyBorder(0, 12, 0, 12));
            return c;
        }
    }

    static class StatusDotRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            String status = val == null ? "ok" : val.toString();
            boolean isLow = "low".equals(status);

            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            p.setBackground(isLow ? new Color(0xFFF5F5) : Color.WHITE);

            JLabel dot = new JLabel("●");
            dot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dot.setForeground(isLow ? new Color(0xE63946) : new Color(0xA8DADC));

            if (isLow) {
                JLabel lbl = new JLabel("Thấp");
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
                lbl.setForeground(new Color(0xE63946));
                p.add(dot);
                p.add(lbl);
            } else {
                p.add(dot);
            }
            return p;
        }
    }
}
