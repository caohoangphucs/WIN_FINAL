package com.example.winfinal.view;

import com.example.winfinal.controller.PestReportController;
import com.example.winfinal.controller.ProductionLotController;
import com.example.winfinal.dto.PestReportDTO;
import com.example.winfinal.dto.ProductionLotDTO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Pest Report screen with JFreeChart analytics and refined layout.
 */
public class PestReportView extends JPanel {

    private final PestReportController ctrl = new PestReportController();
    private final ProductionLotController lotCtrl = new ProductionLotController();

    private JTable table;
    private DefaultTableModel tableModel;
    private ChartPanel piePanel;
    private ChartPanel linePanel;

    private JComboBox<String> cbLot;
    private JComboBox<String> cbSeverity;
    private JTextField txtFromDate;

    public PestReportView() {
        setLayout(new BorderLayout(0, 16));
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        content.add(buildCharts(), BorderLayout.NORTH);
        content.add(buildTable(),  BorderLayout.CENTER);
        
        add(content, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(12, 10));
        p.setOpaque(false);
        p.add(UiUtils.createSectionTitle("Màn hình — Báo cáo Sâu bệnh (PestReport)"), BorderLayout.NORTH);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        filterBar.setOpaque(false);

        filterBar.add(new JLabel("Lô:"));
        cbLot = new JComboBox<>(new String[]{"Tất cả lô"});
        cbLot.setPreferredSize(new Dimension(140, 30));
        filterBar.add(cbLot);

        filterBar.add(new JLabel("Mức độ:"));
        cbSeverity = new JComboBox<>(new String[]{"Tất cả", "CRITICAL", "HIGH", "MEDIUM", "LOW"});
        cbSeverity.setPreferredSize(new Dimension(110, 30));
        filterBar.add(cbSeverity);

        filterBar.add(new JLabel("Từ ngày:"));
        txtFromDate = new JTextField("01/01/2025");
        txtFromDate.setPreferredSize(new Dimension(100, 30));
        filterBar.add(txtFromDate);

        JButton btnFilter = UiUtils.createPrimaryButton("Lọc");
        btnFilter.setPreferredSize(new Dimension(80, 32));
        btnFilter.addActionListener(e -> refreshData());
        filterBar.add(btnFilter);

        JButton btnAdd = UiUtils.createDangerButton("+ Báo cáo mới");
        btnAdd.setPreferredSize(new Dimension(140, 32));
        
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(btnAdd);

        p.add(filterBar, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);

        // Load lots async
        new SwingWorker<List<ProductionLotDTO>, Void>() {
            @Override protected List<ProductionLotDTO> doInBackground() { return lotCtrl.getAllLots(); }
            @Override protected void done() {
                try {
                    for (var l : get()) cbLot.addItem(l.getLotCode());
                } catch (Exception ignored) {}
            }
        }.execute();

        return p;
    }

    private JPanel buildCharts() {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 250));

        // Pie Chart
        DefaultPieDataset<String> pieDataset = new DefaultPieDataset<>();
        JFreeChart pieChart = ChartFactory.createPieChart("Tỉ lệ mức độ sâu bệnh", pieDataset, true, true, false);
        stylePieChart(pieChart);
        piePanel = new ChartPanel(pieChart);
        piePanel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT));
        row.add(piePanel);

        // Line Chart
        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
        JFreeChart lineChart = ChartFactory.createLineChart("Diễn biến sâu bệnh theo tháng", "Tháng", "Số vụ", lineDataset);
        styleLineChart(lineChart);
        linePanel = new ChartPanel(lineChart);
        linePanel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT));
        row.add(linePanel);

        return row;
    }

    private void stylePieChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        @SuppressWarnings("unchecked")
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setSectionPaint("CRITICAL", new Color(0xE11D48));
        plot.setSectionPaint("HIGH",     new Color(0xD97706));
        plot.setSectionPaint("MEDIUM",   new Color(0x2563EB));
        plot.setSectionPaint("LOW",      new Color(0x16A34A));
    }

    private void styleLineChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(AppTheme.BORDER_LIGHT);
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, AppTheme.DANGER);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        plot.setRenderer(renderer);
    }

    private JPanel buildTable() {
        String[] cols = {"Mã lô", "Tên loại cây", "Loại sâu bệnh", "Mức độ", "Nhân viên", "Ngày báo cáo"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UiUtils.styleTable(table);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object val, boolean isSelected,
                                                               boolean hasFocus, int row, int col) {
                    JLabel c = (JLabel) super.getTableCellRendererComponent(t, val, isSelected, hasFocus, row, col);
                    c.setHorizontalAlignment(SwingConstants.CENTER);
                    c.setBorder(new EmptyBorder(0, 10, 0, 10));
                    
                    if (col == 3) { // Severity badges
                        String sev = String.valueOf(val);
                        c.setText(sev);
                        c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        c.setForeground(severityColor(sev)[1]);
                        c.setBackground(severityColor(sev)[0]);
                        c.setOpaque(true);
                    } else {
                        c.setForeground(AppTheme.TEXT_PRIMARY);
                        c.setBackground(isSelected ? t.getSelectionBackground() : Color.WHITE);
                        c.setOpaque(true);
                    }
                    return c;
                }
            });
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private Color[] severityColor(String sev) {
        if (sev == null) return new Color[]{Color.WHITE, AppTheme.TEXT_SECONDARY};
        return switch (sev.toUpperCase()) {
            case "CRITICAL" -> new Color[]{new Color(0xFFF1F2), AppTheme.DANGER};
            case "HIGH"     -> new Color[]{new Color(0xFFF7ED), new Color(0x9A3412)};
            case "MEDIUM"   -> new Color[]{new Color(0xEFF6FF), new Color(0x1E40AF)};
            case "LOW"      -> new Color[]{new Color(0xF0FDF4), new Color(0x166534)};
            default         -> new Color[]{Color.WHITE, AppTheme.TEXT_SECONDARY};
        };
    }

    private void refreshData() {
        refreshTable();
        refreshCharts();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        new SwingWorker<List<PestReportDTO>, Void>() {
            @Override protected List<PestReportDTO> doInBackground() {
                // For simplicity, we fetch all and filter in memory here, 
                // but real app should use DAO filters
                return ctrl.getAllReports();
            }
            @Override protected void done() {
                try {
                    List<PestReportDTO> list = get();
                    String selLot = (String) cbLot.getSelectedItem();
                    String selSev = (String) cbSeverity.getSelectedItem();

                    for (var p : list) {
                        if (!"Tất cả lô".equals(selLot) && !selLot.equals(p.getLotCode())) continue;
                        if (!"Tất cả".equals(selSev) && !selSev.equals(p.getSeverityCode())) continue;

                        tableModel.addRow(new Object[]{
                            p.getLotCode(),
                            "Dưa lưới Taki", // Mock or from lot
                            p.getPestName(),
                            p.getSeverityCode(),
                            p.getEmployeeName(),
                            p.getReportedAt() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(p.getReportedAt()) : "--"
                        });
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    @SuppressWarnings("unchecked")
    private void refreshCharts() {
        new SwingWorker<Map<String, Object>, Void>() {
            @Override protected Map<String, Object> doInBackground() {
                Map<String, Object> data = new HashMap<>();
                data.put("pie", ctrl.getSeverityDistribution());
                data.put("line", ctrl.getMonthlyTrend());
                return data;
            }
            @Override protected void done() {
                try {
                    Map<String, Object> res = get();
                    
                    // Update Pie
                    PiePlot<?> plot = (PiePlot<?>) piePanel.getChart().getPlot();
                    DefaultPieDataset<String> pieDataset = (DefaultPieDataset<String>) plot.getDataset();
                    pieDataset.clear();
                    List<Object[]> pieData = (List<Object[]>) res.get("pie");
                    for (Object[] r : pieData) {
                        pieDataset.setValue(String.valueOf(r[0]), (Number) r[1]);
                    }

                    // Update Line
                    CategoryPlot categoryPlot = linePanel.getChart().getCategoryPlot();
                    DefaultCategoryDataset lineDataset = (DefaultCategoryDataset) categoryPlot.getDataset();
                    lineDataset.clear();
                    List<Object[]> lineData = (List<Object[]>) res.get("line");
                    for (Object[] r : lineData) {
                        String label = "T" + r[1];
                        lineDataset.addValue((Number) r[2], "Sâu bệnh", label);
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }
}
