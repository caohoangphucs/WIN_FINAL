package com.example.winfinal.service;

import com.example.winfinal.dao.ProductionLotDAO;
import com.example.winfinal.dto.ProductionLotDTO;
import com.example.winfinal.entity.production.ProductionLot;
import com.example.winfinal.mapper.ProductionLotMapper;

public class ProductionLotService extends BaseService<ProductionLot, ProductionLotDTO> {
    private static final ProductionLotMapper mapper = ProductionLotMapper.INSTANCE;

    public ProductionLotService() {
        super(new ProductionLotDAO());
    }

    @Override
    protected ProductionLotDTO toDTO(ProductionLot entity) {
        return mapper.toDTO(entity);
    }

    @Override
    protected ProductionLot toEntity(ProductionLotDTO dto) {
        return mapper.toEntity(dto);
    }

    @Override
    protected void updateEntityFromDTO(ProductionLotDTO dto, ProductionLot entity) {
        mapper.updateEntityFromDTO(dto, entity);
    }

    @Override
    protected Object getEntityId(ProductionLotDTO dto) {
        return dto.getId();
    }
}
