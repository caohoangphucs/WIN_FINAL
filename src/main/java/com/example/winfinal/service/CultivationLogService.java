package com.example.winfinal.service;

import com.example.winfinal.dao.CultivationLogDAO;
import com.example.winfinal.dto.CultivationLogDTO;
import com.example.winfinal.entity.operation.CultivationLog;
import com.example.winfinal.mapper.CultivationLogMapper;

public class CultivationLogService extends BaseService<CultivationLog, CultivationLogDTO> {
    private static final CultivationLogMapper mapper = CultivationLogMapper.INSTANCE;

    public CultivationLogService() {
        super(new CultivationLogDAO());
    }

    @Override
    protected CultivationLogDTO toDTO(CultivationLog entity) {
        return mapper.toDTO(entity);
    }

    @Override
    protected CultivationLog toEntity(CultivationLogDTO dto) {
        return mapper.toEntity(dto);
    }

    @Override
    protected void updateEntityFromDTO(CultivationLogDTO dto, CultivationLog entity) {
        mapper.updateEntityFromDTO(dto, entity);
    }

    @Override
    protected Object getEntityId(CultivationLogDTO dto) {
        return dto.getId();
    }
}
