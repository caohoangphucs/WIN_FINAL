package com.example.winfinal.view;

import com.example.winfinal.controller.*;
import com.example.winfinal.dto.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * Dashboard: dùng tối đa queries từ DAO.
 * [2.1] AvgYieldByCropType → BarChart
 * [2.4] QualityGradeStats  → PieChart
 * [2.2] YieldBySeason      → LineChart
 * [3.1] LowStock + [2.6] UpcomingHarvest → Alert table
 */
public class DashboardView extends JPanel {

    private final HarvestRecordController  harvestCtrl = new HarvestRecordController();
    private final AgriSupplyController     supplyCtrl  = new AgriSupplyController();
    private final ProductionLotController  lotCtrl     = new ProductionLotController();
    private final PestReportController     pestCtrl    = new PestReportController();

    private DefaultTableModel alertModel;

    // Chart data holders (populated async)
    private String[] yieldLabels  = {};
    private double[] yieldValues  = {};
    private Color[]  yieldColors  = {new Color(0x52B788),new Color(0xFFB833),new Color(0x9B72CF),new Color(0x4895EF),new Color(0xE63946)};
    private String[] gradeLabels  = {};
    private double[] gradeValues  = {};
    private String[] seasonLabels = {};
    private double[] seasonValues = {};

    private BarChartPanel   barChart;
    private PieChartPanel   pieChart;
    private LineChartPanel  lineChart;

    public DashboardView() {
        setLayout(new BorderLayout(0,0));
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(20,20,20,20));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
        loadAllData();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0,0,16,0));
        JLabel title = new JLabel("Bảng điều khiển");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel date = new JLabel(new java.text.SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi","VN")).format(new Date()));
        date.setFont(AppTheme.FONT_BODY);
        date.setForeground(AppTheme.TEXT_SECONDARY);
        JButton btnRefresh = UiUtils.createSecondaryButton("Làm mới");
        btnRefresh.addActionListener(e -> loadAllData());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(date); right.add(btnRefresh);
        p.add(title, BorderLayout.WEST);
        p.add(right,  BorderLayout.EAST);
        return p;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0,14));
        body.setOpaque(false);
        body.add(buildChartRow(),   BorderLayout.NORTH);
        body.add(buildAlertTable(), BorderLayout.CENTER);
        return body;
    }

    // ── Chart row: 3 cards ────────────────────────────────────

    private JPanel buildChartRow() {
        JPanel row = new JPanel(new GridLayout(1,3,12,0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0,270));

        // [2.1] Năng suất trung bình theo loại cây
        JPanel c1 = makeCard();
        c1.setLayout(new BorderLayout(0,6));
        c1.add(cardTitle("Năng suất theo loại cây trồng", "Tấn"), BorderLayout.NORTH);
        barChart = new BarChartPanel(new String[]{}, new double[]{}, yieldColors);
        c1.add(barChart, BorderLayout.CENTER);
        row.add(c1);

        // [2.4] Tỉ lệ chất lượng
        JPanel c2 = makeCard();
        c2.setLayout(new BorderLayout(0,6));
        c2.add(cardTitle("Tỉ lệ xếp loại chất lượng", null), BorderLayout.NORTH);
        pieChart = new PieChartPanel(new String[]{}, new double[]{},
                new Color[]{new Color(0x52B788),new Color(0xFFB833),new Color(0xE63946)});
        c2.add(pieChart, BorderLayout.CENTER);
        row.add(c2);

        // [2.2] Sản lượng theo mùa vụ
        JPanel c3 = makeCard();
        c3.setLayout(new BorderLayout(0,6));
        c3.add(cardTitle("Sản lượng qua các mùa vụ", "Tấn"), BorderLayout.NORTH);
        lineChart = new LineChartPanel(new String[]{}, new double[]{});
        c3.add(lineChart, BorderLayout.CENTER);
        row.add(c3);

        return row;
    }

    private JLabel cardTitle(String text, String unitSuffix) {
        String fullText = (unitSuffix == null || unitSuffix.isEmpty()) ? text : text + " (" + unitSuffix + ")";
        JLabel l = new JLabel(fullText);
        l.setFont(AppTheme.FONT_SUBTITLE);
        l.setForeground(AppTheme.TEXT_PRIMARY);
        return l;
    }

    // ── Alert table ───────────────────────────────────────────

    private JPanel buildAlertTable() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout(0,10));
        JLabel title = new JLabel("Cảnh báo cần chú ý");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        card.add(title, BorderLayout.NORTH);

        String[] cols = {"Loại","Lô / Vật tư","Vật tư","Trạng thái","Ngày"};
        alertModel = new DefaultTableModel(cols,0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        JTable table = new JTable(alertModel);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(AppTheme.BORDER_LIGHT);
        table.getTableHeader().setFont(AppTheme.FONT_SUBTITLE);
        table.getTableHeader().setBackground(AppTheme.BG_TABLE_HEADER);
        table.getTableHeader().setForeground(AppTheme.TEXT_PRIMARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,2,0,AppTheme.PRIMARY));
        table.setDefaultRenderer(Object.class, new AlertRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT,1));
        sc.getViewport().setBackground(Color.WHITE);
        card.add(sc, BorderLayout.CENTER);
        return card;
    }

    // ── Load all data via SwingWorker ─────────────────────────

    private void loadAllData() {
        SwingWorker<Void, Void> w = new SwingWorker<>() {
            // Chart data
            List<Object[]> cropYield, qualityGrade, seasonYield;
            // Alert rows
            final List<Object[]> alertRows = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                String today = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date());

                // [2.1] Năng suất trung bình theo loại cây
                try { cropYield = harvestCtrl.getAvgYieldByCropType(); }
                catch (Exception e) { cropYield = List.of(); }

                // [2.4] Tỉ lệ chất lượng
                try { qualityGrade = harvestCtrl.getQualityGradeStats(); }
                catch (Exception e) { qualityGrade = List.of(); }

                // [2.2] Sản lượng theo mùa vụ
                try { seasonYield = harvestCtrl.getYieldBySeason(); }
                catch (Exception e) { seasonYield = List.of(); }

                // [2.6] Lô sắp thu hoạch (14 ngày tới)
                try {
                    Calendar c = Calendar.getInstance(); c.add(Calendar.DATE, 14);
                    var harvest = lotCtrl.getUpcomingHarvest(c.getTime());
                    for (var l : harvest)
                        alertRows.add(new Object[]{"LOT", l.getLotCode(), l.getLotCode(), "Sắp thu hoạch", today});
                } catch (Exception ignored) {}

                // [3.1] Vật tư sắp hết kho
                try {
                    var low = supplyCtrl.getLowStockSupplies();
                    for (var s : low) {
                        double ratio = (s.getStockQty() != null && s.getMinStock() != null && s.getMinStock() > 0)
                                ? s.getStockQty() / s.getMinStock() : 0;
                        String statusLabel = ratio == 0 ? "Tồn kho sắp hết" : "Tồn kho thấp";
                        alertRows.add(new Object[]{"VT", s.getName(), s.getName(), statusLabel, today});
                    }
                } catch (Exception ignored) {}

                // [4.2] Cảnh báo sâu bệnh nghiêm trọng
                try {
                    var pests = pestCtrl.getHighSeverityReports();
                    for (var p : pests) {
                        String lotCodeValue = p.getLotCode() != null ? p.getLotCode() : "--";
                        alertRows.add(new Object[]{"SAU", "Lô #"+lotCodeValue, "", "Sâu bệnh nghiêm trọng", today});
                    }
                } catch (Exception ignored) {}

                return null;
            }

            @Override
            protected void done() {
                // Update bar chart [2.1]
                if (cropYield != null && !cropYield.isEmpty()) {
                    int n = cropYield.size();
                    yieldLabels = new String[n];
                    yieldValues = new double[n];
                    for (int i=0;i<n;i++) {
                        yieldLabels[i] = cropYield.get(i)[0] == null ? "?" : cropYield.get(i)[0].toString();
                        yieldValues[i] = cropYield.get(i)[1] == null ? 0 : ((Number)cropYield.get(i)[1]).doubleValue();
                    }
                    barChart.setData(yieldLabels, yieldValues);
                }

                // Update pie chart [2.4]
                if (qualityGrade != null) {
                    Map<String, Long> map = new LinkedHashMap<>();
                    map.put("Loại A", 0L);
                    map.put("Loại B", 0L);
                    map.put("Loại C", 0L);

                    for (int i = 0; i < qualityGrade.size(); i++) {
                        String g = qualityGrade.get(i)[0] == null ? "C" : qualityGrade.get(i)[0].toString().toUpperCase();
                        long cnt = qualityGrade.get(i)[1] == null ? 0 : ((Number)qualityGrade.get(i)[1]).longValue();
                        if (g.endsWith("A") || g.equals("A")) map.put("Loại A", map.get("Loại A") + cnt);
                        else if (g.endsWith("B") || g.equals("B")) map.put("Loại B", map.get("Loại B") + cnt);
                        else map.put("Loại C", map.get("Loại C") + cnt);
                    }

                    gradeLabels = new String[]{"Loại A", "Loại B", "Loại C"};
                    gradeValues = new double[3];
                    long total = map.values().stream().mapToLong(Long::longValue).sum();

                    gradeValues[0] = map.get("Loại A");
                    gradeValues[1] = map.get("Loại B");
                    gradeValues[2] = map.get("Loại C");

                    // convert to %
                    if (total > 0) {
                        for (int i = 0; i < 3; i++) gradeValues[i] = gradeValues[i] / total * 100.0;
                    }
                    pieChart.setData(gradeLabels, gradeValues);
                }

                // Update line chart [2.2]
                if (seasonYield != null && !seasonYield.isEmpty()) {
                    int n = seasonYield.size();
                    seasonLabels = new String[n];
                    seasonValues = new double[n];
                    for (int i=0;i<n;i++) {
                        seasonLabels[i] = seasonYield.get(i)[0] == null ? "?" : seasonYield.get(i)[0].toString();
                        seasonValues[i] = seasonYield.get(i)[1] == null ? 0 : ((Number)seasonYield.get(i)[1]).doubleValue();
                    }
                    lineChart.setData(seasonLabels, seasonValues);
                }

                // Update alert table
                alertModel.setRowCount(0);
                if (alertRows.isEmpty()) {
                    alertModel.addRow(new Object[]{"", "Hệ thống bình thường - Không có cảnh báo", "", "", ""});
                } else {
                    alertRows.forEach(r -> alertModel.addRow(r));
                }
            }
        };
        w.execute();
    }

    // ── Helpers ───────────────────────────────────────────────

    private JPanel makeCard() {
        JPanel p = new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),12,12));
                g2.setColor(AppTheme.BORDER_LIGHT);
                g2.draw(new RoundRectangle2D.Double(0,0,getWidth()-1,getHeight()-1,12,12));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14,14,14,14));
        return p;
    }

    // ── Alert renderer ────────────────────────────────────────

    static class AlertRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t,Object val,
                boolean sel,boolean foc,int row,int col){
            JLabel c=(JLabel)super.getTableCellRendererComponent(t,val,sel,foc,row,col);
            Object type=t.getModel().getValueAt(row,0);
            boolean vt="VT".equals(type), sau="SAU".equals(type);
            if(!sel){
                c.setBackground(sau?new Color(0xFFE4E6):vt?new Color(0xFEF9C3):Color.WHITE);
            }
            if(col==0){
                c.setHorizontalAlignment(CENTER);
                c.setFont(new Font("Segoe UI",Font.BOLD,13));
                c.setForeground("SAU".equals(type)?AppTheme.DANGER:"VT".equals(type)?new Color(0xB45309):AppTheme.TEXT_SECONDARY);
                c.setText("VT".equals(type)?"!":"SAU".equals(type)?"!":"LOT".equals(type)?"⏰":"");
            } else {
                c.setHorizontalAlignment(LEFT);
                c.setFont(AppTheme.FONT_BODY);
                if(col==3&&sau) c.setForeground(AppTheme.DANGER);
                else if(col==3&&vt) c.setForeground(new Color(0x92400E));
                else c.setForeground(AppTheme.TEXT_PRIMARY);
            }
            c.setBorder(new EmptyBorder(0,10,0,10));
            return c;
        }
    }

    // ══════════════════════════════════════════════════════════
    // Mutable chart inner classes
    // ══════════════════════════════════════════════════════════

    static class BarChartPanel extends JPanel {
        private String[] labels; private double[] values; private final Color[] colors;
        BarChartPanel(String[] l, double[] v, Color[] c){labels=l;values=v;colors=c;setOpaque(false);}
        void setData(String[] l, double[] v){labels=l;values=v;repaint();}

        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if(labels==null||labels.length==0){drawEmpty(g);return;}
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int padL=44,padR=8,padT=20,padB=30;
            int W=getWidth()-padL-padR, H=getHeight()-padT-padB;
            int n=labels.length;
            double maxV=Arrays.stream(values).max().orElse(1) * 1.15; // padding for top value
            int barW=Math.max(8,(W/n)-16);
            if(maxV==0) maxV=1;
            
            // gridlines + Y labels
            g2.setFont(new Font("Segoe UI",Font.PLAIN,10));
            for(int i=0;i<=4;i++){
                int y=padT+H*i/4;
                g2.setColor(new Color(0xEEEEEE)); g2.drawLine(padL,y,padL+W,y);
                g2.setColor(AppTheme.TEXT_SECONDARY);
                double val=maxV*(4-i)/4;
                g2.drawString(String.format("%.0f",val),2,y+4);
            }
            for(int i=0;i<n;i++){
                int barH=(int)((values[i]/maxV)*H);
                int x=padL+i*(W/n)+(W/n - barW)/2;
                int y=padT+H-barH;
                g2.setColor(colors[i%colors.length]);
                g2.fill(new RoundRectangle2D.Double(x,y,barW,barH,5,5));
                // value
                g2.setColor(AppTheme.TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI",Font.BOLD,10));
                String vs=String.format("%.0f kg",values[i]);
                g2.drawString(vs,x+(barW-g2.getFontMetrics().stringWidth(vs))/2,y-4);
                // label
                g2.setColor(AppTheme.TEXT_SECONDARY);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,10));
                String lbl=labels[i].length()>14?labels[i].substring(0,13)+"…":labels[i];
                g2.drawString(lbl,x+(barW-g2.getFontMetrics().stringWidth(lbl))/2,padT+H+16);
            }
            g2.dispose();
        }
        private void drawEmpty(Graphics g){
            g.setColor(AppTheme.TEXT_MUTED);
            g.setFont(AppTheme.FONT_SMALL);
            g.drawString("Đang tải...",20,getHeight()/2);
        }
    }

    static class PieChartPanel extends JPanel {
        private String[] labels; private double[] values; private final Color[] colors;
        PieChartPanel(String[] l,double[] v,Color[] c){labels=l;values=v;colors=c;setOpaque(false);}
        void setData(String[] l, double[] v){labels=l;values=v;repaint();}

        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if(values==null||values.length==0){
                g.setColor(AppTheme.TEXT_MUTED);g.setFont(AppTheme.FONT_SMALL);g.drawString("Đang tải...",20,getHeight()/2);return;
            }
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int legH=24;
            int size=Math.min(getWidth()-20,getHeight()-legH-24);
            size=Math.max(size,40);
            int cx=(getWidth()-size)/2, cy=4;
            double total=Arrays.stream(values).sum();
            if(total==0){
                g2.setColor(AppTheme.TEXT_MUTED);
                g2.setFont(AppTheme.FONT_SMALL);
                String msg = "Chưa có dữ liệu";
                g2.drawString(msg, cx + size/2 - g2.getFontMetrics().stringWidth(msg)/2, cy + size/2);
                g2.dispose();
                return;
            }
            double start=-90;
            for(int i=0;i<values.length;i++){
                double sweep=values[i]/total*360;
                if (sweep > 0) {
                    g2.setColor(colors[i%colors.length]);
                    g2.fill(new Arc2D.Double(cx,cy,size,size,start,sweep,Arc2D.PIE));
                }
                start+=sweep;
            }
            // Legend
            int legY=cy+size+12;
            int legWidth = labels.length * 90;
            int legX = cx + size/2 - legWidth/2;
            if(legX<4) legX=4;
            g2.setFont(new Font("Segoe UI",Font.PLAIN,11));
            for(int i=0;i<labels.length;i++){
                g2.setColor(colors[i%colors.length]); g2.fillOval(legX,legY-2,10,10);
                g2.setColor(AppTheme.TEXT_SECONDARY);
                g2.drawString(labels[i]+" "+String.format("%.0f",values[i])+"%",legX+14,legY+7);
                legX+=90;
            }
            g2.dispose();
        }
    }

    static class LineChartPanel extends JPanel {
        private String[] labels; private double[] values;
        LineChartPanel(String[] l,double[] v){labels=l;values=v;setOpaque(false);}
        void setData(String[] l,double[] v){labels=l;values=v;repaint();}

        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if(values==null||values.length<2){
                g.setColor(AppTheme.TEXT_MUTED);g.setFont(AppTheme.FONT_SMALL);g.drawString("Đang tải...",20,getHeight()/2);return;
            }
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int padL=44,padR=14,padT=20,padB=34;
            int W=getWidth()-padL-padR, H=getHeight()-padT-padB;
            int n=values.length;
            double maxV=Arrays.stream(values).max().orElse(1) * 1.15;
            if(maxV==0) maxV=1;
            int[]xs=new int[n],ys=new int[n];
            for(int i=0;i<n;i++){xs[i]=padL+i*W/(n-1);ys[i]=padT+H-(int)(values[i]/maxV*H);}
            // grid
            g2.setFont(new Font("Segoe UI",Font.PLAIN,10));
            for(int i=0;i<=4;i++){
                int y=padT+H*i/4;
                g2.setColor(new Color(0xEEEEEE)); g2.drawLine(padL,y,padL+W,y);
                g2.setColor(AppTheme.TEXT_SECONDARY);
                g2.drawString(String.format("%.0f",maxV*(4-i)/4),2,y+4);
            }
            // area
            int[]fx=new int[n+2],fy=new int[n+2];
            System.arraycopy(xs,0,fx,0,n);System.arraycopy(ys,0,fy,0,n);
            fx[n]=xs[n-1];fy[n]=padT+H;fx[n+1]=xs[0];fy[n+1]=padT+H;
            Composite orig=g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.18f));
            g2.setColor(new Color(0x4895EF));
            g2.fillPolygon(fx,fy,n+2);
            g2.setComposite(orig);
            // line
            g2.setColor(new Color(0x4895EF));
            g2.setStroke(new BasicStroke(2.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            for(int i=0;i<n-1;i++) g2.drawLine(xs[i],ys[i],xs[i+1],ys[i+1]);
            // dots + x-labels
            for(int i=0;i<n;i++){
                g2.setColor(Color.WHITE);g2.fillOval(xs[i]-5,ys[i]-5,10,10);
                g2.setColor(new Color(0x4895EF));g2.setStroke(new BasicStroke(2f));g2.drawOval(xs[i]-5,ys[i]-5,10,10);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,10));
                g2.setColor(AppTheme.TEXT_SECONDARY);
                String lbl = labels[i]; // Show full season name, do not truncate
                g2.drawString(lbl,xs[i]-g2.getFontMetrics().stringWidth(lbl)/2,padT+H+18);
            }
            g2.dispose();
        }
    }
}
