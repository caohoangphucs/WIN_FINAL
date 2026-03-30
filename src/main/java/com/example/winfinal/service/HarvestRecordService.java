package com.example.winfinal.service;

import com.example.winfinal.dao.HarvestRecordDAO;
import com.example.winfinal.dto.HarvestRecordDTO;
import com.example.winfinal.entity.output.HarvestRecord;
import com.example.winfinal.mapper.HarvestRecordMapper;

public class HarvestRecordService extends BaseService<HarvestRecord, HarvestRecordDTO> {
    private static final HarvestRecordMapper mapper = HarvestRecordMapper.INSTANCE;

    public HarvestRecordService() {
        super(new HarvestRecordDAO());
    }

    @Override
    protected HarvestRecordDTO toDTO(HarvestRecord entity) {
        return mapper.toDTO(entity);
    }

    @Override
    protected HarvestRecord toEntity(HarvestRecordDTO dto) {
        return mapper.toEntity(dto);
    }

    @Override
    protected void updateEntityFromDTO(HarvestRecordDTO dto, HarvestRecord entity) {
        mapper.updateEntityFromDTO(dto, entity);
    }

    @Override
    protected Object getEntityId(HarvestRecordDTO dto) {
        return dto.getId();
    }
}
