package com.example.winfinal.service;

import com.example.winfinal.dao.BaseDAO;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic Base Service for CRUD operations
 * T: Entity type
 * D: DTO type
 */
public abstract class BaseService<T, D> {
    protected final BaseDAO<T> dao;

    protected BaseService(BaseDAO<T> dao) {
        this.dao = dao;
    }

    protected abstract D toDTO(T entity);
    protected abstract T toEntity(D dto);
    protected abstract void updateEntityFromDTO(D dto, T entity);
    protected abstract Object getEntityId(D dto);

    protected void validate(D dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu không được để trống (null)");
        }
    }

    public void create(D dto) {
        validate(dto);
        T entity = toEntity(dto);
        dao.save(entity);
    }

    public void update(D dto) {
        validate(dto);
        Object id = getEntityId(dto);
        if (id == null) throw new IllegalArgumentException("ID không được để trống khi phân bổ cập nhật (update)");
        
        T entity = dao.findById(id);
        if (entity != null) {
            updateEntityFromDTO(dto, entity);
            dao.update(entity);
        } else {
            throw new IllegalArgumentException("Không tìm thấy thực thể với ID: " + id);
        }
    }

    public void delete(Object id) {
        dao.delete(id);
    }

    public List<D> getAll() {
        return dao.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public D getById(Object id) {
        T entity = dao.findById(id);
        return entity != null ? toDTO(entity) : null;
    }
}
