package com.example.winfinal.view;

import com.example.winfinal.controller.*;
import com.example.winfinal.dto.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class TraceabilityDetailView extends JPanel {

    private final ProductionLotController lotCtrl = new ProductionLotController();
    private final CultivationLogController logCtrl = new CultivationLogController();
    private final PestReportController pestCtrl = new PestReportController();
    private final IrrigationLogController irrCtrl = new IrrigationLogController();
    private final HarvestRecordController harvestCtrl = new HarvestRecordController();

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private JTextField txtSearch;
    private JPanel contentPanel;
    
    // Labels for Info section
    private JLabel lblFarm, lblCrop, lblSeason, lblManager;
    
    // Tables for sections
    private DefaultTableModel careModel, healthModel;
    private JLabel lblIrrigation;
    private JLabel lblHarvestDate, lblYield, lblCustomer;

    public TraceabilityDetailView() {
        setLayout(new BorderLayout(0, 15));
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(25, 40, 25, 40));

        add(buildHeader(), BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Initially empty or welcome message
        JLabel welcome = new JLabel("Nhập mã lô sản xuất để tra cứu nguồn gốc", SwingConstants.CENTER);
        welcome.setFont(AppTheme.FONT_TITLE);
        welcome.setForeground(AppTheme.TEXT_SECONDARY);
        welcome.setBorder(new EmptyBorder(100, 0, 0, 0));
        contentPanel.add(welcome);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("TRUY XUẤT NGUỒN GỐC", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(0x1E40AF)); // Modern blue
        header.add(title, BorderLayout.NORTH);

        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        searchWrap.setOpaque(false);

        JLabel lblSearch = new JLabel("Nhập mã lô:");
        lblSearch.setFont(AppTheme.FONT_BODY);
        
        txtSearch = UiUtils.createSearchField("Vd: LOT-S1");
        txtSearch.setPreferredSize(new Dimension(300, 36));
        
        JButton btnSearch = UiUtils.createPrimaryButton("Tra cứu");
        btnSearch.setPreferredSize(new Dimension(100, 36));
        btnSearch.addActionListener(e -> performSearch(txtSearch.getText().trim()));
        txtSearch.addActionListener(e -> performSearch(txtSearch.getText().trim()));

        searchWrap.add(lblSearch);
        searchWrap.add(txtSearch);
        searchWrap.add(btnSearch);

        header.add(searchWrap, BorderLayout.CENTER);
        return header;
    }

    public void performSearch(String lotCode) {
        if (lotCode.isEmpty()) return;

        ProductionLotDTO lot = lotCtrl.findByLotCode(lotCode);
        if (lot == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy lô sản xuất: " + lotCode, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        renderDetails(lot);
    }

    private void renderDetails(ProductionLotDTO lot) {
        contentPanel.removeAll();

        // 1. Thông tin chung
        contentPanel.add(createSectionHeader("Thông Tin Chung"));
        
        JPanel infoWrapper = new JPanel(new BorderLayout(25, 0));
        infoWrapper.setOpaque(false);
        
        // Image on the left
        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(160, 160));
        imgLabel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        String imgPath = "pic/" + lot.getCropTypeCode();
        System.out.println("DEBUG: user.dir=" + System.getProperty("user.dir"));
        System.out.println("DEBUG: cropTypeCode=" + lot.getCropTypeCode());
        System.out.println("DEBUG: trying basePath=" + imgPath);
        
        String[] exts = {".jpg", ".png", ".webp", ".jpeg"};
        ImageIcon icon = null;
        for (String ext : exts) {
            java.io.File f = new java.io.File(imgPath + ext);
            System.out.println("DEBUG: checking " + f.getAbsolutePath() + " exists? " + f.exists());
            if (f.exists()) {
                icon = new ImageIcon(imgPath + ext);
                Image img = icon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(img));
                break;
            }
        }
        if (icon == null) {
            imgLabel.setText("Không có ảnh");
            imgLabel.setFont(AppTheme.FONT_SMALL);
            imgLabel.setForeground(AppTheme.TEXT_SECONDARY);
        }
        
        JPanel infoCard = UiUtils.createCard();
        infoCard.setLayout(new GridLayout(2, 2, 20, 10));
        
        lblFarm = createInfoLabel("Trang trại: ", lot.getFarmName() != null ? lot.getFarmName() : "---");
        lblCrop = createInfoLabel("Loại cây: ", lot.getCropTypeName() != null ? lot.getCropTypeName() : "---");
        lblSeason = createInfoLabel("Mùa vụ: ", lot.getSeasonName() != null ? lot.getSeasonName() : "---");
        lblManager = createInfoLabel("Người quản lý: ", lot.getManagerName() != null ? lot.getManagerName() : "---");

        infoCard.add(lblFarm);
        infoCard.add(lblCrop);
        infoCard.add(lblSeason);
        infoCard.add(lblManager);

        infoWrapper.add(imgLabel, BorderLayout.WEST);
        infoWrapper.add(infoCard, BorderLayout.CENTER);
        
        contentPanel.add(infoWrapper);
        contentPanel.add(Box.createVerticalStrut(20));

        // 2. Quá trình chăm sóc (Cultivation Log)
        contentPanel.add(createSectionHeader("Quá Trình Chăm Sóc"));
        String[] careCols = {"Ngày", "Hoạt động", "Loại vật tư", "Người thực hiện"};
        careModel = new DefaultTableModel(careCols, 0);
        
        List<Object[]> logs = logCtrl.getTraceabilityLogs(lot.getLotCode());
        if (logs != null) {
            for (Object[] r : logs) {
                // [appliedAt, activityTypeCode, supplyName, dosageUsed, employeeFullName]
                String date = r[0] != null ? sdf.format(r[0]) : "---";
                String activity = translateActivity((String)r[1]);
                String material = (String)r[2] + " (" + r[3] + ")";
                String person = (String)r[4];
                careModel.addRow(new Object[]{date, activity, material, person});
            }
        }
        
        contentPanel.add(createTableCard(careModel));
        contentPanel.add(Box.createVerticalStrut(20));

        // 3. Sức khỏe cây trồng (Pest Report)
        contentPanel.add(createSectionHeader("Sức Khỏe Cây Trồng"));
        String[] healthCols = {"Ngày", "Vấn đề", "Cách xử lý"};
        healthModel = new DefaultTableModel(healthCols, 0);
        
        List<PestReportDTO> pests = pestCtrl.findByLot(lot.getId());
        if (pests != null) {
            for (PestReportDTO p : pests) {
                String date = p.getReportedAt() != null ? sdf.format(p.getReportedAt()) : "---";
                healthModel.addRow(new Object[]{date, p.getPestName(), p.getTreatment()});
            }
        }
        contentPanel.add(createTableCard(healthModel));
        contentPanel.add(Box.createVerticalStrut(20));

        // 4. Tưới tiêu
        contentPanel.add(createSectionHeader("Tưới Tiêu"));
        JPanel irrCard = UiUtils.createCard();
        irrCard.setLayout(new BorderLayout());
        
        List<IrrigationLogDTO> irrs = irrCtrl.findByLot(lot.getId());
        String irrMsg = "Chế độ nước: ";
        if (irrs != null && !irrs.isEmpty()) {
            IrrigationLogDTO last = irrs.get(irrs.size()-1);
            irrMsg += (last.getSource() != null ? "Nguồn " + last.getSource() : "Tưới tiêu định kỳ");
        } else {
            irrMsg += "Chưa có dữ liệu tưới tiêu";
        }
        
        lblIrrigation = new JLabel(irrMsg);
        lblIrrigation.setFont(AppTheme.FONT_BODY);
        irrCard.add(lblIrrigation, BorderLayout.CENTER);
        contentPanel.add(irrCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // 5. Đầu ra
        contentPanel.add(createSectionHeader("Đầu Ra"));
        
        List<HarvestRecordDTO> harvests = harvestCtrl.findByLot(lot.getId());
        if (harvests != null && !harvests.isEmpty()) {
            String[] harvestCols = {"Ngày", "Sản lượng (kg)", "Khách hàng", "Chất lượng"};
            DefaultTableModel harvestModel = new DefaultTableModel(harvestCols, 0);
            for (HarvestRecordDTO h : harvests) {
                String date = h.getHarvestDate() != null ? sdf.format(h.getHarvestDate()) : "---";
                String yieldStr = h.getYieldKg() != null ? h.getYieldKg().toString() : "---";
                String cust = h.getCustomerName() != null ? h.getCustomerName() : "Khách hàng sỉ";
                String grade = h.getQualityGradeCode() != null ? translateGrade(h.getQualityGradeCode()) : "---";
                harvestModel.addRow(new Object[]{date, yieldStr, cust, grade});
            }
            contentPanel.add(createTableCard(harvestModel));
        } else {
            JPanel outCard = UiUtils.createCard();
            outCard.setLayout(new GridLayout(3, 1, 0, 8));
            outCard.add(createInfoLabel("Ngày thu hoạch: ", "Đang trong kỳ canh tác"));
            outCard.add(createInfoLabel("Sản lượng: ", "---"));
            outCard.add(createInfoLabel("Khách hàng: ", "---"));
            contentPanel.add(outCard);
        }
        contentPanel.add(Box.createVerticalStrut(40));

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createSectionHeader(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(new Color(0x374151)); // Gray-700
        p.add(l);
        return p;
    }

    private JPanel createTableCard(DefaultTableModel model) {
        JPanel card = UiUtils.createCard();
        card.setLayout(new BorderLayout());
        JTable table = new JTable(model);
        UiUtils.styleTable(table);
        
        // Hide grid for extra clean look if needed, but styleTable sets it
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null);
        sp.setPreferredSize(new Dimension(0, Math.min(150, model.getRowCount() * 36 + 40)));
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    private JLabel createInfoLabel(String label, String value) {
        JLabel l = new JLabel("<html><b>" + label + "</b> <font color='#1F2937'>" + value + "</font></html>");
        l.setFont(AppTheme.FONT_BODY);
        l.setForeground(AppTheme.TEXT_SECONDARY);
        return l;
    }

    private String translateActivity(String code) {
        if (code == null) return "---";
        return switch (code) {
            case "FERTILIZE" -> "Bón phân";
            case "PESTICIDE" -> "Phun thuốc trừ sâu";
            case "FUNGICIDE" -> "Phun thuốc trừ nấm";
            case "FOLIAR"    -> "Phun phân bón lá";
            default -> code;
        };
    }

    private String translateGrade(String code) {
        if (code == null) return "---";
        return switch (code) {
            case "GRADE_A" -> "Loại A";
            case "GRADE_B" -> "Loại B";
            case "GRADE_C" -> "Loại C";
            case "GRADE_D" -> "Loại D";
            default -> code;
        };
    }
}
