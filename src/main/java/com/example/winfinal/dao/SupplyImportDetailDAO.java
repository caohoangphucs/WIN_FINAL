package com.example.winfinal.dao;

import com.example.winfinal.entity.inventory.SupplyImportDetail;
import jakarta.persistence.EntityManager;
import java.util.List;

public class SupplyImportDetailDAO extends BaseDAO<SupplyImportDetail> {
    public SupplyImportDetailDAO() {
        super(SupplyImportDetail.class);
    }

    // [6.6] Tổng chi phí vật tư theo lô để tính lợi nhuận (Dùng giá trung bình)
    // Trả về: Object[] { lotCode, cropName, totalCostEstimate }
    public List<Object[]> getCostEstimateByLot() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT l.lotCode, l.cropType.name, " +
                "       SUM(c.dosageUsed * " +
                "           (SELECT AVG(d.unitPrice) FROM SupplyImportDetail d WHERE d.supply.id = c.supply.id)) " +
                "FROM CultivationLog c JOIN c.lot l " +
                "GROUP BY l.id, l.lotCode, l.cropType.name " +
                "ORDER BY 3 DESC")
                .getResultList();
        } finally {
            em.close();
        }
    }

    // Lấy giá trung bình nhập kho của một vật tư
    public Double getAvgUnitPrice(Long supplyId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT AVG(d.unitPrice) FROM SupplyImportDetail d WHERE d.supply.id = :supplyId",
                Double.class)
                .setParameter("supplyId", supplyId)
                .getSingleResult();
        } finally {
            em.close();
        }
    }
}
