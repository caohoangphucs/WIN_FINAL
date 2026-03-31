package com.example.winfinal.controller;

import com.example.winfinal.dto.FarmDTO;
import com.example.winfinal.service.FarmService;
import java.util.List;

public class FarmController extends BaseController<FarmDTO> {
    public FarmController() {
        super(new FarmService());
    }

    public void createFarm(FarmDTO dto) { create(dto); }
    public void updateFarm(FarmDTO dto) { update(dto); }
    public void deleteFarm(Long id) { delete(id); }
    public List<FarmDTO> getAllFarms() { return getAll(); }
}
