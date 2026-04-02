package com.example.winfinal.controller;

import com.example.winfinal.dto.PestReportDTO;
import com.example.winfinal.service.PestReportService;
import java.util.List;

public class PestReportController extends BaseController<PestReportDTO> {
    private final PestReportService pestService;

    public PestReportController() {
        super(new PestReportService());
        this.pestService = (PestReportService) service;
    }

    // Aliases
    public void createReport(PestReportDTO dto) { create(dto); }
    public void updateReport(PestReportDTO dto) { update(dto); }
    public void deleteReport(Long id) { delete(id); }
    public List<PestReportDTO> getAllReports() { return getAll(); }

    public List<PestReportDTO> getHighSeverityReports() { return pestService.getHighSeverityReports(); }
    public List<PestReportDTO> findByLot(Long lotId) { return pestService.findByLot(lotId); }
    public List<Object[]> getSeverityStatsBySeason(Long seasonId) { return pestService.getSeverityStatsBySeason(seasonId); }
    public List<Object[]> getTraceabilityLogs(String lotCode) { return pestService.getTraceabilityLogs(lotCode); }

    public List<Object[]> getSeverityDistribution() { return pestService.getSeverityDistribution(); }
    public List<Object[]> getMonthlyTrend() { return pestService.getMonthlyTrend(); }
}
