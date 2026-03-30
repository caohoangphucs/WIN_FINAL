package com.example.winfinal.service;

import com.example.winfinal.dao.AgriSupplyDAO;
import com.example.winfinal.dto.AgriSupplyDTO;
import com.example.winfinal.entity.inventory.AgriSupply;
import com.example.winfinal.mapper.AgriSupplyMapper;

public class AgriSupplyService extends BaseService<AgriSupply, AgriSupplyDTO> {
    private static final AgriSupplyMapper mapper = AgriSupplyMapper.INSTANCE;

    public AgriSupplyService() {
        super(new AgriSupplyDAO());
    }

    @Override
    protected AgriSupplyDTO toDTO(AgriSupply entity) {
        return mapper.toDTO(entity);
    }

    @Override
    protected AgriSupply toEntity(AgriSupplyDTO dto) {
        return mapper.toEntity(dto);
    }

    @Override
    protected void updateEntityFromDTO(AgriSupplyDTO dto, AgriSupply entity) {
        mapper.updateEntityFromDTO(dto, entity);
    }

    @Override
    protected Object getEntityId(AgriSupplyDTO dto) {
        return dto.getId();
    }
}
