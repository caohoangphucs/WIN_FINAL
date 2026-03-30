package com.example.winfinal.service;

import com.example.winfinal.dao.DepartmentDAO;
import com.example.winfinal.dto.DepartmentDTO;
import com.example.winfinal.entity.core.Department;
import com.example.winfinal.mapper.DepartmentMapper;

public class DepartmentService extends BaseService<Department, DepartmentDTO> {
    private static final DepartmentMapper mapper = DepartmentMapper.INSTANCE;

    public DepartmentService() {
        super(new DepartmentDAO());
    }

    @Override
    protected DepartmentDTO toDTO(Department entity) {
        return mapper.toDTO(entity);
    }

    @Override
    protected Department toEntity(DepartmentDTO dto) {
        return mapper.toEntity(dto);
    }

    @Override
    protected void updateEntityFromDTO(DepartmentDTO dto, Department entity) {
        mapper.updateEntityFromDTO(dto, entity);
    }

    @Override
    protected Object getEntityId(DepartmentDTO dto) {
        return dto.getId();
    }
}
