package com.example.winfinal.dao;

import com.example.winfinal.entity.operation.IrrigationLog;
import jakarta.persistence.EntityManager;
import java.util.List;

public class IrrigationLogDAO extends BaseDAO<IrrigationLog> {
    public IrrigationLogDAO() {
        super(IrrigationLog.class);
    }

    // [4.4] Tổng lượng nước tưới theo tháng cho một lô
    // Trả về: Object[] { month, totalWaterAmount }
    public List<Object[]> getMonthlyWaterUsage(Long lotId, int year) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT MONTH(i.irrigatedAt), SUM(i.waterAmount) " +
                "FROM IrrigationLog i " +
                "WHERE i.lot.id = :lotId AND YEAR(i.irrigatedAt) = :year " +
                "GROUP BY MONTH(i.irrigatedAt) " +
                "ORDER BY MONTH(i.irrigatedAt)")
                .setParameter("lotId", lotId)
                .setParameter("year", year)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [5.4] Lịch sử tưới tiêu của một lô theo mã lô (Tra cứu nguồn gốc)
    // Trả về: Object[] { irrigatedAt, waterAmount, employee.fullName }
    public List<Object[]> getTraceabilityLogs(String lotCode) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT i.irrigatedAt, i.waterAmount, i.employee.fullName " +
                "FROM IrrigationLog i " +
                "WHERE i.lot.lotCode = :lotCode " +
                "ORDER BY i.irrigatedAt ASC")
                .setParameter("lotCode", lotCode)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // Lấy tất cả nhật ký tưới tiêu của một lô (Entity objects)
    public List<IrrigationLog> findByLot(Long lotId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT i FROM IrrigationLog i WHERE i.lot.id = :lotId ORDER BY i.irrigatedAt DESC",
                IrrigationLog.class)
                .setParameter("lotId", lotId)
                .getResultList();
        } finally {
            em.close();
        }
    }
}
