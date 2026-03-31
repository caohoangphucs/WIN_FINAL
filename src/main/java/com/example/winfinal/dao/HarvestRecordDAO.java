package com.example.winfinal.dao;

import com.example.winfinal.entity.output.HarvestRecord;
import jakarta.persistence.EntityManager;
import java.util.List;

public class HarvestRecordDAO extends BaseDAO<HarvestRecord> {
    public HarvestRecordDAO() {
        super(HarvestRecord.class);
    }

    // [2.1] Năng suất trung bình theo loại cây trồng
    public List<Object[]> getAvgYieldByCropType() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT l.cropType.name, AVG(h.yieldKg) " +
                "FROM HarvestRecord h JOIN h.lot l " +
                "WHERE h.yieldKg IS NOT NULL " +
                "GROUP BY l.cropType.name " +
                "ORDER BY AVG(h.yieldKg) DESC")
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [2.2] Tổng sản lượng thu hoạch theo mùa vụ
    public List<Object[]> getYieldBySeason() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT l.season.name, SUM(h.yieldKg) " +
                "FROM HarvestRecord h JOIN h.lot l " +
                "WHERE h.yieldKg IS NOT NULL " +
                "GROUP BY l.season.name " +
                "ORDER BY SUM(h.yieldKg) DESC")
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [2.3] Hiệu suất diện tích lô canh tác (Sản lượng / m2)
    public List<Object[]> getYieldPerM2ByLot() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT l.lotCode, SUM(h.yieldKg) / l.areaM2 " +
                "FROM HarvestRecord h JOIN h.lot l " +
                "WHERE h.yieldKg IS NOT NULL AND l.areaM2 > 0 " +
                "GROUP BY l.lotCode, l.areaM2 " +
                "ORDER BY SUM(h.yieldKg) / l.areaM2 DESC")
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [2.4] Tỉ lệ xếp loại chất lượng nông sản (Grade Stats)
    public List<Object[]> getQualityGradeStats() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT h.qualityGrade.code, COUNT(h.id) " +
                "FROM HarvestRecord h " +
                "WHERE h.qualityGrade IS NOT NULL " +
                "GROUP BY h.qualityGrade.code " +
                "ORDER BY h.qualityGrade.code")
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [2.5] Tổng sản lượng thu hoạch theo trang trại
    public List<Object[]> getYieldByFarm() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT l.farm.name, SUM(h.yieldKg) " +
                "FROM HarvestRecord h JOIN h.lot l " +
                "WHERE h.yieldKg IS NOT NULL " +
                "GROUP BY l.farm.name " +
                "ORDER BY SUM(h.yieldKg) DESC")
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [5.5] Truy xuất đầu ra của lô (Nhật ký thu hoạch và Khách hàng)
    public List<HarvestRecord> findByLotCode(String lotCode) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT h FROM HarvestRecord h WHERE h.lot.lotCode = :lotCode ORDER BY h.harvestDate ASC", 
                HarvestRecord.class)
                .setParameter("lotCode", lotCode)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [6.4] Xếp hạng doanh số / sản lượng theo khách hàng
    public List<Object[]> getCustomerYieldStats() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT h.customer.name, SUM(h.yieldKg), COUNT(h.id) " +
                "FROM HarvestRecord h WHERE h.customer IS NOT NULL " +
                "GROUP BY h.customer.id, h.customer.name " +
                "ORDER BY SUM(h.yieldKg) DESC")
                .getResultList();
        } finally {
            em.close();
        }
    }
}
