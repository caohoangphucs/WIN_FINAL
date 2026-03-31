package com.example.winfinal.controller;

import com.example.winfinal.entity.operation.PestReport;
import com.example.winfinal.service.PestReportService;
import java.util.List;

public class PestReportController {
    private final PestReportService service = new PestReportService();

    // ── CRUD cơ bản ──────────────────────────────────────────
    public void createReport(PestReport report) { service.save(report); }
    public void updateReport(PestReport report) { service.update(report); }
    public void deleteReport(Long id) { service.delete(id); }
    public List<PestReport> getAllReports() { return service.getAll(); }
    public PestReport getById(Long id) { return service.getById(id); }

    // ── Dashboard / Cảnh báo ─────────────────────────────────
    /** [4.2] Lô có sâu bệnh nghiêm trọng (HIGH/CRITICAL) — hiển thị màu đỏ trên Farm Grid */
    public List<PestReport> getHighSeverityReports() {
        return service.getHighSeverityReports();
    }

    /** Tất cả báo cáo sâu bệnh của một lô */
    public List<PestReport> findByLot(Long lotId) {
        return service.findByLot(lotId);
    }

    // ── Báo cáo ──────────────────────────────────────────────
    /** [6.3] Thống kê sâu bệnh theo mức độ trong mùa vụ */
    public List<Object[]> getSeverityStatsBySeason(Long seasonId) {
        return service.getSeverityStatsBySeason(seasonId);
    }

    // ── Truy xuất nguồn gốc ──────────────────────────────────
    /** [5.3] Nhật ký sâu bệnh theo mã lô — bước 3 của Traceability */
    public List<Object[]> getTraceabilityLogs(String lotCode) {
        return service.getTraceabilityLogs(lotCode);
    }
}
