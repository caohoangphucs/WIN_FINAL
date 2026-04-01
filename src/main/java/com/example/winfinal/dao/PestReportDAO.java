package com.example.winfinal.dao;

import com.example.winfinal.entity.operation.PestReport;
import jakarta.persistence.EntityManager;
import java.util.List;

public class PestReportDAO extends BaseDAO<PestReport> {
    public PestReportDAO() {
        super(PestReport.class);
    }

    // [1.1] Lấy toàn bộ báo cáo sâu bệnh (với JOIN FETCH để tránh LazyInitializationException)
    @Override
    public List<PestReport> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM PestReport p " +
                "JOIN FETCH p.lot " +
                "JOIN FETCH p.employee " +
                "JOIN FETCH p.severity " +
                "ORDER BY p.id DESC", PestReport.class)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [4.2] Tìm các lô đang có cảnh báo sâu bệnh nghiêm trọng
    public List<PestReport> findHighSeverityReports() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM PestReport p " +
                "JOIN FETCH p.lot " +
                "JOIN FETCH p.employee " +
                "JOIN FETCH p.severity " +
                "WHERE p.severity.code IN ('HIGH', 'CRITICAL') " +
                "  AND p.lot.status.code != 'HARVESTED' " +
                "ORDER BY p.lot.id", PestReport.class)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [5.3] Tình hình sâu bệnh của một lô theo mã lô (Tra cứu nguồn gốc)
    // Trả về: Object[] { pestName, severity, employee.fullName }
    public List<Object[]> getTraceabilityLogs(String lotCode) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT p.lot.id, p.severity.code, p.employee.fullName " +
                "FROM PestReport p " +
                "WHERE p.lot.lotCode = :lotCode " +
                "ORDER BY p.lot.id")
                .setParameter("lotCode", lotCode)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // [6.3] Thống kê số lần sâu bệnh theo mức độ trong một mùa vụ
    // Trả về: Object[] { severity, count }
    public List<Object[]> getSeverityStatsBySeason(Long seasonId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT p.severity.code, COUNT(p.id) " +
                "FROM PestReport p " +
                "JOIN p.lot l " +
                "WHERE l.season.id = :seasonId " +
                "GROUP BY p.severity.code " +
                "ORDER BY COUNT(p.id) DESC")
                .setParameter("seasonId", seasonId)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // Lấy tất cả báo cáo sâu bệnh theo lô
    public List<PestReport> findByLot(Long lotId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM PestReport p " +
                "JOIN FETCH p.lot " +
                "JOIN FETCH p.employee " +
                "JOIN FETCH p.severity " +
                "WHERE p.lot.id = :lotId " +
                "ORDER BY p.lot.id", PestReport.class)
                .setParameter("lotId", lotId)
                .getResultList();
        } finally {
            em.close();
        }
    }
}
