package com.example.winfinal.controller;

import com.example.winfinal.dto.SupplyImportDTO;
import com.example.winfinal.service.SupplyImportService;
import java.util.List;

public class SupplyImportController extends BaseController<SupplyImportDTO> {
    public SupplyImportController() {
        super(new SupplyImportService());
    }

    public void createImport(SupplyImportDTO dto) { create(dto); }
    public void updateImport(SupplyImportDTO dto) { update(dto); }
    public void deleteImport(Long id) { delete(id); }
    public List<SupplyImportDTO> getAllImports() { return getAll(); }
}
