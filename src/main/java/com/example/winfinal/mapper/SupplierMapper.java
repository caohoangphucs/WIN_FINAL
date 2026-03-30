package com.example.winfinal.mapper;

import com.example.winfinal.dto.SupplierDTO;
import com.example.winfinal.entity.inventory.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SupplierMapper {
    SupplierMapper INSTANCE = Mappers.getMapper(SupplierMapper.class);

    SupplierDTO toDTO(Supplier entity);

    @Mapping(target = "id", ignore = true)
    Supplier toEntity(SupplierDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(SupplierDTO dto, @MappingTarget Supplier entity);
}
