package com.example.winfinal.controller;

import com.example.winfinal.dto.SupplyImportDTO;
import com.example.winfinal.service.SupplyImportService;
import java.util.List;

public class SupplyImportController extends BaseController<SupplyImportDTO> {
    private final SupplyImportService importService;

    public SupplyImportController() {
        super(new SupplyImportService());
        this.importService = (SupplyImportService) service;
    }

    // ── CRUD cơ bản ──────────────────────────────────────────
    public void createImport(SupplyImportDTO dto) { create(dto); }
    public void updateImport(SupplyImportDTO dto) { update(dto); }
    public void deleteImport(Long id) { delete(id); }
    public List<SupplyImportDTO> getAllImports() { return getAll(); }

    // ── Thống kê kho ─────────────────────────────────────────
    /** [3.3] Tổng chi phí nhập kho theo nhà cung cấp */
    public List<Object[]> getTotalCostBySupplier() {
        return importService.getTotalCostBySupplier();
    }

    /** [3.4] Lịch sử nhập kho của một vật tư */
    public List<SupplyImportDTO> findBySupply(Long supplyId) {
        return importService.findBySupply(supplyId);
    }

    // ── Báo cáo chi phí ──────────────────────────────────────
    /** [6.6] Ước tính chi phí vật tư theo lô để tính lợi nhuận */
    public List<Object[]> getCostEstimateByLot() {
        return importService.getCostEstimateByLot();
    }

    /** Giá trung bình nhập kho của một vật tư */
    public Double getAvgUnitPrice(Long supplyId) {
        return importService.getAvgUnitPrice(supplyId);
    }
}
