package com.example.winfinal.controller;

import com.example.winfinal.dto.AgriSupplyDTO;
import com.example.winfinal.service.AgriSupplyService;
import java.util.List;

public class AgriSupplyController extends BaseController<AgriSupplyDTO> {
    private final AgriSupplyService supplyService;

    public AgriSupplyController() {
        super(new AgriSupplyService());
        this.supplyService = (AgriSupplyService) service;
    }

    // ── CRUD cơ bản ──────────────────────────────────────────
    public void createAgriSupply(AgriSupplyDTO dto) { create(dto); }
    public void updateAgriSupply(AgriSupplyDTO dto) { update(dto); }
    public void deleteAgriSupply(Long id) { delete(id); }
    public List<AgriSupplyDTO> getAllAgriSupplies() { return getAll(); }

    // ── Tìm kiếm ─────────────────────────────────────────────
    public List<AgriSupplyDTO> search(String keyword) {
        return supplyService.search(keyword);
    }

    // ── Dashboard / Cảnh báo ─────────────────────────────────
    public List<AgriSupplyDTO> getLowStockSupplies() {
        return supplyService.getLowStockSupplies();
    }

    // ── Validation ───────────────────────────────────────────
    public boolean existsBySupplyCode(String supplyCode) {
        return supplyService.existsBySupplyCode(supplyCode);
    }
    public Double getStockQty(Long supplyId) {
        return supplyService.getStockQty(supplyId);
    }
}
