package com.example.winfinal.controller;

import com.example.winfinal.entity.operation.IrrigationLog;
import com.example.winfinal.service.IrrigationLogService;
import java.util.List;

public class IrrigationLogController {
    private final IrrigationLogService service = new IrrigationLogService();

    // ── CRUD cơ bản ──────────────────────────────────────────
    public void createLog(IrrigationLog log) { service.save(log); }
    public void updateLog(IrrigationLog log) { service.update(log); }
    public void deleteLog(Long id) { service.delete(id); }
    public List<IrrigationLog> getAllLogs() { return service.getAll(); }
    public IrrigationLog getById(Long id) { return service.getById(id); }

    // ── Thống kê & Giám sát ──────────────────────────────────
    /** [4.4] Tổng lượng nước tưới theo tháng cho một lô — dùng trên biểu đồ */
    public List<Object[]> getMonthlyWaterUsage(Long lotId, int year) {
        return service.getMonthlyWaterUsage(lotId, year);
    }

    /** Lấy danh sách tưới tiêu của một lô */
    public List<IrrigationLog> findByLot(Long lotId) {
        return service.findByLot(lotId);
    }

    // ── Truy xuất nguồn gốc ──────────────────────────────────
    /** [5.4] Nhật ký tưới tiêu theo mã lô — bước 4 của Traceability */
    public List<Object[]> getTraceabilityLogs(String lotCode) {
        return service.getTraceabilityLogs(lotCode);
    }
}
