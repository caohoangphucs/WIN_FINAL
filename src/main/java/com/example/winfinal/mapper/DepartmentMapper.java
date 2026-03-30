package com.example.winfinal.mapper;

import com.example.winfinal.dto.DepartmentDTO;
import com.example.winfinal.entity.core.Department;
import com.example.winfinal.entity.core.Farm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DepartmentMapper {
    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    @Mapping(target = "farmId", source = "farm.id")
    DepartmentDTO toDTO(Department entity);

    @Mapping(target = "farm", source = "farmId")
    @Mapping(target = "id", ignore = true)
    Department toEntity(DepartmentDTO dto);

    @Mapping(target = "farm", source = "farmId")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(DepartmentDTO dto, @MappingTarget Department entity);

    default Farm mapFarm(Long id) {
        if (id == null) return null;
        Farm farm = new Farm();
        farm.setId(id);
        return farm;
    }
}
