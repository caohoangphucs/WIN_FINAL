package com.example.winfinal.controller;

import com.example.winfinal.dto.IrrigationLogDTO;
import com.example.winfinal.service.IrrigationLogService;
import java.util.List;

public class IrrigationLogController extends BaseController<IrrigationLogDTO> {
    private final IrrigationLogService irrigationService;

    public IrrigationLogController() {
        super(new IrrigationLogService());
        this.irrigationService = (IrrigationLogService) service;
    }

    // Aliases
    public void createLog(IrrigationLogDTO dto) { create(dto); }
    public void updateLog(IrrigationLogDTO dto) { update(dto); }
    public void deleteLog(Long id) { delete(id); }
    public List<IrrigationLogDTO> getAllLogs() { return getAll(); }

    public List<Object[]> getMonthlyWaterUsage(Long lotId, int year) { return irrigationService.getMonthlyWaterUsage(lotId, year); }
    public List<Object[]> getTraceabilityLogs(String lotCode) { return irrigationService.getTraceabilityLogs(lotCode); }
    public List<IrrigationLogDTO> findByLot(Long lotId) { return irrigationService.findByLot(lotId); }
}
