package com.example.winfinal.view;

import com.example.winfinal.controller.*;
import com.example.winfinal.dto.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Report – tận dụng TẤT CẢ queries từ DAO:
 * [2.5] YieldByFarm          → Grouped bar chart (card 1)
 * [6.4] CustomerYieldStats   → Customer ranking table (card 2)
 * [6.6] CostEstimateByLot    → Finance table (card 3)
 * [3.3] TotalCostBySupplier  → Cost chart (card 3 bottom)
 * [6.2] TopYieldingLots      → Top lots table
 * [6.5] MonthlyRainStats     → Weather chart (card 4)
 */
public class ReportView extends JPanel {

    private final HarvestRecordController harvestCtrl = new HarvestRecordController();
    private final SupplyImportController  importCtrl  = new SupplyImportController();
    private final ProductionLotController lotCtrl     = new ProductionLotController();
    private final WeatherLogController    weatherCtrl = new WeatherLogController();

    private DefaultTableModel customerModel, financeModel;
    private BarChartLive farmYieldChart;
    private WeatherLiveChart weatherChart;

    public ReportView() {
        setLayout(new BorderLayout(0,0));
        setBackground(AppTheme.BG_MAIN);
        buildUi();
        loadData();
    }

    private void buildUi() {
        removeAll();
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildGrid(),   BorderLayout.CENTER);
    }

    // ── Top bar ───────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()){
            @Override protected void paintComponent(Graphics g){
                g.setColor(new Color(0x2D6A4F));
                g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0,50));
        bar.setBorder(new EmptyBorder(0,18,0,18));
        JLabel title=new JLabel("Báo cáo & Phân tích");
        title.setFont(new Font("Segoe UI",Font.BOLD,17));
        title.setForeground(Color.WHITE);
        JButton btnRefresh=new JButton("Làm mới");
        btnRefresh.setFont(AppTheme.FONT_BODY);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(0x40916C));
        btnRefresh.setBorderPainted(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e->loadData());
        JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,12));
        right.setOpaque(false); right.add(btnRefresh);
        bar.add(title,BorderLayout.WEST);
        bar.add(right,BorderLayout.EAST);
        return bar;
    }

    // ── 2×2 grid ─────────────────────────────────────────────

    private JPanel buildGrid() {
        JPanel grid=new JPanel(new GridLayout(2,2,12,12));
        grid.setBackground(AppTheme.BG_MAIN);
        grid.setBorder(new EmptyBorder(14,14,14,14));

        // Card 1 – [2.5] Sản lượng theo trang trại
        JPanel c1=makeCard();
        c1.setLayout(new BorderLayout(0,8));
        JLabel t1=new JLabel("Báo cáo Hiệu Quả  (Sản lượng theo trang trại)");
        t1.setFont(AppTheme.FONT_SUBTITLE); t1.setForeground(AppTheme.TEXT_PRIMARY);
        farmYieldChart=new BarChartLive();
        c1.add(t1,BorderLayout.NORTH);
        c1.add(farmYieldChart,BorderLayout.CENTER);
        grid.add(c1);

        // Card 2 – [6.4] Khách hàng
        JPanel c2=makeCard();
        c2.setLayout(new BorderLayout(0,8));
        JPanel h2=new JPanel(new BorderLayout());
        h2.setOpaque(false);
        JLabel t2=new JLabel("Báo cáo khách hàng");
        t2.setFont(AppTheme.FONT_SUBTITLE); t2.setForeground(AppTheme.TEXT_PRIMARY);
        JButton btnDetail=new JButton("Xem chi tiết");
        btnDetail.setFont(AppTheme.FONT_SMALL);
        btnDetail.setForeground(new Color(0x2D6A4F));
        btnDetail.setBorder(BorderFactory.createLineBorder(new Color(0x2D6A4F),1,true));
        btnDetail.setContentAreaFilled(false); btnDetail.setFocusPainted(false);
        btnDetail.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        h2.add(t2,BorderLayout.WEST); h2.add(btnDetail,BorderLayout.EAST);
        String[] custCols={"#","Khách hàng","Số lượng (kg)","Số đơn hàng"};
        customerModel=new DefaultTableModel(custCols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        JTable tCust=buildTable(customerModel, true);
        tCust.getColumnModel().getColumn(0).setMinWidth(50);
        tCust.getColumnModel().getColumn(0).setMaxWidth(50);
        tCust.getColumnModel().getColumn(0).setPreferredWidth(50);
        JScrollPane sCust=new JScrollPane(tCust);
        sCust.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT,1));
        sCust.getViewport().setBackground(Color.WHITE);
        c2.add(h2,BorderLayout.NORTH);
        c2.add(sCust,BorderLayout.CENTER);
        grid.add(c2);

        // Card 3 – [6.6] Tài chính theo lô
        JPanel c3=makeCard();
        c3.setLayout(new BorderLayout(0,8));
        JPanel h3=new JPanel(new BorderLayout(0,2)); h3.setOpaque(false);
        JLabel t3=new JLabel("Báo cáo Tài chính (Ước tính)");
        t3.setFont(AppTheme.FONT_SUBTITLE); t3.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel sub3=new JLabel("Chi phí vật tư & sản lượng các lô hàng đầu");
        sub3.setFont(AppTheme.FONT_SMALL); sub3.setForeground(AppTheme.TEXT_SECONDARY);
        h3.add(t3,BorderLayout.NORTH); h3.add(sub3,BorderLayout.CENTER);
        String[] finCols={"Lô","Loại cây","Sản lượng (kg)"};
        financeModel=new DefaultTableModel(finCols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        JTable tFin=buildTable(financeModel, false);
        tFin.getColumnModel().getColumn(0).setMinWidth(70);
        tFin.getColumnModel().getColumn(0).setMaxWidth(70);
        tFin.getColumnModel().getColumn(0).setPreferredWidth(70);
        JScrollPane sFin=new JScrollPane(tFin);
        sFin.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT,1));
        sFin.getViewport().setBackground(Color.WHITE);
        JButton btnExport=buildExportBtn();
        JPanel btnRow=new JPanel(new FlowLayout(FlowLayout.CENTER,0,8));
        btnRow.setOpaque(false); btnRow.add(btnExport);
        JPanel c3center=new JPanel(new BorderLayout(0,6));
        c3center.setOpaque(false);
        c3center.add(sFin,BorderLayout.CENTER);
        c3center.add(btnRow,BorderLayout.SOUTH);
        c3.add(h3,BorderLayout.NORTH);
        c3.add(c3center,BorderLayout.CENTER);
        grid.add(c3);

        // Card 4 – [3.3] Chi phí nhà cung cấp / Weather
        JPanel c4=makeCard();
        c4.setLayout(new BorderLayout(0,8));
        JLabel t4=new JLabel("Báo cáo Chi phí theo Nhà cung cấp");
        t4.setFont(AppTheme.FONT_SUBTITLE); t4.setForeground(AppTheme.TEXT_PRIMARY);
        weatherChart=new WeatherLiveChart();
        c4.add(t4,BorderLayout.NORTH);
        c4.add(weatherChart,BorderLayout.CENTER);
        grid.add(c4);

        return grid;
    }

    // ── Load data via SwingWorker ─────────────────────────────

    private void loadData() {
        new SwingWorker<Void,Void>(){
            List<Object[]> yieldByFarm, custStats, topLots, costBySupplier, costByLot;

            @Override
            protected Void doInBackground(){
                // [2.5] Sản lượng theo trang trại
                try{ yieldByFarm=harvestCtrl.getYieldByFarm(); }catch(Exception e){yieldByFarm=List.of();}
                // [6.4] Thống kê khách hàng
                try{ custStats=harvestCtrl.getCustomerYieldStats(); }catch(Exception e){custStats=List.of();}
                // [6.2] Top lô sản lượng cao
                try{ topLots=lotCtrl.getTopYieldingLots(6); }catch(Exception e){topLots=List.of();}
                // [3.3] Chi phí nhà cung cấp
                try{ costBySupplier=importCtrl.getTotalCostBySupplier(); }catch(Exception e){costBySupplier=List.of();}
                // [6.6] Ước tính chi phí theo lô
                try{ costByLot=importCtrl.getCostEstimateByLot(); }catch(Exception e){costByLot=List.of();}
                return null;
            }

            @Override
            protected void done(){
                // [2.5] → Bar chart (farm yield)
                if(yieldByFarm!=null && !yieldByFarm.isEmpty()){
                    int n=yieldByFarm.size();
                    String[] lbl=new String[n]; double[] val=new double[n];
                    for(int i=0;i<n;i++){
                        lbl[i]=yieldByFarm.get(i)[0]==null?"N/A":yieldByFarm.get(i)[0].toString();
                        val[i]=yieldByFarm.get(i)[1]==null?0:((Number)yieldByFarm.get(i)[1]).doubleValue();
                    }
                    farmYieldChart.setData(lbl,val);
                } else {
                    farmYieldChart.setData(new String[]{"Chưa có dữ liệu"}, new double[]{0});
                }

                // [6.4] → Customer table
                customerModel.setRowCount(0);
                if(custStats!=null){
                    for(int i=0;i<custStats.size();i++){
                        Object[] r=custStats.get(i);
                        String name=r[0]==null?"N/A":r[0].toString();
                        String qty =r[1]==null?"0":String.format("%,.0f",((Number)r[1]).doubleValue());
                        String cnt =r[2]==null?"0":r[2].toString();
                        customerModel.addRow(new Object[]{"#"+(i+1), name, qty, cnt});
                    }
                }
                if(customerModel.getRowCount()==0)
                    customerModel.addRow(new Object[]{"","Chưa có dữ liệu","",""});

                // [6.2] → Finance table (top lots)
                financeModel.setRowCount(0);
                if(topLots!=null){
                    for(Object[] r:topLots){
                        financeModel.addRow(new Object[]{
                            r[0]==null?"":r[0],
                            r[1]==null?"":r[1],
                            r[2]==null?"0":String.format("%,.0f",((Number)r[2]).doubleValue())
                        });
                    }
                }
                if(financeModel.getRowCount()==0)
                    financeModel.addRow(new Object[]{"Chưa có dữ liệu","",""});

                // [3.3] → Weather/supplier cost chart
                if(costBySupplier!=null && !costBySupplier.isEmpty()){
                    int n=costBySupplier.size();
                    String[]lbl=new String[n]; double[]val=new double[n];
                    for(int i=0;i<n;i++){
                        lbl[i]=costBySupplier.get(i)[0]==null?"?":costBySupplier.get(i)[0].toString();
                        val[i]=costBySupplier.get(i)[1]==null?0:((Number)costBySupplier.get(i)[1]).doubleValue();
                    }
                    weatherChart.setData(lbl,val);
                }
            }
        }.execute();
    }

    // ── Helpers ───────────────────────────────────────────────

    private JPanel makeCard(){
        JPanel p=new JPanel(){
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
        p.setBorder(new EmptyBorder(14,16,14,16));
        return p;
    }

    private JTable buildTable(DefaultTableModel model, boolean ranked){
        JTable t=new JTable(model);
        t.setFont(AppTheme.FONT_BODY); t.setRowHeight(32);
        t.setShowVerticalLines(false); t.setShowHorizontalLines(true);
        t.setGridColor(new Color(0xF3F4F6));
        t.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,12));
        t.getTableHeader().setBackground(new Color(0xF9FAFB));
        t.getTableHeader().setForeground(AppTheme.TEXT_SECONDARY);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable tb,Object val,
                    boolean sel,boolean foc,int row,int col){
                Component c=super.getTableCellRendererComponent(tb,val,sel,foc,row,col);
                if(!sel) c.setBackground(row%2==0?Color.WHITE:new Color(0xF9FAFB));
                ((JLabel)c).setBorder(new EmptyBorder(0,10,0,10));
                return c;
            }
        });
        return t;
    }

    private JButton buildExportBtn(){
        JButton b=new JButton("Xuất báo cáo"){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?new Color(0x1B4332):new Color(0x2D6A4F));
                g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),8,8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI",Font.BOLD,13));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(160,36));
        b.addActionListener(e->exportReport());
        return b;
    }

    private void exportReport(){
        // Build simple text report from finance table
        StringBuilder sb=new StringBuilder();
        sb.append("=== XUẤT BÁO CÁO AGRICHAIN ===\n");
        sb.append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date())).append("\n\n");
        sb.append("TOP LÔ SẢN LƯỢNG CAO:\n");
        for(int r=0;r<financeModel.getRowCount();r++){
            sb.append(String.format("  %-12s %-18s %s kg%n",
                financeModel.getValueAt(r,0),financeModel.getValueAt(r,1),financeModel.getValueAt(r,2)));
        }
        sb.append("\nKHÁCH HÀNG HÀNG ĐẦU:\n");
        for(int r=0;r<customerModel.getRowCount();r++){
            sb.append(String.format("  %s %-20s %s kg%n",
                customerModel.getValueAt(r,0),customerModel.getValueAt(r,1),customerModel.getValueAt(r,2)));
        }
        JTextArea area=new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced",Font.PLAIN,12));
        area.setEditable(false);
        JScrollPane sp=new JScrollPane(area);
        sp.setPreferredSize(new Dimension(500,350));
        JOptionPane.showMessageDialog(this,sp,"Xuất báo cáo",JOptionPane.PLAIN_MESSAGE);
    }

    // ══════════════════════════════════════════════════════════
    // Chart panels (live-updatable)
    // ══════════════════════════════════════════════════════════

    /** Grouped/horizontal bar chart for farm yield */
    static class BarChartLive extends JPanel {
        private String[] labels={}; private double[] values={};
        private final Color[] COLORS={
            new Color(0x2D6A4F),new Color(0xE88C2A),new Color(0x4895EF),
            new Color(0x9B72CF),new Color(0x52B788),new Color(0xE63946)};
        BarChartLive(){setOpaque(false);}
        void setData(String[] l,double[] v){labels=l;values=v;repaint();}

        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if(labels==null||labels.length==0){return;}
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int padL=46,padR=8,padT=20,padB=34;
            int W=getWidth()-padL-padR, H=getHeight()-padT-padB;
            int n=labels.length;
            double maxV=Arrays.stream(values).max().orElse(1);
            int barGap=10, barW=Math.max(8,W/n-barGap);

            // Y grid
            g2.setFont(new Font("Segoe UI",Font.PLAIN,9));
            for(int i=0;i<=4;i++){
                int y=padT+H*i/4; g2.setColor(new Color(0xEEEEEE)); g2.drawLine(padL,y,padL+W,y);
                g2.setColor(AppTheme.TEXT_SECONDARY);
                String lbl=String.format("%.0f",maxV*(4-i)/4);
                g2.drawString(lbl,2,y+4);
            }
            g2.setColor(AppTheme.TEXT_SECONDARY);
            g2.drawString("Sản lượng",padL,padT-4);

            for(int i=0;i<n;i++){
                int bH=(int)(values[i]/maxV*H);
                int x=padL+i*(barW+barGap), y=padT+H-bH;
                g2.setColor(COLORS[i%COLORS.length]);
                g2.fill(new RoundRectangle2D.Double(x,y,barW,bH,5,5));
                // label
                g2.setColor(AppTheme.TEXT_PRIMARY); g2.setFont(new Font("Segoe UI",Font.BOLD,10));
                String vs=String.format("%.0f",values[i]);
                g2.drawString(vs,x+(barW-g2.getFontMetrics().stringWidth(vs))/2,y-3);
                // x label
                g2.setColor(AppTheme.TEXT_SECONDARY); g2.setFont(new Font("Segoe UI",Font.PLAIN,9));
                String lbl=labels[i].length()>10?labels[i].substring(0,9)+"…":labels[i];
                g2.drawString(lbl,x+(barW-g2.getFontMetrics().stringWidth(lbl))/2,padT+H+16);
            }
            g2.dispose();
        }
    }

    /** Bar chart for cost-by-supplier */
    static class WeatherLiveChart extends JPanel {
        private String[] labels={}; private double[] values={};
        private final Color[] COLORS={new Color(0x4895EF),new Color(0x52B788),new Color(0xFFB833),new Color(0xE63946),new Color(0x9B72CF)};
        WeatherLiveChart(){setOpaque(false);}
        void setData(String[] l,double[] v){labels=l;values=v;repaint();}

        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if(labels==null||labels.length==0){
                g.setColor(AppTheme.TEXT_MUTED);g.setFont(AppTheme.FONT_SMALL);g.drawString("Đang tải...",20,getHeight()/2);return;
            }
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int padL=46,padR=8,padT=14,padB=36;
            int W=getWidth()-padL-padR,H=getHeight()-padT-padB;
            int n=labels.length;
            double maxV=Arrays.stream(values).max().orElse(1);
            int barGap=8,barW=Math.max(8,W/n-barGap);
            for(int i=0;i<=4;i++){
                int y=padT+H*i/4; g2.setColor(new Color(0xEEEEEE)); g2.drawLine(padL,y,padL+W,y);
                g2.setColor(AppTheme.TEXT_SECONDARY); g2.setFont(new Font("Segoe UI",Font.PLAIN,9));
                g2.drawString(String.format("%.0f",maxV*(4-i)/4),2,y+4);
            }
            g2.setColor(AppTheme.TEXT_SECONDARY);
            g2.setFont(new Font("Segoe UI",Font.PLAIN,9));
            g2.drawString("Triệu VND",padL,padT-2);
            for(int i=0;i<n;i++){
                int bH=(int)(values[i]/maxV*H); int x=padL+i*(barW+barGap),y=padT+H-bH;
                g2.setColor(COLORS[i%COLORS.length]);
                g2.fill(new RoundRectangle2D.Double(x,y,barW,bH,5,5));
                g2.setColor(AppTheme.TEXT_PRIMARY); g2.setFont(new Font("Segoe UI",Font.BOLD,10));
                String vs=String.format("%.0ftr",values[i]/1000000);
                g2.drawString(vs,x+(barW-g2.getFontMetrics().stringWidth(vs))/2,y-3);
                g2.setColor(AppTheme.TEXT_SECONDARY); g2.setFont(new Font("Segoe UI",Font.PLAIN,8));
                String lbl=labels[i].length()>10?labels[i].substring(0,9)+"…":labels[i];
                g2.drawString(lbl,x+(barW-g2.getFontMetrics().stringWidth(lbl))/2,padT+H+18);
            }
            g2.dispose();
        }
    }
}
