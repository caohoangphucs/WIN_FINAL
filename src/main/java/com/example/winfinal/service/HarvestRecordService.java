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

    // [2.1] Năng suất trung bình theo loại cây trồng
    public List<Object[]> getAvgYieldByCropType() {
        return harvestDAO.getAvgYieldByCropType();
    }

    // [2.2] Tổng sản lượng theo mùa vụ
    public List<Object[]> getYieldBySeason() {
        return harvestDAO.getYieldBySeason();
    }

    // [2.3] Hiệu suất m² theo lô
    public List<Object[]> getYieldPerM2ByLot() {
        return harvestDAO.getYieldPerM2ByLot();
    }

    // [2.4] Phân bố chất lượng nông sản
    public List<Object[]> getQualityGradeStats() {
        return harvestDAO.getQualityGradeStats();
    }

    // [2.5] Sản lượng theo trang trại
    public List<Object[]> getYieldByFarm() {
        return harvestDAO.getYieldByFarm();
    }

    // [5.5] Tra lịch sử thu hoạch theo mã lô (dùng trong Traceability)
    public List<HarvestRecordDTO> findByLotCode(String lotCode) {
        return harvestDAO.findByLotCode(lotCode).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // [6.4] Xếp hạng khách hàng theo sản lượng
    public List<Object[]> getCustomerYieldStats() {
        return harvestDAO.getCustomerYieldStats();
    }
}
