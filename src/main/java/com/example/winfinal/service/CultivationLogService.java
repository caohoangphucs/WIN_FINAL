package com.example.winfinal.service;

import com.example.winfinal.dao.CultivationLogDAO;
import com.example.winfinal.dto.CultivationLogDTO;
import com.example.winfinal.entity.operation.CultivationLog;
import com.example.winfinal.mapper.CultivationLogMapper;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CultivationLogService extends BaseService<CultivationLog, CultivationLogDTO> {
    private static final CultivationLogMapper mapper = CultivationLogMapper.INSTANCE;
    private final CultivationLogDAO cultivationDAO;

    public CultivationLogService() {
        super(new CultivationLogDAO());
        this.cultivationDAO = (CultivationLogDAO) dao;
    }

    @Override protected CultivationLogDTO toDTO(CultivationLog e) { return mapper.toDTO(e); }
    @Override protected CultivationLog toEntity(CultivationLogDTO d) { return mapper.toEntity(d); }
    @Override protected void updateEntityFromDTO(CultivationLogDTO d, CultivationLog e) { mapper.updateEntityFromDTO(d, e); }
    @Override protected Object getEntityId(CultivationLogDTO d) { return d.getId(); }

    // [3.2] Tổng vật tư tiêu thụ theo loại
    public List<Object[]> getMaterialConsumptionByType() {
        return cultivationDAO.getMaterialConsumptionByType();
    }

    // [3.5] Tổng vật tư đã dùng cho một lô
    public List<Object[]> getUsageByLot(Long lotId) {
        return cultivationDAO.getUsageByLot(lotId);
    }

    // [3.6] Chi tiết từng hoạt động trong một lô
    public List<Object[]> getDetailedActivityByLot(Long lotId) {
        return cultivationDAO.getDetailedActivityByLot(lotId);
    }

    // [4.1] Nhật ký theo lô và khoảng thời gian (VietGAP)
    public List<CultivationLogDTO> findByLotAndDateRange(Long lotId, Date startDate, Date endDate) {
        return cultivationDAO.findByLotAndDateRange(lotId, startDate, endDate)
                             .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // [4.3] Thống kê hoạt động theo lô
    public List<Object[]> getActivityStatsByLot(Long lotId) {
        return cultivationDAO.getActivityStatsByLot(lotId);
    }

    // [5.2] Nhật ký vật tư cho truy xuất nguồn gốc
    public List<Object[]> getTraceabilityLogs(String lotCode) {
        return cultivationDAO.getTraceabilityLogs(lotCode);
    }
}
