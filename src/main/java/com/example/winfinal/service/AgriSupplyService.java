package com.example.winfinal.service;

import com.example.winfinal.dao.AgriSupplyDAO;
import com.example.winfinal.dto.AgriSupplyDTO;
import com.example.winfinal.entity.inventory.AgriSupply;
import com.example.winfinal.mapper.AgriSupplyMapper;
import java.util.List;
import java.util.stream.Collectors;

public class AgriSupplyService extends BaseService<AgriSupply, AgriSupplyDTO> {
    private static final AgriSupplyMapper mapper = AgriSupplyMapper.INSTANCE;
    private final AgriSupplyDAO supplyDAO;

    public AgriSupplyService() {
        super(new AgriSupplyDAO());
        this.supplyDAO = (AgriSupplyDAO) dao;
    }

    @Override protected AgriSupplyDTO toDTO(AgriSupply e) { return mapper.toDTO(e); }
    @Override protected AgriSupply toEntity(AgriSupplyDTO d) { return mapper.toEntity(d); }
    @Override protected void updateEntityFromDTO(AgriSupplyDTO d, AgriSupply e) { mapper.updateEntityFromDTO(d, e); }
    @Override protected Object getEntityId(AgriSupplyDTO d) { return d.getId(); }

    // [1.5] Tìm vật tư theo từ khóa (tên hoặc mã)
    public List<AgriSupplyDTO> search(String keyword) {
        return supplyDAO.search(keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // [3.1] Danh sách vật tư sắp hết hàng
    public List<AgriSupplyDTO> getLowStockSupplies() {
        return supplyDAO.findLowStock().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // [7.3] Kiểm tra mã vật tư đã tồn tại chưa
    public boolean existsBySupplyCode(String supplyCode) {
        return supplyDAO.existsBySupplyCode(supplyCode);
    }

    // [7.5] Lấy tồn kho hiện tại của một vật tư
    public Double getStockQty(Long supplyId) {
        return supplyDAO.getStockQty(supplyId);
    }
}
