package com.example.winfinal.controller;

import com.example.winfinal.entity.operation.WeatherLog;
import com.example.winfinal.service.WeatherLogService;
import java.util.List;

public class WeatherLogController {
    private final WeatherLogService service = new WeatherLogService();

    // ── CRUD cơ bản ──────────────────────────────────────────
    public void createLog(WeatherLog log) { service.save(log); }
    public void updateLog(WeatherLog log) { service.update(log); }
    public void deleteLog(Long id) { service.delete(id); }
    public List<WeatherLog> getAllLogs() { return service.getAll(); }
    public WeatherLog getById(Long id) { return service.getById(id); }

    // ── Báo cáo / Phân tích ──────────────────────────────────
    /** [6.5] Thống kê ngày mưa theo tháng cho trang trại — dùng trong báo cáo tương quan Thời tiết – Năng suất */
    public List<Object[]> getMonthlyRainStats(Long farmId, int year) {
        return service.getMonthlyRainStats(farmId, year);
    }

    /** Nhật ký thời tiết của một trang trại theo năm */
    public List<WeatherLog> findByFarmAndYear(Long farmId, int year) {
        return service.findByFarmAndYear(farmId, year);
    }
}
