package com.example.winfinal.mapper;

import com.example.winfinal.dto.CropTypeDTO;
import com.example.winfinal.entity.master.CropCategory;
import com.example.winfinal.entity.master.CropType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CropTypeMapper {
    CropTypeMapper INSTANCE = Mappers.getMapper(CropTypeMapper.class);

    @Mapping(target = "categoryId", source = "category.id")
    CropTypeDTO toDTO(CropType entity);

    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "id", ignore = true)
    CropType toEntity(CropTypeDTO dto);

    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(CropTypeDTO dto, @MappingTarget CropType entity);

    default CropCategory mapCategory(Long id) {
        if (id == null) return null;
        CropCategory category = new CropCategory();
        category.setId(id);
        return category;
    }
}
