package com.example.winfinal.view;

import com.example.winfinal.controller.AgriSupplyController;
import com.example.winfinal.dto.AgriSupplyDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
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
    private JPanel tabImport, tabConsumption, tabCost;
    private JButton tabBtnImport, tabBtnConsume, tabBtnCost;
    private CardLayout tabLayout;
    private JPanel tabContent;

    private AgriSupplyDTO selectedSupply;

    public AgriSupplyView() {
        setLayout(new BorderLayout(0,0));
        setBackground(AppTheme.BG_MAIN);

        add(buildTopBar(),   BorderLayout.NORTH);
        add(buildBody(),     BorderLayout.CENTER);

        refreshTable();
    }

    // ── Top header bar (dark navy) ────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(0x1B355E));
                g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0,52));
        bar.setBorder(new EmptyBorder(0,18,0,18));

        JLabel title = new JLabel("Inventory Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        right.setOpaque(false);
        for (String ico : new String[]{"?", "Bell", "Set", "="}) {
            JLabel ic = new JLabel(ico);
            ic.setForeground(new Color(0xBFD3E6));
            ic.setFont(AppTheme.FONT_BODY);
            right.add(ic);
        }

        bar.add(title, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Body: filter row + split panel ───────────────────────

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0,0));
        body.setBackground(AppTheme.BG_MAIN);
        body.setBorder(new EmptyBorder(12,14,12,14));

        body.add(buildFilterRow(),  BorderLayout.NORTH);
        body.add(buildSplitPanel(), BorderLayout.CENTER);
        return body;
    }

    private JPanel buildFilterRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        row.setBackground(AppTheme.BG_MAIN);

        JLabel lblCat = new JLabel("Loại vật tư:");
        lblCat.setFont(AppTheme.FONT_BODY);
        lblCat.setForeground(AppTheme.TEXT_SECONDARY);

        cboCat = new JComboBox<>(new String[]{"Tất cả","Phân bón","Thuốc BVTV","Hạt giống","Thiết bị"});
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

        row.add(lblCat);
        row.add(cboCat);
        row.add(txtSearch);
        return row;
    }

    private JSplitPane buildSplitPanel() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildMasterPanel(), buildDetailPanel());
        split.setDividerLocation(380);
        split.setDividerSize(6);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setOpaque(false);
        return split;
    }

    // ── Master (left) ─────────────────────────────────────────

    private JPanel buildMasterPanel() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout());

        String[] cols = {"Mã vật tư  ·  Tên vật tư", "Tồn kho", "Đơn vị", "Min Stock", "Trạng thái"};
        masterModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
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

        // Status column
        masterTable.getColumnModel().getColumn(4).setCellRenderer(new StatusDotRenderer());
        masterTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        masterTable.getColumnModel().getColumn(0).setPreferredWidth(170);

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
        detailPanel = new JPanel(new BorderLayout(0,0));
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
        detailPanel.removeAll();

        Object nameObj = masterModel.getValueAt(row, 0);
        String name    = nameObj == null ? "N/A" : nameObj.toString();
        Object stockObj= masterModel.getValueAt(row, 1);
        String stock   = stockObj == null ? "N/A" : stockObj.toString() + " " + masterModel.getValueAt(row,2);

        JPanel card = makeCard();
        card.setLayout(new BorderLayout(0, 0));

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(new EmptyBorder(0,0,12,0));
        JLabel titleLbl = new JLabel("Chi tiết vật tư - " + (name.length()>40 ? name.substring(0,40)+"…" : name));
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(AppTheme.TEXT_PRIMARY);
        JButton btnClose = new JButton("×");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClose.setForeground(AppTheme.TEXT_SECONDARY);
        btnClose.setBorderPainted(false); btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> {
            detailPanel.removeAll();
            detailPanel.add(buildDetailPanel());
            detailPanel.revalidate(); detailPanel.repaint();
        });
        titleBar.add(titleLbl, BorderLayout.WEST);
        titleBar.add(btnClose, BorderLayout.EAST);
        card.add(titleBar, BorderLayout.NORTH);

        // Tab buttons
        JPanel tabRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabRow.setOpaque(false);
        tabRow.setBorder(new EmptyBorder(0,0,8,0));
        tabBtnImport  = makeTabBtn("Lịch sử nhập kho", true);
        tabBtnConsume = makeTabBtn("Mức độ tiêu thụ", false);
        tabBtnCost    = makeTabBtn("Báo cáo chi phí", false);
        tabRow.add(tabBtnImport); tabRow.add(tabBtnConsume); tabRow.add(tabBtnCost);

        // Tab content
        tabLayout = new CardLayout();
        tabContent = new JPanel(tabLayout);
        tabContent.setOpaque(false);
        tabContent.add(buildImportTab(),     "import");
        tabContent.add(buildConsumeTab(),    "consume");
        tabContent.add(buildCostTab(),       "cost");

        tabBtnImport.addActionListener(e -> { tabLayout.show(tabContent,"import");  activateTab(tabBtnImport); });
        tabBtnConsume.addActionListener(e ->{ tabLayout.show(tabContent,"consume"); activateTab(tabBtnConsume); });
        tabBtnCost.addActionListener(e ->   { tabLayout.show(tabContent,"cost");    activateTab(tabBtnCost); });

        JPanel center = new JPanel(new BorderLayout(0, 6));
        center.setOpaque(false);
        center.add(tabRow, BorderLayout.NORTH);
        center.add(tabContent, BorderLayout.CENTER);
        card.add(center, BorderLayout.CENTER);

        detailPanel.add(card);
        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private JPanel buildImportTab() {
        JPanel p = new JPanel(new BorderLayout(0,6));
        p.setOpaque(false);
        JLabel header = new JLabel("Lịch sử nhập kho");
        header.setFont(AppTheme.FONT_SUBTITLE);
        header.setForeground(AppTheme.TEXT_SECONDARY);

        String[] cols = {"Ngày nhập","Số lượng","Nhà cung cấp"};
        String[][] data = {
            {"15/04/2024","50 L","Công Ty Nông Dược"},
            {"28/03/2024","30 L","Hóa Chất XYZ"},
            {"10/03/2024","20 L","Công Ty Việt Nông"},
        };
        JTable t = buildSimpleTable(cols, data);
        JScrollPane sc = new JScrollPane(t);
        sc.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        p.add(header, BorderLayout.NORTH);
        p.add(sc, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildConsumeTab() {
        JPanel p = new JPanel(new BorderLayout(0,6));
        p.setOpaque(false);
        JLabel header = new JLabel("Mức độ tiêu thụ hàng tháng");
        header.setFont(AppTheme.FONT_SUBTITLE);
        header.setForeground(AppTheme.TEXT_SECONDARY);
        p.add(header, BorderLayout.NORTH);
        p.add(new LineMiniChart(), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildCostTab() {
        JPanel p = new JPanel(new BorderLayout(0,6));
        p.setOpaque(false);
        JLabel header = new JLabel("Báo cáo chi phí");
        header.setFont(AppTheme.FONT_SUBTITLE);
        header.setForeground(AppTheme.TEXT_SECONDARY);
        String[] cols = {"Nhà cung cấp","Số lượng","Thành tiền"};
        String[][] data = {
            {"Công Ty Nông Dược","150 L","7,500,000 đ"},
            {"Hóa Chất XYZ","80 L","4,000,000 đ"},
            {"Công Ty Việt Nông","60 L","3,000,000 đ"},
            {"Tổng cộng:","","14,500,000 đ"},
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
        try {
            String kw = txtSearch.getText().trim().toLowerCase();
            List<AgriSupplyDTO> list = ctrl.getAllAgriSupplies();
            for (AgriSupplyDTO s : list) {
                String nm = s.getName()==null ? "" : s.getName();
                if (!kw.isEmpty() && !nm.toLowerCase().contains(kw)
                        && (s.getSupplyCode()==null || !s.getSupplyCode().toLowerCase().contains(kw))) continue;

                boolean isLow = s.getStockQty()!=null && s.getMinStock()!=null
                        && s.getStockQty() <= s.getMinStock();
                String nameCell = (s.getSupplyCode()==null ? "" : s.getSupplyCode())+" - "+nm;
                masterModel.addRow(new Object[]{
                    nameCell,
                    s.getStockQty()==null ? "N/A" : (isLow
                            ? "<html><b>"+s.getStockQty()+"</b></html>"
                            : s.getStockQty()),
                    s.getUnit()==null ? "" : s.getUnit(),
                    s.getMinStock()==null ? "" : s.getMinStock(),
                    isLow ? "low" : "ok"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────

    private JButton makeTabBtn(String text, boolean active) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color activeBg = new Color(0x2563EB);
        if (active) {
            b.setBackground(activeBg);
            b.setForeground(Color.WHITE);
            b.setBorder(BorderFactory.createEmptyBorder(5,12,5,12));
        } else {
            b.setBackground(new Color(0xF1F5F9));
            b.setForeground(AppTheme.TEXT_SECONDARY);
            b.setBorder(BorderFactory.createLineBorder(new Color(0xD1D5DB),1));
        }
        return b;
    }

    private void activateTab(JButton active) {
        for (JButton b : new JButton[]{tabBtnImport, tabBtnConsume, tabBtnCost}) {
            boolean isActive = (b == active);
            b.setBackground(isActive ? new Color(0x2563EB) : new Color(0xF1F5F9));
            b.setForeground(isActive ? Color.WHITE : AppTheme.TEXT_SECONDARY);
        }
    }

    private JTable buildSimpleTable(String[] cols, String[][] rows) {
        DefaultTableModel m = new DefaultTableModel(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(m);
        t.setFont(AppTheme.FONT_BODY); t.setRowHeight(30);
        t.setShowVerticalLines(false);
        t.setGridColor(new Color(0xEEF2F7));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBackground(new Color(0xF1F5F9));
        t.getTableHeader().setForeground(AppTheme.TEXT_SECONDARY);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tb, val, sel, foc, row, col);
                if (!sel) c.setBackground(row%2==0 ? Color.WHITE : new Color(0xF8FAFC));
                ((JLabel)c).setBorder(new EmptyBorder(0,10,0,10));
                return c;
            }
        });
        return t;
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
        p.setBorder(new EmptyBorder(14,14,14,14));
        return p;
    }

    // ── Renderers ─────────────────────────────────────────────

    static class MasterRowRenderer extends DefaultTableCellRenderer {
        static final Color LOW_BG = new Color(0xFFF5F5);
        @Override public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            String status = t.getModel().getValueAt(row, 4)==null ? "ok" : t.getModel().getValueAt(row, 4).toString();
            if (!sel) {
                c.setBackground("low".equals(status) ? LOW_BG : Color.WHITE);
            }
            c.setFont("low".equals(status) && col==1 ? new Font("Segoe UI",Font.BOLD,13) : AppTheme.FONT_BODY);
            c.setBorder(new EmptyBorder(0,12,0,12));
            return c;
        }
    }

    static class StatusDotRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            String status = val==null ? "ok" : val.toString();
            boolean isLow = "low".equals(status);

            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            p.setBackground(isLow ? new Color(0xFFF5F5) : Color.WHITE);

            JLabel dot = new JLabel("●");
            dot.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            dot.setForeground(isLow ? new Color(0xE63946) : new Color(0x52B788));

            JLabel lbl = new JLabel(isLow ? "Tồn thấp" : "");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(isLow ? new Color(0xE63946) : new Color(0x52B788));

            p.add(dot); if (isLow) p.add(lbl);
            return p;
        }
    }

    // ── Line mini chart ───────────────────────────────────────

    static class LineMiniChart extends JPanel {
        private final int[] values = {18,25,20,12,28,20,24,19};
        private final String[] labels = {"Th10","Th11","Th2","Th3","Th1","Th2","Th3","Apr"};

        LineMiniChart() { setOpaque(false); setPreferredSize(new Dimension(0, 150)); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int padL=30,padR=10,padT=14,padB=26;
            int W=getWidth()-padL-padR, H=getHeight()-padT-padB;
            int maxV=30, n=values.length;
            if (n<2){g2.dispose();return;}

            // Grid
            g2.setColor(new Color(0xF3F4F6));
            for (int i=0;i<=5;i++) { int y=padT+H*i/5; g2.drawLine(padL,y,padL+W,y); }
            g2.setColor(AppTheme.TEXT_MUTED);
            g2.setFont(new Font("Segoe UI",Font.PLAIN,9));
            for (int i=0;i<=5;i++) { int v=(5-i)*maxV/5; int y=padT+H*i/5; g2.drawString(String.valueOf(v),2,y+4); }

            int[]xs=new int[n], ys=new int[n];
            for (int i=0;i<n;i++) {
                xs[i]=padL+i*W/(n-1);
                ys[i]=padT+H-(int)((double)values[i]/maxV*H);
            }
            // area
            int[] fx=new int[n+2], fy=new int[n+2];
            System.arraycopy(xs,0,fx,0,n); System.arraycopy(ys,0,fy,0,n);
            fx[n]=xs[n-1];fy[n]=padT+H; fx[n+1]=xs[0];fy[n+1]=padT+H;
            g2.setColor(new Color(0xE63946));
            Composite orig = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
            g2.fillPolygon(fx,fy,n+2);
            g2.setComposite(orig);

            // line
            g2.setColor(new Color(0xE63946));
            g2.setStroke(new BasicStroke(2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            for (int i=0;i<n-1;i++) g2.drawLine(xs[i],ys[i],xs[i+1],ys[i+1]);

            // dots
            for (int i=0;i<n;i++) {
                g2.setColor(Color.WHITE); g2.fillOval(xs[i]-4,ys[i]-4,8,8);
                g2.setColor(new Color(0xE63946)); g2.drawOval(xs[i]-4,ys[i]-4,8,8);
                g2.setColor(AppTheme.TEXT_MUTED);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,9));
                g2.drawString(labels[i], xs[i]-g2.getFontMetrics().stringWidth(labels[i])/2, padT+H+16);
            }

            // Y label
            g2.setColor(AppTheme.TEXT_SECONDARY);
            g2.setFont(new Font("Segoe UI",Font.PLAIN,9));
            g2.drawString("Lít",2,padT-2);
            g2.dispose();
        }
    }
}
