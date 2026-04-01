package com.example.winfinal.dao;

import com.example.winfinal.entity.core.Department;
import jakarta.persistence.EntityManager;
import java.util.List;

public class DepartmentDAO extends BaseDAO<Department> {
    public DepartmentDAO() {
        super(Department.class);
    }

    public List<Department> findByFarm(Long farmId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT d FROM Department d WHERE d.farm.id = :farmId ORDER BY d.deptCode", Department.class)
                    .setParameter("farmId", farmId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
