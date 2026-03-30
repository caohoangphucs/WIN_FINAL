package com.example.winfinal.service;

import com.example.winfinal.dao.AgriSupplyDAO;
import com.example.winfinal.dao.SupplyImportDAO;
import com.example.winfinal.dao.SupplyImportDetailDAO;
import com.example.winfinal.dto.SupplyImportDTO;
import com.example.winfinal.dto.SupplyImportDetailDTO;
import com.example.winfinal.entity.inventory.AgriSupply;
import com.example.winfinal.entity.inventory.SupplyImport;
import com.example.winfinal.entity.inventory.SupplyImportDetail;
import com.example.winfinal.mapper.SupplyImportDetailMapper;
import com.example.winfinal.mapper.SupplyImportMapper;

import java.util.List;
import java.util.stream.Collectors;

public class SupplyImportService extends BaseService<SupplyImport, SupplyImportDTO> {
    private static final SupplyImportMapper mapper = SupplyImportMapper.INSTANCE;
    private static final SupplyImportDetailMapper detailMapper = SupplyImportDetailMapper.INSTANCE;
    
    private final SupplyImportDetailDAO detailDAO = new SupplyImportDetailDAO();
    private final AgriSupplyDAO supplyDAO = new AgriSupplyDAO();

    public SupplyImportService() {
        super(new SupplyImportDAO());
    }

    /**
     * Create a new import and update stock levels
     */
    @Override
    public void create(SupplyImportDTO dto) {
        SupplyImport entity = toEntity(dto);
        dao.save(entity); // Save the main import
        
        if (dto.getDetails() != null) {
            for (SupplyImportDetailDTO dDTO : dto.getDetails()) {
                dDTO.setImportId(entity.getId());
                SupplyImportDetail detail = detailMapper.toEntity(dDTO);
                detailDAO.save(detail);

                // Update stock for each supply
                AgriSupply supply = supplyDAO.findById(dDTO.getSupplyId());
                if (supply != null) {
                    double currentStock = (supply.getStockQty() != null) ? supply.getStockQty() : 0.0;
                    supply.setStockQty(currentStock + dDTO.getQuantity());
                    supplyDAO.update(supply);
                }
            }
        }
    }

    @Override
    protected SupplyImportDTO toDTO(SupplyImport entity) {
        SupplyImportDTO dto = mapper.toDTO(entity);
        // Load details manually if needed or via JPA mapping if provided
        return dto;
    }

    @Override
    protected SupplyImport toEntity(SupplyImportDTO dto) {
        return mapper.toEntity(dto);
    }

    @Override
    protected void updateEntityFromDTO(SupplyImportDTO dto, SupplyImport entity) {
        mapper.updateEntityFromDTO(dto, entity);
    }

    @Override
    protected Object getEntityId(SupplyImportDTO dto) {
        return dto.getId();
    }
}
