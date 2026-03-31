package com.example.winfinal.controller;

import com.example.winfinal.dto.SupplierDTO;
import com.example.winfinal.service.SupplierService;
import java.util.List;

public class SupplierController extends BaseController<SupplierDTO> {
    public SupplierController() {
        super(new SupplierService());
    }

    public void createSupplier(SupplierDTO dto) { create(dto); }
    public void updateSupplier(SupplierDTO dto) { update(dto); }
    public void deleteSupplier(Long id) { delete(id); }
    public List<SupplierDTO> getAllSuppliers() { return getAll(); }
}
