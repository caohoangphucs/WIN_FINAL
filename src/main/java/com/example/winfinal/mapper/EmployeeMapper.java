package com.example.winfinal.mapper;

import com.example.winfinal.dto.EmployeeDTO;
import com.example.winfinal.entity.core.Department;
import com.example.winfinal.entity.core.Employee;
import com.example.winfinal.entity.lookup.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(target = "roleCode", source = "role.code")
    @Mapping(target = "departmentId", source = "department.id")
    EmployeeDTO toDTO(Employee entity);

    @Mapping(target = "role", source = "roleCode")
    @Mapping(target = "department", source = "departmentId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Employee toEntity(EmployeeDTO dto);

    default Role mapRole(String code) {
        if (code == null) return null;
        Role role = new Role();
        role.setCode(code);
        return role;
    }

    default Department mapDepartment(Long id) {
        if (id == null) return null;
        Department dept = new Department();
        dept.setId(id);
        return dept;
    }
}
