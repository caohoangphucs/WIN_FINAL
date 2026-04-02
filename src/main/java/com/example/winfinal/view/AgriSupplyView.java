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
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(AppTheme.BG_MAIN);

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftFilters.setOpaque(false);

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

        leftFilters.add(lblCat);
        leftFilters.add(cboCat);
        leftFilters.add(txtSearch);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightActions.setOpaque(false);
        JButton btnAdd = new JButton("+ Thêm vật tư");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBackground(AppTheme.SUCCESS);
        btnAdd.setBorderPainted(false);
        btnAdd.setFocusPainted(false);
        btnAdd.setOpaque(true);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.setPreferredSize(new Dimension(130, 32));
        btnAdd.addActionListener(e -> showAddDialog());
        rightActions.add(btnAdd);

        row.add(leftFilters, BorderLayout.WEST);
        row.add(rightActions, BorderLayout.EAST);
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
        // Vertical content panel for the two sections
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Header Section: Image + Supply info if needed
        JPanel headerInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        headerInfoPanel.setOpaque(false);
        headerInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(100, 100));
        imgLabel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        String imgPath = "pic/" + supply.getSupplyCode();
        String[] exts = {".jpg", ".png", ".webp", ".jpeg"};
        ImageIcon icon = null;
        for (String ext : exts) {
            java.io.File f = new java.io.File(imgPath + ext);
            if (f.exists()) {
                icon = new ImageIcon(imgPath + ext);
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(img));
                break;
            }
        }
        if (icon == null) {
            imgLabel.setText("Không có ảnh");
            imgLabel.setFont(AppTheme.FONT_SMALL);
            imgLabel.setForeground(AppTheme.TEXT_SECONDARY);
        }
        
        headerInfoPanel.add(imgLabel);
        contentPanel.add(headerInfoPanel);
        contentPanel.add(Box.createVerticalStrut(24));

        // Section 1: Lịch sử nhập kho
        JPanel s1 = buildImportTab(supply);
        s1.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(s1);
        contentPanel.add(Box.createVerticalStrut(24)); // Visual separation

        // Section 2: Chi phí
        JPanel s2 = buildCostTab();
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
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220)); // Limit height but allow expansion

        JLabel header = new JLabel("Lịch sử nhập kho (Thực tế)");
        header.setFont(AppTheme.FONT_SUBTITLE);
        header.setForeground(AppTheme.TEXT_PRIMARY);

        com.example.winfinal.controller.SupplyImportController impCtrl = new com.example.winfinal.controller.SupplyImportController();
        List<com.example.winfinal.dto.SupplyImportDTO> imports = null;
        try {
            if (supply != null && supply.getId() != null) {
                imports = impCtrl.findBySupply(supply.getId());
            }
        } catch (Exception ignored) {
        }

        String[] cols = { "Ngày nhập", "Mã phiếu nhập", "Nhà cung cấp" };
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        if (imports != null && !imports.isEmpty()) {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd/MM/yyyy");
            for (com.example.winfinal.dto.SupplyImportDTO i : imports) {
                m.addRow(new Object[] {
                        i.getImportDate() == null ? "" : format.format(i.getImportDate()),
                        i.getImportCode() == null ? "" : i.getImportCode(),
                        i.getSupplierName() != null ? i.getSupplierName() : "ID: " + i.getSupplierId()
                });
            }
        } else {
            m.addRow(new Object[] { "Chưa có dữ liệu", "", "" });
        }

        JTable t = buildSimpleTable(cols, null);
        t.setModel(m);
        JScrollPane sc = new JScrollPane(t);
        sc.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        p.add(header, BorderLayout.NORTH);
        p.add(sc, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildCostTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel header = new JLabel("Chi phí");
        header.setFont(AppTheme.FONT_SUBTITLE);
        header.setForeground(AppTheme.TEXT_PRIMARY);
        String[] cols = { "Nhà cung cấp", "Số lượng", "Thành tiền" };
        String[][] data = {
                { "Công Ty Nông Dược", "150 L", "7,500,000 đ" },
                { "Hóa Chất XYZ", "80 L", "4,000,000 đ" },
                { "Công Ty Việt Nông", "60 L", "3,000,000 đ" },
                { "Tổng cộng:", "", "14,500,000 đ" },
        };
        JTable t = buildSimpleTable(cols, data);
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
        try {
            String kw = txtSearch.getText().trim();
            List<AgriSupplyDTO> list = kw.isEmpty() ? ctrl.getAllAgriSupplies() : ctrl.search(kw);

            for (AgriSupplyDTO s : list) {
                String nm = s.getName() == null ? "" : s.getName();
                // Filter by category
                String cat = (String) cboCat.getSelectedItem();
                if (cat != null && !cat.equals("Tất cả")) {
                    // Logic to check category if available (placeholder simulation if category not
                    // mapped)
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
                ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
        return t;
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

    private void showAddDialog() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm mới Vật tư", true);
        d.setSize(500, 420);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());
        
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        form.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        JTextField txtCode = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtUnit = new JTextField();
        JTextField txtMinStock = new JTextField();
        JTextField txtStock = new JTextField("0.0"); // Initial stock

        addGridRow(form, g, "Mã VT (VD: AS015):", txtCode, 0);
        addGridRow(form, g, "Tên vật tư:", txtName, 1);
        addGridRow(form, g, "Đơn vị tính:", txtUnit, 2);
        addGridRow(form, g, "Định mức tối thiểu:", txtMinStock, 3);
        addGridRow(form, g, "Tồn kho ban đầu:", txtStock, 4);

        JPanel pnlImg = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlImg.setBackground(Color.WHITE);
        JLabel lblImgFile = new JLabel("Chưa chọn file...");
        lblImgFile.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        JButton btnImg = new JButton("Chọn file...");
        java.util.concurrent.atomic.AtomicReference<java.io.File> selectedImage = new java.util.concurrent.atomic.AtomicReference<>();
        btnImg.addActionListener(ev -> {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "webp"));
            if (jfc.showOpenDialog(d) == JFileChooser.APPROVE_OPTION) {
                selectedImage.set(jfc.getSelectedFile());
                lblImgFile.setText(jfc.getSelectedFile().getName());
            }
        });
        pnlImg.add(btnImg);
        pnlImg.add(Box.createHorizontalStrut(10));
        pnlImg.add(lblImgFile);
        addGridRow(form, g, "Ảnh đại diện:", pnlImg, 5);

        d.add(form, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bot.setBackground(Color.WHITE);
        bot.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JButton btnCancel = new JButton("Hủy");
        btnCancel.addActionListener(e -> d.dispose());
        JButton btnSave = new JButton("Lưu");
        btnSave.addActionListener(e -> {
            try {
                AgriSupplyDTO dto = new AgriSupplyDTO();
                dto.setSupplyCode(txtCode.getText().trim());
                dto.setName(txtName.getText().trim());
                dto.setUnit(txtUnit.getText().trim());
                dto.setMinStock(Double.parseDouble(txtMinStock.getText().trim()));
                dto.setStockQty(Double.parseDouble(txtStock.getText().trim()));
                
                ctrl.createAgriSupply(dto);
                
                // Lưu ảnh nếu có chọn
                if (selectedImage.get() != null) {
                    try {
                        String code = dto.getSupplyCode().trim();
                        String originalName = selectedImage.get().getName();
                        String ext = "";
                        int idx = originalName.lastIndexOf('.');
                        if (idx > 0) ext = originalName.substring(idx);
                        
                        java.io.File picDir = new java.io.File("pic");
                        if (!picDir.exists()) picDir.mkdirs();
                        
                        java.io.File dest = new java.io.File(picDir, code + ext);
                        java.nio.file.Files.copy(selectedImage.get().toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception exx) {
                        System.err.println("Không thể lưu ảnh vật tư: " + exx.getMessage());
                    }
                }
                
                d.dispose();
                refreshTable();
                JOptionPane.showMessageDialog(this, "Thêm vật tư thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu vật tư: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        bot.add(btnCancel);
        bot.add(btnSave);
        d.add(bot, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void addGridRow(JPanel form, GridBagConstraints g, String label, JComponent field, int row) {
        g.gridy = row;
        g.gridx = 0; g.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_BODY);
        form.add(lbl, g);
        g.gridx = 1; g.weightx = 0.7;
        if(field instanceof JTextField) {
            field.setFont(AppTheme.FONT_BODY);
            field.putClientProperty("JComponent.roundRect", true);
            field.setPreferredSize(new Dimension(field.getPreferredSize().width, 32));
        }
        form.add(field, g);
    }
}
