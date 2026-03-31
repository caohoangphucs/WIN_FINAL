package com.example.winfinal.dao;

import com.example.winfinal.entity.inventory.AgriSupply;
import jakarta.persistence.EntityManager;
import java.util.List;

public class AgriSupplyDAO extends BaseDAO<AgriSupply> {
    public AgriSupplyDAO() {
        super(AgriSupply.class);
    }

    // [1.5] Tìm vật tư theo từ khóa (tên hoặc mã)
    public List<AgriSupply> search(String keyword) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT s FROM AgriSupply s " +
                "WHERE LOWER(s.name) LIKE LOWER(:kw) " +
                "   OR LOWER(s.supplyCode) LIKE LOWER(:kw)", AgriSupply.class)
                .setParameter("kw", "%" + keyword + "%")
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [3.1] Cảnh báo vật tư sắp hết hàng (stock_qty <= min_stock)
    public List<AgriSupply> findLowStock() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT s FROM AgriSupply s " +
                "WHERE s.stockQty <= s.minStock " +
                "ORDER BY (s.stockQty / s.minStock) ASC", AgriSupply.class)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [7.3] Kiểm tra mã vật tư đã tồn tại chưa
    public boolean existsBySupplyCode(String supplyCode) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(s.id) FROM AgriSupply s WHERE s.supplyCode = :supplyCode", Long.class)
                           .setParameter("supplyCode", supplyCode)
                           .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    // [7.5] Lấy tồn kho hiện tại (Dùng để validate trước khi bón phân)
    public Double getStockQty(Long supplyId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT s.stockQty FROM AgriSupply s WHERE s.id = :supplyId", Double.class)
                     .setParameter("supplyId", supplyId)
                     .getSingleResult();
        } finally {
            em.close();
        }
    }
}
