package com.example.winfinal.service;

import com.example.winfinal.dao.FarmDAO;
import com.example.winfinal.dto.FarmDTO;
import com.example.winfinal.entity.core.Farm;
import com.example.winfinal.mapper.FarmMapper;
import java.util.List;

public class FarmService extends BaseService<Farm, FarmDTO> {
    private static final FarmMapper mapper = FarmMapper.INSTANCE;
    private final FarmDAO farmDAO;

    public FarmService() {
        super(new FarmDAO());
        this.farmDAO = (FarmDAO) dao;
    }

    @Override protected FarmDTO toDTO(Farm e) { return mapper.toDTO(e); }
    @Override protected Farm toEntity(FarmDTO d) { return mapper.toEntity(d); }
    @Override protected void updateEntityFromDTO(FarmDTO d, Farm e) { mapper.updateEntityFromDTO(d, e); }
    @Override protected Object getEntityId(FarmDTO d) { return d.getId(); }

    @Override
    protected void validate(FarmDTO dto) {
        super.validate(dto);
        if (dto.getFarmCode() == null || dto.getFarmCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã trang trại không được để trống");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên trang trại không được để trống");
        }
        if (dto.getTotalArea() != null && dto.getTotalArea() < 0) {
            throw new IllegalArgumentException("Diện tích (totalArea) không thể âm");
        }
    }

    // Backward compatibility aliases
    public void createFarm(FarmDTO dto) { create(dto); }
    public void updateFarm(FarmDTO dto) { update(dto); }
    public void deleteFarm(Long id) { delete(id); }
    public List<FarmDTO> getAllFarms() { return getAll(); }

    // [6.1] Tổng quan hoạt động theo mùa vụ của trang trại
    public List<Object[]> getSeasonalSummary(Long farmId) {
        return farmDAO.getSeasonalSummary(farmId);
    }
}
