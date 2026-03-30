package com.example.winfinal.mapper;

import com.example.winfinal.dto.ProductionLotDTO;
import com.example.winfinal.entity.core.Employee;
import com.example.winfinal.entity.core.Farm;
import com.example.winfinal.entity.lookup.LotStatus;
import com.example.winfinal.entity.master.CropType;
import com.example.winfinal.entity.master.Season;
import com.example.winfinal.entity.production.ProductionLot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductionLotMapper {
    ProductionLotMapper INSTANCE = Mappers.getMapper(ProductionLotMapper.class);

    @Mapping(target = "statusCode", source = "status.code")
    @Mapping(target = "farmId", source = "farm.id")
    @Mapping(target = "cropTypeId", source = "cropType.id")
    @Mapping(target = "managerId", source = "manager.id")
    ProductionLotDTO toDTO(ProductionLot entity);

    @Mapping(target = "status", source = "statusCode")
    @Mapping(target = "farm", source = "farmId")
    @Mapping(target = "cropType", source = "cropTypeId")
    @Mapping(target = "manager", source = "managerId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actualHarvestDate", ignore = true)
    @Mapping(target = "season", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductionLot toEntity(ProductionLotDTO dto);

    @Mapping(target = "status", source = "statusCode")
    @Mapping(target = "farm", source = "farmId")
    @Mapping(target = "cropType", source = "cropTypeId")
    @Mapping(target = "manager", source = "managerId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actualHarvestDate", ignore = true)
    @Mapping(target = "season", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ProductionLotDTO dto, @MappingTarget ProductionLot entity);

    default LotStatus mapStatus(String code) {
        if (code == null) return null;
        LotStatus status = new LotStatus();
        status.setCode(code);
        return status;
    }

    default Farm mapFarm(Long id) {
        if (id == null) return null;
        Farm farm = new Farm();
        farm.setId(id);
        return farm;
    }

    default CropType mapCropType(Long id) {
        if (id == null) return null;
        CropType type = new CropType();
        type.setId(id);
        return type;
    }

    default Employee mapEmployee(Long id) {
        if (id == null) return null;
        Employee emp = new Employee();
        emp.setId(id);
        return emp;
    }
}
