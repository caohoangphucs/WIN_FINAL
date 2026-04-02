package com.example.winfinal.service;

import com.example.winfinal.dao.PestReportDAO;
import com.example.winfinal.dto.PestReportDTO;
import com.example.winfinal.entity.operation.PestReport;
import com.example.winfinal.mapper.PestReportMapper;
import java.util.List;
import java.util.stream.Collectors;

public class PestReportService extends BaseService<PestReport, PestReportDTO> {
    private static final PestReportMapper mapper = PestReportMapper.INSTANCE;
    private final PestReportDAO pestDAO;

    public PestReportService() {
        super(new PestReportDAO());
        this.pestDAO = (PestReportDAO) dao;
    }

    @Override 
    protected PestReportDTO toDTO(PestReport e) { 
        PestReportDTO d = mapper.toDTO(e); 
        if (d != null && e != null) {
            if (e.getLot() != null) d.setLotCode(e.getLot().getLotCode());
            if (e.getEmployee() != null) d.setEmployeeName(e.getEmployee().getFullName());
            if (e.getSeverity() != null) d.setSeverityCode(e.getSeverity().getCode());
        }
        return d;
    }
    @Override protected PestReport toEntity(PestReportDTO d) { return mapper.toEntity(d); }
    @Override protected void updateEntityFromDTO(PestReportDTO d, PestReport e) { /* Update logic */ }
    @Override protected Object getEntityId(PestReportDTO d) { return d.getId(); }

    public List<PestReportDTO> getHighSeverityReports() {
        return pestDAO.findHighSeverityReports().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<Object[]> getTraceabilityLogs(String lotCode) {
        return pestDAO.getTraceabilityLogs(lotCode);
    }

    public List<Object[]> getSeverityStatsBySeason(Long seasonId) {
        return pestDAO.getSeverityStatsBySeason(seasonId);
    }

    public List<PestReportDTO> findByLot(Long lotId) {
        return pestDAO.findByLot(lotId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<Object[]> getSeverityDistribution() { return pestDAO.getSeverityDistribution(); }
    public List<Object[]> getMonthlyTrend() { return pestDAO.getMonthlyTrend(); }
}
