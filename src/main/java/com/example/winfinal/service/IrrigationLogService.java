package com.example.winfinal.service;

import com.example.winfinal.dao.IrrigationLogDAO;
import com.example.winfinal.dto.IrrigationLogDTO;
import com.example.winfinal.entity.operation.IrrigationLog;
import com.example.winfinal.mapper.IrrigationLogMapper;
import java.util.List;
import java.util.stream.Collectors;

public class IrrigationLogService extends BaseService<IrrigationLog, IrrigationLogDTO> {
    private static final IrrigationLogMapper mapper = IrrigationLogMapper.INSTANCE;
    private final IrrigationLogDAO irrDAO;

    public IrrigationLogService() {
        super(new IrrigationLogDAO());
        this.irrDAO = (IrrigationLogDAO) dao;
    }

    @Override protected IrrigationLogDTO toDTO(IrrigationLog e) { return mapper.toDTO(e); }
    @Override protected IrrigationLog toEntity(IrrigationLogDTO d) { return mapper.toEntity(d); }
    @Override protected void updateEntityFromDTO(IrrigationLogDTO d, IrrigationLog e) { /* Update logic */ }
    @Override protected Object getEntityId(IrrigationLogDTO d) { return d.getId(); }

    public List<Object[]> getMonthlyWaterUsage(Long lotId, int year) {
        return irrDAO.getMonthlyWaterUsage(lotId, year);
    }

    public List<Object[]> getTraceabilityLogs(String lotCode) {
        return irrDAO.getTraceabilityLogs(lotCode);
    }

    public List<IrrigationLogDTO> findByLot(Long lotId) {
        return irrDAO.findByLot(lotId).stream().map(this::toDTO).collect(Collectors.toList());
    }
}
