package com.example.winfinal.service;

import com.example.winfinal.dao.CropTypeDAO;
import com.example.winfinal.dto.CropTypeDTO;
import com.example.winfinal.entity.master.CropType;
import com.example.winfinal.mapper.CropTypeMapper;

public class CropTypeService extends BaseService<CropType, CropTypeDTO> {
    private static final CropTypeMapper mapper = CropTypeMapper.INSTANCE;

    public CropTypeService() {
        super(new CropTypeDAO());
    }

    @Override
    protected CropTypeDTO toDTO(CropType entity) {
        return mapper.toDTO(entity);
    }

    @Override
    protected CropType toEntity(CropTypeDTO dto) {
        return mapper.toEntity(dto);
    }

    @Override
    protected void updateEntityFromDTO(CropTypeDTO dto, CropType entity) {
        mapper.updateEntityFromDTO(dto, entity);
    }

    @Override
    protected Object getEntityId(CropTypeDTO dto) {
        return dto.getId();
    }
}
