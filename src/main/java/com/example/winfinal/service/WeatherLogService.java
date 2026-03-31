package com.example.winfinal.service;

import com.example.winfinal.dao.WeatherLogDAO;
import com.example.winfinal.entity.operation.WeatherLog;
import java.util.List;

public class WeatherLogService {
    private final WeatherLogDAO dao = new WeatherLogDAO();

    public void save(WeatherLog log) { dao.save(log); }
    public void update(WeatherLog log) { dao.update(log); }
    public void delete(Long id) { dao.delete(id); }
    public List<WeatherLog> getAll() { return dao.findAll(); }
    public WeatherLog getById(Long id) { return (WeatherLog) dao.findById(id); }

    // [6.5] Thống kê ngày mưa theo tháng cho trang trại
    public List<Object[]> getMonthlyRainStats(Long farmId, int year) {
        return dao.getMonthlyRainStats(farmId, year);
    }

    // Lấy nhật ký thời tiết theo năm
    public List<WeatherLog> findByFarmAndYear(Long farmId, int year) {
        return dao.findByFarmAndYear(farmId, year);
    }
}
