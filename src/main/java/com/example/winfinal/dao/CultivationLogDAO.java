package com.example.winfinal.dao;

import com.example.winfinal.entity.operation.CultivationLog;
import jakarta.persistence.EntityManager;
import java.util.Date;
import java.util.List;

public class CultivationLogDAO extends BaseDAO<CultivationLog> {
    public CultivationLogDAO() {
        super(CultivationLog.class);
    }

    @Override
    public List<CultivationLog> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT c FROM CultivationLog c " +
                "LEFT JOIN FETCH c.lot " +
                "LEFT JOIN FETCH c.supply " +
                "LEFT JOIN FETCH c.activityType " +
                "LEFT JOIN FETCH c.employee " +
                "ORDER BY c.appliedAt DESC", CultivationLog.class)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [3.2] Tổng lượng vật tư đã tiêu thụ theo loại (phân bón/thuốc)
    public List<Object[]> getMaterialConsumptionByType() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT c.supply.category.name, SUM(c.dosageUsed) " +
                "FROM CultivationLog c " +
                "GROUP BY c.supply.category.name " +
                "ORDER BY SUM(c.dosageUsed) DESC")
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [3.5] Tổng lượng vật tư đã dùng cho một lô cụ thể (Tính chi phí đầu vào)
    public List<Object[]> getUsageByLot(Long lotId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT c.supply.name, SUM(c.dosageUsed) " +
                "FROM CultivationLog c " +
                "WHERE c.lot.id = :lotId " +
                "GROUP BY c.supply.name ORDER BY SUM(c.dosageUsed) DESC")
                .setParameter("lotId", lotId)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [3.6] Chi tiết từng lần hoạt động của một lô
    public List<Object[]> getDetailedActivityByLot(Long lotId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT c.activityType.name, c.supply.name, c.dosageUsed, c.appliedAt " +
                "FROM CultivationLog c " +
                "WHERE c.lot.id = :lotId " +
                "ORDER BY c.appliedAt DESC")
                .setParameter("lotId", lotId)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [4.1] Lịch sử bón phân/phun thuốc trong khoảng thời gian (Truy xuất VietGAP)
    public List<CultivationLog> findByLotAndDateRange(Long lotId, Date startDate, Date endDate) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT c FROM CultivationLog c " +
                "WHERE c.lot.id = :lotId AND c.appliedAt BETWEEN :startDate AND :endDate " +
                "ORDER BY c.appliedAt DESC", CultivationLog.class)
                .setParameter("lotId", lotId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [4.3] Thống kê số lần thực hiện theo loại hoạt động của một lô
    public List<Object[]> getActivityStatsByLot(Long lotId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT c.activityType.name, COUNT(c.id) " +
                "FROM CultivationLog c " +
                "WHERE c.lot.id = :lotId " +
                "GROUP BY c.activityType.name")
                .setParameter("lotId", lotId)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [5.2] Nhật ký vật tư đầy đủ theo mã lô (Tra cứu nguồn gốc)
    public List<Object[]> getTraceabilityLogs(String lotCode) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT c.appliedAt, c.activityType.name, c.supply.name, c.dosageUsed, c.employee.fullName " +
                "FROM CultivationLog c " +
                "WHERE c.lot.lotCode = :lotCode " +
                "ORDER BY c.appliedAt ASC")
                .setParameter("lotCode", lotCode)
                .getResultList();
        } finally {
            em.close();
        }
    }
}
