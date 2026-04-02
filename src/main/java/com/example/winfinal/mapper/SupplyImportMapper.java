package com.example.winfinal.mapper;

import com.example.winfinal.dto.SupplyImportDTO;
import com.example.winfinal.entity.core.Employee;
import com.example.winfinal.entity.inventory.Supplier;
import com.example.winfinal.entity.inventory.SupplyImport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SupplyImportMapper {
    SupplyImportMapper INSTANCE = Mappers.getMapper(SupplyImportMapper.class);

    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "details", ignore = true)
    SupplyImportDTO toDTO(SupplyImport entity);

    @Mapping(target = "supplier", source = "supplierId")
    @Mapping(target = "employee", source = "employeeId")
    @Mapping(target = "id", ignore = true)
    SupplyImport toEntity(SupplyImportDTO dto);

    @Mapping(target = "supplier", source = "supplierId")
    @Mapping(target = "employee", source = "employeeId")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(SupplyImportDTO dto, @MappingTarget SupplyImport entity);

    default Supplier mapSupplier(Long id) {
        if (id == null) return null;
        Supplier supplier = new Supplier();
        supplier.setId(id);
        return supplier;
    }

    default Employee mapEmployee(Long id) {
        if (id == null) return null;
        Employee emp = new Employee();
        emp.setId(id);
        return emp;
    }
}
