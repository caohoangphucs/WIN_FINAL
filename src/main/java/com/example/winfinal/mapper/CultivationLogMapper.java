package com.example.winfinal.mapper;

import com.example.winfinal.dto.CultivationLogDTO;
import com.example.winfinal.entity.core.Employee;
import com.example.winfinal.entity.inventory.AgriSupply;
import com.example.winfinal.entity.lookup.ActivityType;
import com.example.winfinal.entity.operation.CultivationLog;
import com.example.winfinal.entity.production.ProductionLot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CultivationLogMapper {
    CultivationLogMapper INSTANCE = Mappers.getMapper(CultivationLogMapper.class);

    @Mapping(target = "lotId", source = "lot.id")
    @Mapping(target = "lotCode", source = "lot.lotCode")
    @Mapping(target = "supplyId", source = "supply.id")
    @Mapping(target = "supplyName", source = "supply.name")
    @Mapping(target = "supplyUnit", source = "supply.unit")
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeFullName", source = "employee.fullName")
    @Mapping(target = "activityTypeCode", source = "activityType.code")
    CultivationLogDTO toDTO(CultivationLog entity);

    @Mapping(target = "lot", source = "lotId")
    @Mapping(target = "supply", source = "supplyId")
    @Mapping(target = "employee", source = "employeeId")
    @Mapping(target = "activityType", source = "activityTypeCode")
    @Mapping(target = "id", ignore = true)
    CultivationLog toEntity(CultivationLogDTO dto);

    @Mapping(target = "lot", source = "lotId")
    @Mapping(target = "supply", source = "supplyId")
    @Mapping(target = "employee", source = "employeeId")
    @Mapping(target = "activityType", source = "activityTypeCode")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(CultivationLogDTO dto, @MappingTarget CultivationLog entity);

    default ProductionLot mapLot(Long id) {
        if (id == null) return null;
        ProductionLot lot = new ProductionLot();
        lot.setId(id);
        return lot;
    }

    default AgriSupply mapSupply(Long id) {
        if (id == null) return null;
        AgriSupply supply = new AgriSupply();
        supply.setId(id);
        return supply;
    }

    default Employee mapEmployee(Long id) {
        if (id == null) return null;
        Employee emp = new Employee();
        emp.setId(id);
        return emp;
    }

    default ActivityType mapActivityType(String code) {
        if (code == null) return null;
        ActivityType type = new ActivityType();
        type.setCode(code);
        return type;
    }
}
