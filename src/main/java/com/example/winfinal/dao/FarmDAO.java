package com.example.winfinal.dao;

import com.example.winfinal.entity.core.Farm;
import jakarta.persistence.EntityManager;
import java.util.List;

public class FarmDAO extends BaseDAO<Farm> {
    public FarmDAO() {
        super(Farm.class);
    }

    // [6.1] Tổng quan hoạt động của trang trại theo mùa vụ
    // Trả về: Object[] { seasonName, lotCount, totalAreaM2, totalYieldKg }
    public List<Object[]> getSeasonalSummary(Long farmId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT l.season.name, COUNT(DISTINCT l.id), SUM(l.areaM2), SUM(h.yieldKg) " +
                "FROM ProductionLot l " +
                "LEFT JOIN HarvestRecord h ON h.lot.id = l.id " +
                "WHERE l.farm.id = :farmId " +
                "GROUP BY l.season.id, l.season.name " +
                "ORDER BY l.season.startDate DESC")
                .setParameter("farmId", farmId)
                .getResultList();
        } finally {
            em.close();
        }
    }
}
