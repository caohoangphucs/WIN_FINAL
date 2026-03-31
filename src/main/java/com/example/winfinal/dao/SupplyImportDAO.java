package com.example.winfinal.dao;

import com.example.winfinal.entity.inventory.SupplyImport;
import jakarta.persistence.EntityManager;
import java.util.List;

public class SupplyImportDAO extends BaseDAO<SupplyImport> {
    public SupplyImportDAO() {
        super(SupplyImport.class);
    }

    // [3.3] Tổng chi phí nhập kho theo nhà cung cấp
    // Trả về: Object[] { supplierName, totalAmount }
    public List<Object[]> getTotalCostBySupplier() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT d.supply.supplier.name, SUM(d.quantity * d.unitPrice) " +
                "FROM SupplyImportDetail d " +
                "GROUP BY d.supply.supplier.name " +
                "ORDER BY SUM(d.quantity * d.unitPrice) DESC")
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [3.4] Lịch sử nhập kho của một vật tư cụ thể
    public List<SupplyImport> findBySupply(Long supplyId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT si FROM SupplyImport si " +
                "JOIN si.details d " +
                "WHERE d.supply.id = :supplyId " +
                "ORDER BY si.importDate DESC", SupplyImport.class)
                .setParameter("supplyId", supplyId)
                .getResultList();
        } finally {
            em.close();
        }
    }
}
