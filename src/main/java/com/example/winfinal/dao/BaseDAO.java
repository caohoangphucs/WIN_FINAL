package com.example.winfinal.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public abstract class BaseDAO<T> {
    private static EntityManagerFactory emf;

    public static void setEntityManagerFactory(EntityManagerFactory factory) {
        emf = factory;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            java.util.Map<String, String> properties = new java.util.HashMap<>();
            String dbUrl = System.getenv("DB_URL");
            if (dbUrl != null && !dbUrl.isEmpty()) {
                properties.put("jakarta.persistence.jdbc.url", dbUrl);
            }
            emf = Persistence.createEntityManagerFactory("WinFinalPU", properties);
        }
        return emf;
    }

    private final Class<T> entityClass;

    protected BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public void save(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public T findById(Object id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("from " + entityClass.getSimpleName(), entityClass).getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(Object id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
