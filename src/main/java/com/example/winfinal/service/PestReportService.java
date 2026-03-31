package com.example.winfinal.service;

import com.example.winfinal.dao.PestReportDAO;
import com.example.winfinal.entity.operation.PestReport;
import java.util.List;

public class PestReportService {
    private final PestReportDAO dao = new PestReportDAO();

    public void save(PestReport report) { dao.save(report); }
    public void update(PestReport report) { dao.update(report); }
    public void delete(Long id) { dao.delete(id); }
    public List<PestReport> getAll() { return dao.findAll(); }
    public PestReport getById(Long id) { return (PestReport) dao.findById(id); }

    // [4.2] Cảnh báo sâu bệnh nghiêm trọng
    public List<PestReport> getHighSeverityReports() {
        return dao.findHighSeverityReports();
    }

    // [5.3] Nhật ký sâu bệnh theo mã lô (Tra cứu nguồn gốc)
    public List<Object[]> getTraceabilityLogs(String lotCode) {
        return dao.getTraceabilityLogs(lotCode);
    }

    // [6.3] Thống kê sâu bệnh theo mức độ trong mùa vụ
    public List<Object[]> getSeverityStatsBySeason(Long seasonId) {
        return dao.getSeverityStatsBySeason(seasonId);
    }

    // Lấy tất cả báo cáo theo lô
    public List<PestReport> findByLot(Long lotId) {
        return dao.findByLot(lotId);
    }
}
