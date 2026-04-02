package com.example.winfinal.mapper;

import com.example.winfinal.dto.IrrigationLogDTO;
import com.example.winfinal.entity.operation.IrrigationLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IrrigationLogMapper {
    IrrigationLogMapper INSTANCE = Mappers.getMapper(IrrigationLogMapper.class);

    @Mapping(target = "lotId", source = "lot.id")
    @Mapping(target = "employeeId", source = "employee.id")
    IrrigationLogDTO toDTO(IrrigationLog entity);

    @Mapping(target = "lot.id", source = "lotId")
    @Mapping(target = "employee.id", source = "employeeId")
    @Mapping(target = "id", ignore = true)
    IrrigationLog toEntity(IrrigationLogDTO dto);
}
