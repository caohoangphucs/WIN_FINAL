package com.example.winfinal.controller;

import com.example.winfinal.dto.ProductionLotDTO;
import com.example.winfinal.service.ProductionLotService;
import java.util.List;

public class ProductionLotController extends BaseController<ProductionLotDTO> {
    public ProductionLotController() {
        super(new ProductionLotService());
    }

    public void createLot(ProductionLotDTO dto) { create(dto); }
    public void updateLot(ProductionLotDTO dto) { update(dto); }
    public void deleteLot(Long id) { delete(id); }
    public List<ProductionLotDTO> getAllLots() { return getAll(); }
}
