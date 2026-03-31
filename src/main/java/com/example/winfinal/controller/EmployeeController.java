package com.example.winfinal.controller;

import com.example.winfinal.dto.EmployeeDTO;
import com.example.winfinal.service.EmployeeService;

import java.util.List;

public class EmployeeController extends BaseController<EmployeeDTO> {
    public EmployeeController() {
        super(new EmployeeService());
    }

    public void createEmployee(EmployeeDTO dto) { create(dto); }
    public void updateEmployee(EmployeeDTO dto) { update(dto); }
    public void deleteEmployee(Long id) { delete(id); }
    public List<EmployeeDTO> getAllEmployees() { return getAll(); }
}
