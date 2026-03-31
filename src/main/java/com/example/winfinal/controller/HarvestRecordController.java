package com.example.winfinal.controller;

import com.example.winfinal.dto.HarvestRecordDTO;
import com.example.winfinal.service.HarvestRecordService;
import java.util.List;

public class HarvestRecordController extends BaseController<HarvestRecordDTO> {
    private final HarvestRecordService harvestService;

    public HarvestRecordController() {
        super(new HarvestRecordService());
        this.harvestService = (HarvestRecordService) service;
    }

    // ── CRUD cơ bản ──────────────────────────────────────────
    public void createHarvestRecord(HarvestRecordDTO dto) { create(dto); }
    public void updateHarvestRecord(HarvestRecordDTO dto) { update(dto); }
    public void deleteHarvestRecord(Long id) { delete(id); }
    public List<HarvestRecordDTO> getAllHarvestRecords() { return getAll(); }

    // ── Thống kê Dashboard ────────────────────────────────────
    public List<Object[]> getAvgYieldByCropType() { return harvestService.getAvgYieldByCropType(); }
    public List<Object[]> getYieldBySeason()       { return harvestService.getYieldBySeason(); }
    public List<Object[]> getYieldPerM2ByLot()     { return harvestService.getYieldPerM2ByLot(); }
    public List<Object[]> getQualityGradeStats()   { return harvestService.getQualityGradeStats(); }
    public List<Object[]> getYieldByFarm()         { return harvestService.getYieldByFarm(); }
    public List<Object[]> getCustomerYieldStats()  { return harvestService.getCustomerYieldStats(); }

    // ── Truy xuất nguồn gốc ──────────────────────────────────
    public List<HarvestRecordDTO> findByLotCode(String lotCode) {
        return harvestService.findByLotCode(lotCode);
    }
}
