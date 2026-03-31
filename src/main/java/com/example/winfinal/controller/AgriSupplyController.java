package com.example.winfinal.controller;

import com.example.winfinal.dto.AgriSupplyDTO;
import com.example.winfinal.service.AgriSupplyService;
import java.util.List;

public class AgriSupplyController extends BaseController<AgriSupplyDTO> {
    public AgriSupplyController() {
        super(new AgriSupplyService());
    }

    public void createAgriSupply(AgriSupplyDTO dto) { create(dto); }
    public void updateAgriSupply(AgriSupplyDTO dto) { update(dto); }
    public void deleteAgriSupply(Long id) { delete(id); }
    public List<AgriSupplyDTO> getAllAgriSupplies() { return getAll(); }
}
