package com.example.winfinal.service;

import com.example.winfinal.dao.SupplierDAO;
import com.example.winfinal.dto.SupplierDTO;
import com.example.winfinal.entity.inventory.Supplier;
import com.example.winfinal.mapper.SupplierMapper;

public class SupplierService extends BaseService<Supplier, SupplierDTO> {
    private static final SupplierMapper mapper = SupplierMapper.INSTANCE;

    public SupplierService() {
        super(new SupplierDAO());
    }

    @Override
    protected SupplierDTO toDTO(Supplier entity) {
        return mapper.toDTO(entity);
    }

    @Override
    protected Supplier toEntity(SupplierDTO dto) {
        return mapper.toEntity(dto);
    }

    @Override
    protected void updateEntityFromDTO(SupplierDTO dto, Supplier entity) {
        mapper.updateEntityFromDTO(dto, entity);
    }

    @Override
    protected Object getEntityId(SupplierDTO dto) {
        return dto.getId();
    }
}
