package com.example.winfinal.dao;

import com.example.winfinal.entity.operation.WeatherLog;
import jakarta.persistence.EntityManager;
import java.util.List;

public class WeatherLogDAO extends BaseDAO<WeatherLog> {
    public WeatherLogDAO() {
        super(WeatherLog.class);
    }

    // [6.5] Thống kê ngày mưa trong tháng theo trang trại
    // Trả về: Object[] { month, rainyDayCount, avgRainfallMm }
    public List<Object[]> getMonthlyRainStats(Long farmId, int year) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT MONTH(w.weatherDate), COUNT(w.id), AVG(w.rainfallMm) " +
                "FROM WeatherLog w " +
                "WHERE w.farm.id = :farmId " +
                "  AND YEAR(w.weatherDate) = :year " +
                "  AND w.rainfallMm > 0 " +
                "GROUP BY MONTH(w.weatherDate) " +
                "ORDER BY MONTH(w.weatherDate)")
                .setParameter("farmId", farmId)
                .setParameter("year", year)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // Lấy nhật ký thời tiết của một trang trại trong khoảng thời gian
    public List<WeatherLog> findByFarmAndYear(Long farmId, int year) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT w FROM WeatherLog w " +
                "WHERE w.farm.id = :farmId AND YEAR(w.weatherDate) = :year " +
                "ORDER BY w.weatherDate ASC", WeatherLog.class)
                .setParameter("farmId", farmId)
                .setParameter("year", year)
                .getResultList();
        } finally {
            em.close();
        }
    }
}
