package com.example.winfinal.view;

import com.example.winfinal.controller.EmployeeController;
import com.example.winfinal.dto.EmployeeDTO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
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
 * Modern HR Management View with CRUD and Analytics.
 */
public class EmployeeView extends JPanel {

    private final EmployeeController ctrl = new EmployeeController();

    private JTable table;
    private DefaultTableModel tableModel;
    private ChartPanel piePanel;
    private ChartPanel barPanel;

    private JTextField txtSearch;

    public EmployeeView() {
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
        p.add(UiUtils.createSectionTitle("Quản lý & Đánh giá Nhân sự"), BorderLayout.NORTH);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        filterBar.setOpaque(false);

        filterBar.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.addActionListener(e -> refreshData());
        filterBar.add(txtSearch);

        JButton btnSearch = UiUtils.createPrimaryButton("Tìm kiếm");
        btnSearch.addActionListener(e -> refreshData());
        filterBar.add(btnSearch);

        JButton btnAdd = UiUtils.createSuccessButton("+ Thêm nhân sự");
        btnAdd.addActionListener(e -> showAddEditDialog(null));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(btnAdd);

        p.add(filterBar, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JPanel buildCharts() {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 260));

        // Pie: Role structure
        DefaultPieDataset<String> pieDataset = new DefaultPieDataset<>();
        JFreeChart pieChart = ChartFactory.createPieChart("Cơ cấu nhân sự theo vai trò", pieDataset, true, true, false);
        stylePieChart(pieChart);
        piePanel = new ChartPanel(pieChart);
        piePanel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT));
        row.add(piePanel);

        // Bar: Harvest performance
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        JFreeChart barChart = ChartFactory.createBarChart("Top 10 Năng suất Thu hoạch (kg)", "Nhân viên", "Tổng sản lượng", barDataset);
        styleBarChart(barChart);
        barPanel = new ChartPanel(barChart);
        barPanel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT));
        row.add(barPanel);

        return row;
    }

    private void stylePieChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        @SuppressWarnings("unchecked")
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setSectionPaint("MANAGER",    new Color(0x3B82F6));
        plot.setSectionPaint("TECHNICIAN", new Color(0x10B981));
        plot.setSectionPaint("WORKER",     new Color(0xF59E0B));
    }

    private void styleBarChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(AppTheme.BORDER_LIGHT);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0x6366F1));
        renderer.setMaximumBarWidth(0.1);
    }

    private JPanel buildTable() {
        String[] cols = {"ID", "Mã NV", "Họ tên", "Vai trò", "Email", "SĐT", "Thao tác"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 6; }
        };
        table = new JTable(tableModel);
        UiUtils.styleTable(table);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object val, boolean isSelected,
                                                               boolean hasFocus, int row, int col) {
                    if (col == 6) return buildActionButtons(row); // Should use a specialized renderer instead for real apps

                    JLabel c = (JLabel) super.getTableCellRendererComponent(t, val, isSelected, hasFocus, row, col);
                    c.setHorizontalAlignment(SwingConstants.CENTER);
                    c.setBorder(new EmptyBorder(0, 10, 0, 10));
                    
                    if (col == 3) { // Role Badge
                        String role = String.valueOf(val);
                        c.setText(role);
                        c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        c.setForeground(roleColor(role)[1]);
                        c.setOpaque(true);
                        c.setBackground(roleColor(role)[0]);
                    } else {
                        c.setForeground(AppTheme.TEXT_PRIMARY);
                        c.setBackground(isSelected ? t.getSelectionBackground() : Color.WHITE);
                    }
                    return c;
                }
            });
        }

        // Action Column: Edit & Delete
        table.getColumn("Thao tác").setPreferredWidth(160);
        table.getColumn("Thao tác").setCellRenderer(new ActionRenderer());
        table.getColumn("Thao tác").setCellEditor(new ActionEditor(new JCheckBox()));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private Color[] roleColor(String role) {
        if (role == null) return new Color[]{Color.WHITE, AppTheme.TEXT_SECONDARY};
        return switch (role.toUpperCase()) {
            case "MANAGER"    -> new Color[]{new Color(0xEFF6FF), new Color(0x1E40AF)};
            case "TECHNICIAN" -> new Color[]{new Color(0xF0FDF4), new Color(0x166534)};
            case "WORKER"     -> new Color[]{new Color(0xFEFCE8), new Color(0x854D0E)};
            default           -> new Color[]{Color.WHITE, AppTheme.TEXT_SECONDARY};
        };
    }

    private void refreshData() {
        refreshTable();
        refreshCharts();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        new SwingWorker<List<EmployeeDTO>, Void>() {
            @Override protected List<EmployeeDTO> doInBackground() {
                String kw = txtSearch.getText().trim();
                return kw.isEmpty() ? ctrl.getAllEmployees() : ctrl.search(kw);
            }
            @Override protected void done() {
                try {
                    for (var e : get()) {
                        tableModel.addRow(new Object[]{
                            e.getId(),
                            e.getEmpCode(),
                            e.getFullName(),
                            e.getRoleCode(),
                            e.getEmail(),
                            e.getPhone(),
                            "" // Buttons
                        });
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    @SuppressWarnings("unchecked")
    private void refreshCharts() {
        new SwingWorker<Map<String, List<Object[]>>, Void>() {
            @Override protected Map<String, List<Object[]>> doInBackground() {
                Map<String, List<Object[]>> data = new HashMap<>();
                data.put("role", ctrl.getRoleDistribution());
                data.put("yield", ctrl.getHarvestPerformance());
                return data;
            }
            @Override protected void done() {
                try {
                    var res = get();
                    // Update Pie
                    DefaultPieDataset<String> pieDS = (DefaultPieDataset<String>) ((PiePlot<String>) piePanel.getChart().getPlot()).getDataset();
                    pieDS.clear();
                    for (var r : res.get("role")) pieDS.setValue(String.valueOf(r[0]), (Number) r[1]);

                    // Update Bar
                    DefaultCategoryDataset barDS = (DefaultCategoryDataset) barPanel.getChart().getCategoryPlot().getDataset();
                    barDS.clear();
                    for (var r : res.get("yield")) barDS.addValue((Number) r[1], "Năng suất", String.valueOf(r[0]));
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void showAddEditDialog(EmployeeDTO dto) {
        boolean isEdit = dto != null;
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), isEdit ? "Sửa nhân viên" : "Thêm nhân viên", true);
        d.setLayout(new GridBagLayout());
        d.setSize(400, 500);
        d.setLocationRelativeTo(this);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 15, 10, 15);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fCode = new JTextField(isEdit ? dto.getEmpCode() : "");
        JTextField fName = new JTextField(isEdit ? dto.getFullName() : "");
        JComboBox<String> fRole = new JComboBox<>(new String[]{"MANAGER", "TECHNICIAN", "WORKER"});
        if (isEdit) fRole.setSelectedItem(dto.getRoleCode());

        JTextField fEmail = new JTextField(isEdit ? dto.getEmail() : "");
        JTextField fPhone = new JTextField(isEdit ? dto.getPhone() : "");
        JTextField fDept  = new JTextField(isEdit ? (dto.getDepartmentId() != null ? String.valueOf(dto.getDepartmentId()) : "") : "1");

        fCode.setEnabled(!isEdit);

        int r = 0;
        addLabel(d, "Mã nhân viên:", g, r++); d.add(fCode, g);
        addLabel(d, "Họ tên:", g, r++);      d.add(fName, g);
        addLabel(d, "Vai trò:", g, r++);     d.add(fRole, g);
        addLabel(d, "Email:", g, r++);       d.add(fEmail, g);
        addLabel(d, "Số điện thoại:", g, r++); d.add(fPhone, g);
        addLabel(d, "ID Phòng ban:", g, r++);  d.add(fDept, g);

        JButton btnSave = UiUtils.createPrimaryButton("Lưu");
        btnSave.addActionListener(e -> {
            try {
                EmployeeDTO n = isEdit ? dto : new EmployeeDTO();
                n.setEmpCode(fCode.getText());
                n.setFullName(fName.getText());
                n.setRoleCode((String) fRole.getSelectedItem());
                n.setEmail(fEmail.getText());
                n.setPhone(fPhone.getText());
                n.setDepartmentId(Long.parseLong(fDept.getText()));

                if (isEdit) ctrl.updateEmployee(n);
                else ctrl.createEmployee(n);

                d.dispose();
                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Lỗi: " + ex.getMessage());
            }
        });

        g.gridx = 0; g.gridy = r; g.gridwidth = 2;
        d.add(btnSave, g);
        d.setVisible(true);
    }

    private void addLabel(JDialog d, String text, GridBagConstraints g, int r) {
        g.gridx = 0; g.gridy = r; g.gridwidth = 2;
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_SUBTITLE);
        d.add(l, g);
        g.gridy = r; 
    }

    private JPanel buildActionButtons(int row) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        p.setOpaque(false);
        JButton btnE = new JButton("Sửa");
        JButton btnD = new JButton("Xóa");
        p.add(btnE); p.add(btnD);
        return p;
    }

    // ── Table Action Customization ───────────────────────────

    class ActionRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
            p.setOpaque(true);
            p.setBackground(s ? t.getSelectionBackground() : Color.WHITE);
            
            JButton b1 = new JButton("Sửa"); b1.setPreferredSize(new Dimension(65, 24));
            JButton b2 = new JButton("Xóa"); b2.setPreferredSize(new Dimension(65, 24));
            b1.setFont(new Font("Segoe UI", Font.BOLD, 11));
            b2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            b1.setForeground(new Color(0x3B82F6));
            b2.setForeground(AppTheme.DANGER);
            
            p.add(b1); p.add(b2);
            return p;
        }
    }

    class ActionEditor extends DefaultCellEditor {
        public ActionEditor(JCheckBox chk) { super(chk); }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
            p.setBackground(Color.WHITE);
            JButton b1 = new JButton("Sửa");
            JButton b2 = new JButton("Xóa");
            b1.addActionListener(e -> {
                Long id = (Long) t.getValueAt(r, 0);
                EmployeeDTO dto = ctrl.getAllEmployees().stream().filter(em -> em.getId().equals(id)).findFirst().orElse(null);
                fireEditingStopped();
                showAddEditDialog(dto);
            });
            b2.addActionListener(e -> {
                Long id = (Long) t.getValueAt(r, 0);
                int opt = JOptionPane.showConfirmDialog(t, "Xóa nhân viên ID " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    ctrl.deleteEmployee(id);
                    refreshData();
                }
                fireEditingStopped();
            });
            p.add(b1); p.add(b2);
            return p;
        }
    }
}
