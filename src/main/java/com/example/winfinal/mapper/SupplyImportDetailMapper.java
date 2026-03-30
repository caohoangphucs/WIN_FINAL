package com.example.winfinal.mapper;

import com.example.winfinal.dto.SupplyImportDetailDTO;
import com.example.winfinal.entity.inventory.AgriSupply;
import com.example.winfinal.entity.inventory.SupplyImport;
import com.example.winfinal.entity.inventory.SupplyImportDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SupplyImportDetailMapper {
    SupplyImportDetailMapper INSTANCE = Mappers.getMapper(SupplyImportDetailMapper.class);

    @Mapping(target = "supplyId", source = "supply.id")
    @Mapping(target = "importId", source = "supplyImport.id")
    SupplyImportDetailDTO toDTO(SupplyImportDetail entity);

    @Mapping(target = "supply", source = "supplyId")
    @Mapping(target = "supplyImport", source = "importId")
    @Mapping(target = "id", ignore = true)
    SupplyImportDetail toEntity(SupplyImportDetailDTO dto);

    @Mapping(target = "supply", source = "supplyId")
    @Mapping(target = "supplyImport", source = "importId")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(SupplyImportDetailDTO dto, @MappingTarget SupplyImportDetail entity);

    default SupplyImport mapImport(Long id) {
        if (id == null) return null;
        SupplyImport imp = new SupplyImport();
        imp.setId(id);
        return imp;
    }

    default AgriSupply mapSupply(Long id) {
        if (id == null) return null;
        AgriSupply supply = new AgriSupply();
        supply.setId(id);
        return supply;
    }
}
