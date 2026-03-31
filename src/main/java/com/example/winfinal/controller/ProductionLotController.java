package com.example.winfinal.controller;

import com.example.winfinal.dto.ProductionLotDTO;
import com.example.winfinal.service.ProductionLotService;
import java.util.Date;
import java.util.List;

public class ProductionLotController extends BaseController<ProductionLotDTO> {
    private final ProductionLotService lotService;

    public ProductionLotController() {
        super(new ProductionLotService());
        this.lotService = (ProductionLotService) service;
    }

    // ── CRUD cơ bản ──────────────────────────────────────────
    public void createLot(ProductionLotDTO dto) { create(dto); }
    public void updateLot(ProductionLotDTO dto) { update(dto); }
    public void deleteLot(Long id) { delete(id); }
    public List<ProductionLotDTO> getAllLots() { return getAll(); }

    // ── Tìm kiếm & Lọc ──────────────────────────────────────
    public ProductionLotDTO findByLotCode(String lotCode) {
        return lotService.findByLotCode(lotCode);
    }
    public List<ProductionLotDTO> findByStatus(String statusCode) {
        return lotService.findByStatus(statusCode);
    }
    public List<ProductionLotDTO> findByFarm(Long farmId) {
        return lotService.findByFarm(farmId);
    }
    public List<ProductionLotDTO> search(Long farmId, String statusCode, Long cropTypeId, Long seasonId) {
        return lotService.search(farmId, statusCode, cropTypeId, seasonId);
    }

    // ── Dashboard ─────────────────────────────────────────────
    public List<ProductionLotDTO> getUpcomingHarvest(Date nextDate) {
        return lotService.getUpcomingHarvest(nextDate);
    }

    // ── Truy xuất nguồn gốc ──────────────────────────────────
    public ProductionLotDTO getFullTraceabilityInfo(String lotCode) {
        return lotService.getFullTraceabilityInfo(lotCode);
    }

    // ── Báo cáo ──────────────────────────────────────────────
    public List<Object[]> getTopYieldingLots(int limit) {
        return lotService.getTopYieldingLots(limit);
    }

    // ── Validation ───────────────────────────────────────────
    public boolean existsByLotCode(String lotCode) {
        return lotService.existsByLotCode(lotCode);
    }
}
