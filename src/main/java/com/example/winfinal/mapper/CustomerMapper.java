package com.example.winfinal.mapper;

import com.example.winfinal.dto.CustomerDTO;
import com.example.winfinal.entity.output.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    CustomerDTO toDTO(Customer entity);

    @Mapping(target = "id", ignore = true)
    Customer toEntity(CustomerDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(CustomerDTO dto, @MappingTarget Customer entity);
}
