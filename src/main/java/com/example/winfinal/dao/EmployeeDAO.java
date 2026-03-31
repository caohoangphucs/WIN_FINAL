package com.example.winfinal.dao;

import com.example.winfinal.entity.core.Employee;
import jakarta.persistence.EntityManager;
import java.util.List;

public class EmployeeDAO extends BaseDAO<Employee> {
    public EmployeeDAO() {
        super(Employee.class);
    }

    // [1.6] Tìm nhân viên theo tên hoặc mã
    public List<Employee> search(String keyword) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT e FROM Employee e " +
                "WHERE LOWER(e.fullName) LIKE LOWER(:kw) " +
                "   OR LOWER(e.empCode) LIKE LOWER(:kw)", Employee.class)
                .setParameter("kw", "%" + keyword + "%")
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [4.5] Nhân viên thực hiện nhiều hoạt động canh tác nhất trong thời gian cụ thể (Đánh giá hiệu suất)
    public List<Object[]> getTopPerformers(int month, int year) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT c.employee.fullName, COUNT(c.id) " +
                "FROM CultivationLog c " +
                "WHERE MONTH(c.appliedAt) = :month " +
                "  AND YEAR(c.appliedAt) = :year " +
                "GROUP BY c.employee.id, c.employee.fullName " +
                "ORDER BY COUNT(c.id) DESC")
                .setParameter("month", month)
                .setParameter("year", year)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [7.2] Kiểm tra mã nhân viên đã tồn tại chưa
    public boolean existsByEmpCode(String empCode) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(e.id) FROM Employee e WHERE e.empCode = :empCode", Long.class)
                           .setParameter("empCode", empCode)
                           .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    // [7.6] Đếm số hoạt động nhật ký của một nhân viên trong ngày hôm nay
    public Long countTodayLogs(Long employeeId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(c.id) FROM CultivationLog c " +
                "WHERE c.employee.id = :employeeId " +
                "  AND c.appliedAt >= CURRENT_DATE", Long.class)
                .setParameter("employeeId", employeeId)
                .getSingleResult();
        } finally {
            em.close();
        }
    }
}
