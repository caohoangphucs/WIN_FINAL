package com.example.winfinal.mapper;

import com.example.winfinal.dto.HarvestRecordDTO;
import com.example.winfinal.entity.core.Employee;
import com.example.winfinal.entity.lookup.QualityGrade;
import com.example.winfinal.entity.output.Customer;
import com.example.winfinal.entity.output.HarvestRecord;
import com.example.winfinal.entity.production.ProductionLot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HarvestRecordMapper {
    HarvestRecordMapper INSTANCE = Mappers.getMapper(HarvestRecordMapper.class);

    @Mapping(target = "lotId", source = "lot.id")
    @Mapping(target = "lotCode", source = "lot.lotCode")
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeName", source = "employee.fullName")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "qualityGradeCode", source = "qualityGrade.code")
    HarvestRecordDTO toDTO(HarvestRecord entity);

    @Mapping(target = "lot", source = "lotId")
    @Mapping(target = "employee", source = "employeeId")
    @Mapping(target = "customer", source = "customerId")
    @Mapping(target = "qualityGrade", source = "qualityGradeCode")
    @Mapping(target = "id", ignore = true)
    HarvestRecord toEntity(HarvestRecordDTO dto);

    @Mapping(target = "lot", source = "lotId")
    @Mapping(target = "employee", source = "employeeId")
    @Mapping(target = "customer", source = "customerId")
    @Mapping(target = "qualityGrade", source = "qualityGradeCode")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(HarvestRecordDTO dto, @MappingTarget HarvestRecord entity);

    default ProductionLot mapLot(Long id) {
        if (id == null) return null;
        ProductionLot lot = new ProductionLot();
        lot.setId(id);
        return lot;
    }

    default Employee mapEmployee(Long id) {
        if (id == null) return null;
        Employee emp = new Employee();
        emp.setId(id);
        return emp;
    }

    default Customer mapCustomer(Long id) {
        if (id == null) return null;
        Customer customer = new Customer();
        customer.setId(id);
        return customer;
    }

    default QualityGrade mapQualityGrade(String code) {
        if (code == null) return null;
        QualityGrade qg = new QualityGrade();
        qg.setCode(code);
        return qg;
    }
}
