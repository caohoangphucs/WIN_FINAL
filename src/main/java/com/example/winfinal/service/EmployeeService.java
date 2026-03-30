package com.example.winfinal.service;

import com.example.winfinal.dao.EmployeeDAO;
import com.example.winfinal.dto.EmployeeDTO;
import com.example.winfinal.entity.core.Employee;
import com.example.winfinal.mapper.EmployeeMapper;

public class EmployeeService extends BaseService<Employee, EmployeeDTO> {
    private static final EmployeeMapper mapper = EmployeeMapper.INSTANCE;

    public EmployeeService() {
        super(new EmployeeDAO());
    }

    @Override
    protected EmployeeDTO toDTO(Employee entity) {
        return mapper.toDTO(entity);
    }

    @Override
    protected Employee toEntity(EmployeeDTO dto) {
        return mapper.toEntity(dto);
    }

    @Override
    protected void updateEntityFromDTO(EmployeeDTO dto, Employee entity) {
        mapper.updateEntityFromDTO(dto, entity);
    }

    @Override
    protected Object getEntityId(EmployeeDTO dto) {
        return dto.getId();
    }
}
