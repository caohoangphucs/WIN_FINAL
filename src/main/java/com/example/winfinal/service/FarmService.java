package com.example.winfinal.service;

import com.example.winfinal.dao.FarmDAO;
import com.example.winfinal.dto.FarmDTO;
import com.example.winfinal.entity.core.*;
import com.example.winfinal.mapper.FarmMapper;
import java.util.List;
import java.util.stream.Collectors;

public class FarmService extends BaseService<Farm, FarmDTO> {
    private static final FarmMapper mapper = FarmMapper.INSTANCE;

    public FarmService() {
        super(new FarmDAO());
    }

    @Override
    protected FarmDTO toDTO(Farm entity) {
        return mapper.toDTO(entity);
    }

    @Override
    protected Farm toEntity(FarmDTO dto) {
        return mapper.toEntity(dto);
    }

    @Override
    protected void updateEntityFromDTO(FarmDTO dto, Farm entity) {
        mapper.updateEntityFromDTO(dto, entity);
    }

    @Override
    protected Object getEntityId(FarmDTO dto) {
        return dto.getId();
    }

    // --- Backward compatibility aliases ---
    public void createFarm(FarmDTO dto) { create(dto); }
    public void updateFarm(FarmDTO dto) { update(dto); }
    public void deleteFarm(Long id) { delete(id); }
    public List<FarmDTO> getAllFarms() { return getAll(); }
}
