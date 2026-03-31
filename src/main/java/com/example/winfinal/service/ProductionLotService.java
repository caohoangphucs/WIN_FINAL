package com.example.winfinal.service;

import com.example.winfinal.dao.ProductionLotDAO;
import com.example.winfinal.dto.ProductionLotDTO;
import com.example.winfinal.entity.production.ProductionLot;
import com.example.winfinal.mapper.ProductionLotMapper;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ProductionLotService extends BaseService<ProductionLot, ProductionLotDTO> {
    private static final ProductionLotMapper mapper = ProductionLotMapper.INSTANCE;
    private final ProductionLotDAO lotDAO;

    public ProductionLotService() {
        super(new ProductionLotDAO());
        this.lotDAO = (ProductionLotDAO) dao;
    }

    @Override protected ProductionLotDTO toDTO(ProductionLot e) { return mapper.toDTO(e); }
    @Override protected ProductionLot toEntity(ProductionLotDTO d) { return mapper.toEntity(d); }
    @Override protected void updateEntityFromDTO(ProductionLotDTO d, ProductionLot e) { mapper.updateEntityFromDTO(d, e); }
    @Override protected Object getEntityId(ProductionLotDTO d) { return d.getId(); }

    // [1.2] Tìm theo mã lô
    public ProductionLotDTO findByLotCode(String lotCode) {
        ProductionLot e = lotDAO.findByLotCode(lotCode);
        return e != null ? toDTO(e) : null;
    }

    // [1.3] Lọc theo trạng thái
    public List<ProductionLotDTO> findByStatus(String statusCode) {
        return lotDAO.findByStatus(statusCode).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // [1.4] Lọc theo trang trại
    public List<ProductionLotDTO> findByFarm(Long farmId) {
        return lotDAO.findByFarm(farmId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // [1.8] Tìm kiếm đa điều kiện
    public List<ProductionLotDTO> search(Long farmId, String statusCode, Long cropTypeId, Long seasonId) {
        return lotDAO.searchByConditions(farmId, statusCode, cropTypeId, seasonId)
                     .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // [2.6] Lô sắp đến ngày thu hoạch
    public List<ProductionLotDTO> getUpcomingHarvest(Date nextDate) {
        return lotDAO.findUpcomingHarvest(nextDate).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // [5.1] Thông tin đầy đủ phục vụ truy xuất nguồn gốc
    public ProductionLotDTO getFullTraceabilityInfo(String lotCode) {
        ProductionLot e = lotDAO.getFullTraceabilityInfo(lotCode);
        return e != null ? toDTO(e) : null;
    }

    // [6.2] Top N lô năng suất cao nhất
    public List<Object[]> getTopYieldingLots(int limit) {
        return lotDAO.getTopYieldingLots(limit);
    }

    // [7.1] Kiểm tra mã lô tồn tại
    public boolean existsByLotCode(String lotCode) {
        return lotDAO.existsByLotCode(lotCode);
    }
}
