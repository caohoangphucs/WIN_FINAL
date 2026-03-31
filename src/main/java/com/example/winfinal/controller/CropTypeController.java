package com.example.winfinal.controller;

import com.example.winfinal.dto.CropTypeDTO;
import com.example.winfinal.service.CropTypeService;
import java.util.List;

public class CropTypeController extends BaseController<CropTypeDTO> {
    public CropTypeController() {
        super(new CropTypeService());
    }

    public void createCropType(CropTypeDTO dto) { create(dto); }
    public void updateCropType(CropTypeDTO dto) { update(dto); }
    public void deleteCropType(Long id) { delete(id); }
    public List<CropTypeDTO> getAllCropTypes() { return getAll(); }
}
