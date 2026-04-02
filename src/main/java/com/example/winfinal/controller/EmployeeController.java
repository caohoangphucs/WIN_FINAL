package com.example.winfinal.controller;

import com.example.winfinal.dto.EmployeeDTO;
import com.example.winfinal.service.EmployeeService;
import java.util.List;

public class EmployeeController extends BaseController<EmployeeDTO> {
    private final EmployeeService employeeService;

    public EmployeeController() {
        super(new EmployeeService());
        this.employeeService = (EmployeeService) service;
    }

    // ── CRUD cơ bản ──────────────────────────────────────────
    public void createEmployee(EmployeeDTO dto) { create(dto); }
    public void updateEmployee(EmployeeDTO dto) { update(dto); }
    public void deleteEmployee(Long id) { delete(id); }
    public List<EmployeeDTO> getAllEmployees() { return getAll(); }

    // ── Tìm kiếm ─────────────────────────────────────────────
    public List<EmployeeDTO> search(String keyword) {
        return employeeService.search(keyword);
    }

    // ── Báo cáo hiệu suất ────────────────────────────────────
    public List<Object[]> getTopPerformers(int month, int year) {
        return employeeService.getTopPerformers(month, year);
    }

    // ── Validation ───────────────────────────────────────────
    public boolean existsByEmpCode(String empCode) {
        return employeeService.existsByEmpCode(empCode);
    }
    public Long countTodayLogs(Long employeeId) {
        return employeeService.countTodayLogs(employeeId);
    }

    public List<Object[]> getRoleDistribution() { return employeeService.getRoleDistribution(); }
    public List<Object[]> getHarvestPerformance() { return employeeService.getHarvestPerformance(); }
}
