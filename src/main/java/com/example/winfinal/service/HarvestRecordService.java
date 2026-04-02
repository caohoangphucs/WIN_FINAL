package com.example.winfinal.service;

import com.example.winfinal.dao.HarvestRecordDAO;
import com.example.winfinal.dto.HarvestRecordDTO;
import com.example.winfinal.entity.output.HarvestRecord;
import com.example.winfinal.mapper.HarvestRecordMapper;
import java.util.List;
import java.util.stream.Collectors;

public class HarvestRecordService extends BaseService<HarvestRecord, HarvestRecordDTO> {
    private static final HarvestRecordMapper mapper = HarvestRecordMapper.INSTANCE;
    private final HarvestRecordDAO harvestDAO;

    public HarvestRecordService() {
        super(new HarvestRecordDAO());
        this.harvestDAO = (HarvestRecordDAO) dao;
    }

    @Override protected HarvestRecordDTO toDTO(HarvestRecord e) { return mapper.toDTO(e); }
    @Override protected HarvestRecord toEntity(HarvestRecordDTO d) { return mapper.toEntity(d); }
    @Override protected void updateEntityFromDTO(HarvestRecordDTO d, HarvestRecord e) { mapper.updateEntityFromDTO(d, e); }
    @Override protected Object getEntityId(HarvestRecordDTO d) { return d.getId(); }

    public List<Object[]> getAvgYieldByCropType() { return harvestDAO.getAvgYieldByCropType(); }
    public List<Object[]> getYieldBySeason() { return harvestDAO.getYieldBySeason(); }
    public List<Object[]> getYieldPerM2ByLot() { return harvestDAO.getYieldPerM2ByLot(); }
    public List<Object[]> getQualityGradeStats() { return harvestDAO.getQualityGradeStats(); }
    public List<Object[]> getYieldByFarm() { return harvestDAO.getYieldByFarm(); }
    public List<Object[]> getCustomerYieldStats() { return harvestDAO.getCustomerYieldStats(); }

    public List<HarvestRecordDTO> findByLotCode(String lotCode) {
        return harvestDAO.findByLotCode(lotCode).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<HarvestRecordDTO> findByLot(Long lotId) {
        return harvestDAO.findByLot(lotId).stream().map(this::toDTO).collect(Collectors.toList());
    }
}
