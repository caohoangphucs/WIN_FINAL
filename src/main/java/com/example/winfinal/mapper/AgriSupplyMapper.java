package com.example.winfinal.mapper;

import com.example.winfinal.dto.AgriSupplyDTO;
import com.example.winfinal.entity.inventory.AgriSupply;
import com.example.winfinal.entity.inventory.Supplier;
import com.example.winfinal.entity.master.SupplyCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AgriSupplyMapper {
    AgriSupplyMapper INSTANCE = Mappers.getMapper(AgriSupplyMapper.class);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "supplierId", source = "supplier.id")
    AgriSupplyDTO toDTO(AgriSupply entity);

    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "supplier", source = "supplierId")
    @Mapping(target = "id", ignore = true)
    AgriSupply toEntity(AgriSupplyDTO dto);

    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "supplier", source = "supplierId")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(AgriSupplyDTO dto, @MappingTarget AgriSupply entity);

    default SupplyCategory mapCategory(Long id) {
        if (id == null) return null;
        SupplyCategory category = new SupplyCategory();
        category.setId(id);
        return category;
    }

    default Supplier mapSupplier(Long id) {
        if (id == null) return null;
        Supplier supplier = new Supplier();
        supplier.setId(id);
        return supplier;
    }
}
