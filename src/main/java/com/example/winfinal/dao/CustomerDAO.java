package com.example.winfinal.dao;

import com.example.winfinal.entity.output.Customer;
import jakarta.persistence.EntityManager;
import java.util.List;

public class CustomerDAO extends BaseDAO<Customer> {
    public CustomerDAO() {
        super(Customer.class);
    }

    // [1.7] Lấy toàn bộ khách hàng đang hoạt động
    public List<Customer> findAllActive() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Customer c WHERE c.status = 'ACTIVE' ORDER BY c.name", Customer.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // [6.4] Doanh thu / Sản lượng theo khách hàng
    @SuppressWarnings("unchecked")
    public List<Object[]> getYieldStatsByCustomer() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT h.customer.name, h.customer.type, SUM(h.yieldKg), COUNT(h.id) " +
                "FROM HarvestRecord h " +
                "WHERE h.customer IS NOT NULL " +
                "GROUP BY h.customer.id, h.customer.name, h.customer.type " +
                "ORDER BY SUM(h.yieldKg) DESC")
                .getResultList();
        } finally {
            em.close();
        }
    }
}
