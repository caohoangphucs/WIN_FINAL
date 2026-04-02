package com.example.winfinal.mapper;

import com.example.winfinal.dto.PestReportDTO;
import com.example.winfinal.entity.operation.PestReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PestReportMapper {
    PestReportMapper INSTANCE = Mappers.getMapper(PestReportMapper.class);

    @Mapping(target = "lotId", source = "lot.id")
    @Mapping(target = "lotCode", source = "lot.lotCode")
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeName", source = "employee.fullName")
    @Mapping(target = "severityCode", source = "severity.code")
    PestReportDTO toDTO(PestReport entity);

    @Mapping(target = "lot.id", source = "lotId")
    @Mapping(target = "employee.id", source = "employeeId")
    @Mapping(target = "severity.code", source = "severityCode")
    @Mapping(target = "id", ignore = true)
    PestReport toEntity(PestReportDTO dto);
}
