package com.example.winfinal.dao;

import com.example.winfinal.entity.production.ProductionLot;
import com.example.winfinal.entity.lookup.LotStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.Date;
import java.util.List;

public class ProductionLotDAO extends BaseDAO<ProductionLot> {
    public ProductionLotDAO() {
        super(ProductionLot.class);
    }

    // [1.1] Lấy toàn bộ lô sản xuất, sắp xếp theo ngày xuống giống mới nhất
    @Override
    public List<ProductionLot> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT l FROM ProductionLot l ORDER BY l.plantDate DESC", ProductionLot.class).getResultList();
        } finally {
            em.close();
        }
    }

    // [1.2] Tìm lô theo mã lô (Exact match)
    public ProductionLot findByLotCode(String lotCode) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT l FROM ProductionLot l WHERE l.lotCode = :lotCode", ProductionLot.class)
                    .setParameter("lotCode", lotCode)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    // [1.3] Tìm lô theo trạng thái
    public List<ProductionLot> findByStatus(String statusCode) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT l FROM ProductionLot l WHERE l.status.code = :statusCode ORDER BY l.lotCode", ProductionLot.class)
                    .setParameter("statusCode", statusCode)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // [1.4] Tìm lô theo trang trại
    public List<ProductionLot> findByFarm(Long farmId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT l FROM ProductionLot l WHERE l.farm.id = :farmId ORDER BY l.plantDate DESC", ProductionLot.class)
                    .setParameter("farmId", farmId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // [1.8] Tìm kiếm lô theo nhiều điều kiện động
    public List<ProductionLot> searchByConditions(Long farmId, String statusCode, Long cropTypeId, Long seasonId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT l FROM ProductionLot l " +
                "WHERE (:farmId IS NULL OR l.farm.id = :farmId) " +
                "  AND (:statusCode IS NULL OR l.status.code = :statusCode) " +
                "  AND (:cropTypeId IS NULL OR l.cropType.id = :cropTypeId) " +
                "  AND (:seasonId IS NULL OR l.season.id = :seasonId) " +
                "ORDER BY l.plantDate DESC", ProductionLot.class)
                .setParameter("farmId", farmId)
                .setParameter("statusCode", statusCode)
                .setParameter("cropTypeId", cropTypeId)
                .setParameter("seasonId", seasonId)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [2.6] Danh sách lô sắp đến ngày thu hoạch (trong khoảng thời gian tới)
    public List<ProductionLot> findUpcomingHarvest(Date nextDate) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT l FROM ProductionLot l " +
                "WHERE l.expectedHarvestDate BETWEEN CURRENT_DATE AND :nextDate " +
                "  AND l.status.code NOT IN ('HARVESTED', 'IDLE') " +
                "ORDER BY l.expectedHarvestDate ASC", ProductionLot.class)
                .setParameter("nextDate", nextDate)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [5.1] Truy xuất thông tin đầy đủ của một lô (JOIN FETCH tối ưu)
    public ProductionLot getFullTraceabilityInfo(String lotCode) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT l FROM ProductionLot l " +
                "JOIN FETCH l.farm JOIN FETCH l.cropType JOIN FETCH l.season JOIN FETCH l.manager " +
                "WHERE l.lotCode = :lotCode", ProductionLot.class)
                .setParameter("lotCode", lotCode)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    // [6.2] Top N lô có sản lượng cao nhất
    public List<Object[]> getTopYieldingLots(int limit) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT l.lotCode, l.cropType.name, SUM(h.yieldKg) " +
                "FROM HarvestRecord h JOIN h.lot l " +
                "GROUP BY l.id, l.lotCode, l.cropType.name " +
                "ORDER BY SUM(h.yieldKg) DESC")
                .setMaxResults(limit)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [7.1] Kiểm tra mã lô tồn tại
    public boolean existsByLotCode(String lotCode) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(l.id) FROM ProductionLot l WHERE l.lotCode = :lotCode", Long.class)
                           .setParameter("lotCode", lotCode)
                           .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
