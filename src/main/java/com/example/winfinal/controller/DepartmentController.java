package com.example.winfinal.controller;

import com.example.winfinal.dto.DepartmentDTO;
import com.example.winfinal.service.DepartmentService;
import java.util.List;

public class DepartmentController extends BaseController<DepartmentDTO> {
    public DepartmentController() {
        super(new DepartmentService());
    }

    public void createDepartment(DepartmentDTO dto) { create(dto); }
    public void updateDepartment(DepartmentDTO dto) { update(dto); }
    public void deleteDepartment(Long id) { delete(id); }
    public List<DepartmentDTO> getAllDepartments() { return getAll(); }
}
