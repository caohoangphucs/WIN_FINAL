package com.example.winfinal.view;

import com.example.winfinal.dto.FarmDTO;
import com.example.winfinal.service.FarmService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FarmView extends JFrame {
    private final FarmService farmService = new FarmService();
    
    private JTable farmTable;
    private DefaultTableModel tableModel;
    
    private JTextField txtId, txtFarmCode, txtName, txtAddress, txtTotalArea, txtOwnerName, txtPhone;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public FarmView() {
        setTitle("Farm Management - CRUD");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initComponents();
        refreshTable();
    }

    private void initComponents() {
        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("ID (Read-only):"));
        txtId = new JTextField();
        txtId.setEditable(false);
        formPanel.add(txtId);

        formPanel.add(new JLabel("Farm Code:"));
        txtFarmCode = new JTextField();
        formPanel.add(txtFarmCode);

        formPanel.add(new JLabel("Name:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Address:"));
        txtAddress = new JTextField();
        formPanel.add(txtAddress);

        formPanel.add(new JLabel("Total Area:"));
        txtTotalArea = new JTextField();
        formPanel.add(txtTotalArea);

        formPanel.add(new JLabel("Owner Name:"));
        txtOwnerName = new JTextField();
        formPanel.add(txtOwnerName);

        formPanel.add(new JLabel("Phone:"));
        txtPhone = new JTextField();
        formPanel.add(txtPhone);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel();
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        // --- Table ---
        String[] columns = {"ID", "Code", "Name", "Address", "Area", "Owner", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        farmTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(farmTable);

        // --- Layout assembly ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Events ---
        btnAdd.addActionListener(e -> addFarm());
        btnUpdate.addActionListener(e -> updateFarm());
        btnDelete.addActionListener(e -> deleteFarm());
        btnClear.addActionListener(e -> clearForm());

        farmTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && farmTable.getSelectedRow() != -1) {
                populateForm();
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<FarmDTO> farms = farmService.getAllFarms();
        for (FarmDTO f : farms) {
            tableModel.addRow(new Object[]{
                f.getId(), f.getFarmCode(), f.getName(), f.getAddress(), 
                f.getTotalArea(), f.getOwnerName(), f.getPhone()
            });
        }
    }

    private void addFarm() {
        try {
            FarmDTO dto = getDtoFromForm();
            farmService.createFarm(dto);
            JOptionPane.showMessageDialog(this, "Farm added successfully!");
            refreshTable();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateFarm() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a farm to update");
                return;
            }
            FarmDTO dto = getDtoFromForm();
            dto.setId(Long.parseLong(txtId.getText()));
            farmService.updateFarm(dto);
            JOptionPane.showMessageDialog(this, "Farm updated successfully!");
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteFarm() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a farm to delete");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                farmService.deleteFarm(Long.parseLong(txtId.getText()));
                JOptionPane.showMessageDialog(this, "Farm deleted successfully!");
                refreshTable();
                clearForm();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void populateForm() {
        int row = farmTable.getSelectedRow();
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtFarmCode.setText(tableModel.getValueAt(row, 1).toString());
        txtName.setText(tableModel.getValueAt(row, 2).toString());
        txtAddress.setText(tableModel.getValueAt(row, 3).toString());
        txtTotalArea.setText(tableModel.getValueAt(row, 4).toString());
        txtOwnerName.setText(tableModel.getValueAt(row, 5).toString());
        txtPhone.setText(tableModel.getValueAt(row, 6).toString());
    }

    private void clearForm() {
        txtId.setText("");
        txtFarmCode.setText("");
        txtName.setText("");
        txtAddress.setText("");
        txtTotalArea.setText("");
        txtOwnerName.setText("");
        txtPhone.setText("");
        farmTable.clearSelection();
    }

    private FarmDTO getDtoFromForm() {
        FarmDTO dto = new FarmDTO();
        dto.setFarmCode(txtFarmCode.getText());
        dto.setName(txtName.getText());
        dto.setAddress(txtAddress.getText());
        dto.setTotalArea(Double.parseDouble(txtTotalArea.getText()));
        dto.setOwnerName(txtOwnerName.getText());
        dto.setPhone(txtPhone.getText());
        return dto;
    }
}
