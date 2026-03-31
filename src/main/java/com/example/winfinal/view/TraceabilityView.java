package com.example.winfinal.view;

import com.example.winfinal.controller.*;
import com.example.winfinal.dto.ProductionLotDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Traceability – matches reference: blue banner + section cards with tables.
 */
public class TraceabilityView extends JPanel {

    private final ProductionLotController  lotCtrl         = new ProductionLotController();
    private final CultivationLogController cultivationCtrl = new CultivationLogController();
    private final IrrigationLogController  irrigationCtrl  = new IrrigationLogController();
    private final PestReportController     pestCtrl        = new PestReportController();
    private final HarvestRecordController  harvestCtrl     = new HarvestRecordController();

    private JTextField txtLotCode;
    private JPanel contentPanel;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public TraceabilityView() {
        setLayout(new BorderLayout(0, 0));
        setBackground(AppTheme.BG_MAIN);

        add(buildBanner(),  BorderLayout.NORTH);
        add(buildMain(),    BorderLayout.CENTER);
    }

    // ── Blue banner ───────────────────────────────────────────

    private JPanel buildBanner() {
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 16)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0x1B4F9B));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("TRUY XUẤT NGUỒN GỐC");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        banner.add(title);
        return banner;
    }

    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout(0, 14));
        main.setBackground(AppTheme.BG_MAIN);
        main.setBorder(new EmptyBorder(18, 24, 18, 24));

        // Search row
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setOpaque(false);
        searchRow.setBorder(new EmptyBorder(0,0,10,0));

        JLabel lblCode = new JLabel("Nhập mã lô:");
        lblCode.setFont(AppTheme.FONT_BODY);
        lblCode.setForeground(AppTheme.TEXT_SECONDARY);

        txtLotCode = new JTextField(22);
        txtLotCode.setFont(AppTheme.FONT_BODY);
        txtLotCode.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xBFD3E6), 1, true),
                new EmptyBorder(5, 10, 5, 10)));
        txtLotCode.setPreferredSize(new Dimension(260, 34));

        JButton btnSearch = buildBlueBtn("Tra cứu");
        JButton btnClear  = UiUtils.createSecondaryButton("Xóa");

        btnSearch.addActionListener(e -> doTrace());
        txtLotCode.addActionListener(e -> doTrace());
        btnClear.addActionListener(e -> {
            txtLotCode.setText("");
            contentPanel.removeAll();
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        searchRow.add(lblCode);
        searchRow.add(txtLotCode);
        searchRow.add(btnSearch);
        searchRow.add(btnClear);

        // Scrollable content
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(AppTheme.BG_MAIN);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppTheme.BG_MAIN);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        main.add(searchRow, BorderLayout.NORTH);
        main.add(scroll,    BorderLayout.CENTER);
        return main;
    }

    // ── Trace logic ───────────────────────────────────────────

    private void doTrace() {
        String lotCode = txtLotCode.getText().trim();
        if (lotCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã lô.");
            return;
        }
        contentPanel.removeAll();

        // ── Lot info card ────────────────────────────────────
        try {
            ProductionLotDTO lot = lotCtrl.getFullTraceabilityInfo(lotCode);
            if (lot != null) contentPanel.add(buildInfoCard(lot));
        } catch (Exception ignored) {}

        // ── Quá trình chăm sóc ──────────────────────────────
        String[][] careRows = {};
        try {
            List<Object[]> logs = cultivationCtrl.getTraceabilityLogs(lotCode);
            careRows = new String[logs.size()][4];
            for (int i=0; i<logs.size(); i++) {
                Object[] r = logs.get(i);
                careRows[i][0] = r[0]==null ? "N/A" : r[0].toString();
                careRows[i][1] = r[1]==null ? "" : r[1].toString();
                careRows[i][2] = r[2]==null ? "" : r[2].toString();
                careRows[i][3] = "";   // employee name – placeholder
            }
        } catch (Exception ignored) {}
        contentPanel.add(buildSectionTable(
                "Quá Trình Chăm Sóc",
                new String[]{"Ngày","Hoạt động","Loại vật tư","Người thực hiện"},
                careRows,
                new Color(0x2B5B4E)));

        // ── Sức khỏe cây trồng (pest) ───────────────────────
        String[][] pestRows = {};
        try {
            List<Object[]> logs = pestCtrl.getTraceabilityLogs(lotCode);
            pestRows = new String[logs.size()][3];
            for (int i=0; i<logs.size(); i++) {
                Object[] r = logs.get(i);
                pestRows[i][0] = r[0]==null ? "N/A" : r[0].toString();
                pestRows[i][1] = r[1]==null ? "" : r[1].toString();
                pestRows[i][2] = r[2]==null ? "" : r[2].toString();
            }
        } catch (Exception ignored) {}
        contentPanel.add(buildSectionTable(
                "Sức Khỏe Cây Trồng",
                new String[]{"Ngày","Vấn đề","Cách xử lý"},
                pestRows,
                new Color(0x4E7B2B)));

        // ── Tưới tiêu ────────────────────────────────────────
        String irrigInfo = "N/A";
        try {
            List<Object[]> logs = irrigationCtrl.getTraceabilityLogs(lotCode);
            if (!logs.isEmpty() && logs.get(0)[1] != null)
                irrigInfo = "Lượng nước: " + logs.get(0)[1] + " L";
        } catch (Exception ignored) {}
        contentPanel.add(buildSimpleSection("Tưới Tiêu", "Chế độ nước:", irrigInfo, new Color(0x2B5B7B)));

        // ── Đầu ra (harvest) ─────────────────────────────────
        String hDate = "N/A", hYield = "N/A", hCust = "N/A";
        try {
            var harvests = harvestCtrl.findByLotCode(lotCode);
            if (!harvests.isEmpty()) {
                var h = harvests.get(0);
                hDate  = h.getHarvestDate() == null ? "N/A" : sdf.format(h.getHarvestDate());
                hYield = h.getYieldKg() == null ? "N/A" : h.getYieldKg() + " Tấn";
                hCust  = h.getCustomerId() == null ? "N/A" : "ID: " + h.getCustomerId();
            }
        } catch (Exception ignored) {}
        contentPanel.add(buildHarvestSection(hDate, hYield, hCust));

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ── Section builders ──────────────────────────────────────

    private JPanel buildInfoCard(ProductionLotDTO lot) {
        JPanel card = makeSectionCard("Thông Tin Chung", new Color(0x2563EB));
        card.setLayout(new BorderLayout(0,10));

        JPanel titleRow = (JPanel) new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);

        JLabel sectionTitle = new JLabel("  Thông Tin Chung");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionTitle.setForeground(new Color(0x2563EB));
        titleRow.setOpaque(false);

        JPanel grid = new JPanel(new GridLayout(2, 4, 0, 8));
        grid.setBackground(Color.WHITE);
        grid.setBorder(new EmptyBorder(12, 16, 12, 16));

        addInfoPair(grid, "Trang trại:", lot.getFarmId() == null ? "N/A" : "Farm #"+lot.getFarmId());
        addInfoPair(grid, "Loại cây:", lot.getCropTypeId() == null ? "N/A" : "CropType #"+lot.getCropTypeId());
        addInfoPair(grid, "Mùa vụ:", lot.getPlantDate() == null ? "N/A" : sdf.format(lot.getPlantDate()));
        addInfoPair(grid, "Người quản lý:", lot.getManagerId() == null ? "N/A" : "Mgr #"+lot.getManagerId());

        JPanel outer = new JPanel(new BorderLayout(0,6));
        outer.setBackground(Color.WHITE);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xBFD3E6), 1, true),
                new EmptyBorder(0,0,0,0)));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        header.setBackground(new Color(0xF0F4FF));
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0, new Color(0xBFD3E6)));

        JLabel ico = new JLabel("  Thong Tin Chung");
        ico.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ico.setForeground(new Color(0x2563EB));
        header.add(ico);

        outer.add(header, BorderLayout.NORTH);
        outer.add(grid, BorderLayout.CENTER);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(0,0,10,0));
        wrap.add(outer);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        return wrap;
    }

    private void addInfoPair(JPanel p, String label, String value) {
        JPanel item = new JPanel(new BorderLayout(0,2));
        item.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(0x2563EB));
        JLabel val = new JLabel(value);
        val.setFont(AppTheme.FONT_BODY);
        val.setForeground(AppTheme.TEXT_PRIMARY);
        item.add(lbl, BorderLayout.NORTH);
        item.add(val, BorderLayout.CENTER);
        p.add(item);
    }

    private JPanel buildSectionTable(String title, String[] cols, String[][] rows, Color headerColor) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(0,0,10,0));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        outer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel card = new JPanel(new BorderLayout(0,0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xD1D5DB), 1, true));

        // Header
        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        hdr.setBackground(new Color(0xF8FAFC));
        hdr.setBorder(BorderFactory.createMatteBorder(0,0,1,0, new Color(0xD1D5DB)));
        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(headerColor);
        hdr.add(lbl);
        card.add(hdr, BorderLayout.NORTH);

        // Table
        DefaultTableModel mdl = new DefaultTableModel(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(mdl);
        t.setFont(AppTheme.FONT_BODY);
        t.setRowHeight(32);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(new Color(0xEDF2F7));
        t.setBackground(Color.WHITE);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBackground(new Color(0xEBF8FF));
        t.getTableHeader().setForeground(new Color(0x2563EB));
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,1,0, new Color(0xBFD3E6)));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tb, val, sel, foc, row, col);
                if (!sel) c.setBackground(row%2==0 ? Color.WHITE : new Color(0xF8FAFC));
                ((JLabel)c).setBorder(new EmptyBorder(0, 12, 0, 12));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(t);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);

        outer.add(card);
        return outer;
    }

    private JPanel buildSimpleSection(String title, String label, String value, Color color) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(0,0,10,0));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        outer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel card = new JPanel(new BorderLayout(0,0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xD1D5DB), 1, true));

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        hdr.setBackground(new Color(0xF8FAFC));
        hdr.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(0xD1D5DB)));
        JLabel hLbl = new JLabel("  " + title);
        hLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hLbl.setForeground(color);
        hdr.add(hLbl);

        JPanel body = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        body.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(0x2563EB));
        JLabel val = new JLabel(value);
        val.setFont(AppTheme.FONT_BODY);
        val.setForeground(AppTheme.TEXT_PRIMARY);
        body.add(lbl); body.add(val);

        card.add(hdr, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        outer.add(card);
        return outer;
    }

    private JPanel buildHarvestSection(String date, String yield, String customer) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(0,0,10,0));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        outer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xD1D5DB), 1, true));

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        hdr.setBackground(new Color(0xF8FAFC));
        hdr.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(0xD1D5DB)));
        JLabel hLbl = new JLabel("  Đầu Ra");
        hLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hLbl.setForeground(new Color(0xB45309));
        hdr.add(hLbl);

        JPanel body = new JPanel(new GridLayout(3, 2, 6, 8));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(10, 16, 10, 16));
        addInfoPair2(body, "Ngày thu hoạch:", date);
        addInfoPair2(body, "", "");
        addInfoPair2(body, "Sản lượng:", yield);
        addInfoPair2(body, "", "");
        addInfoPair2(body, "Khách hàng:", customer);
        addInfoPair2(body, "", "");

        card.add(hdr, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        outer.add(card);
        return outer;
    }

    private void addInfoPair2(JPanel p, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(0x2563EB));
        JLabel val = new JLabel(value);
        val.setFont(AppTheme.FONT_BODY);
        val.setForeground(AppTheme.TEXT_PRIMARY);
        p.add(lbl); p.add(val);
    }

    private JPanel makeSectionCard(String title, Color accent) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xBFD3E6), 1, true),
                new EmptyBorder(10,14,10,14)));
        return p;
    }

    private JButton buildBlueBtn(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x1D4ED8) : new Color(0x2563EB));
                g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),7,7));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(AppTheme.FONT_BODY);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(90, 34));
        return b;
    }
}
