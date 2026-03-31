package com.example.winfinal.controller;

import com.example.winfinal.dto.CultivationLogDTO;
import com.example.winfinal.service.CultivationLogService;
import java.util.Date;
import java.util.List;

public class CultivationLogController extends BaseController<CultivationLogDTO> {
    private final CultivationLogService cultivationService;

    public CultivationLogController() {
        super(new CultivationLogService());
        this.cultivationService = (CultivationLogService) service;
    }

    // ── CRUD cơ bản ──────────────────────────────────────────
    public void createLog(CultivationLogDTO dto) { create(dto); }
    public void updateLog(CultivationLogDTO dto) { update(dto); }
    public void deleteLog(Long id) { delete(id); }
    public List<CultivationLogDTO> getAllLogs() { return getAll(); }

    // ── Thống kê vật tư ──────────────────────────────────────
    public List<Object[]> getMaterialConsumptionByType() {
        return cultivationService.getMaterialConsumptionByType();
    }
    public List<Object[]> getUsageByLot(Long lotId) {
        return cultivationService.getUsageByLot(lotId);
    }
    public List<Object[]> getDetailedActivityByLot(Long lotId) {
        return cultivationService.getDetailedActivityByLot(lotId);
    }

    // ── Giám sát canh tác ─────────────────────────────────────
    public List<CultivationLogDTO> findByLotAndDateRange(Long lotId, Date startDate, Date endDate) {
        return cultivationService.findByLotAndDateRange(lotId, startDate, endDate);
    }
    public List<Object[]> getActivityStatsByLot(Long lotId) {
        return cultivationService.getActivityStatsByLot(lotId);
    }

    // ── Truy xuất nguồn gốc ──────────────────────────────────
    public List<Object[]> getTraceabilityLogs(String lotCode) {
        return cultivationService.getTraceabilityLogs(lotCode);
    }
}
