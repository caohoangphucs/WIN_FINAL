package com.example.winfinal.service;

import com.example.winfinal.dao.FarmDAO;
import com.example.winfinal.dto.FarmDTO;
import com.example.winfinal.entity.core.*;
import com.example.winfinal.mapper.FarmMapper;
import java.util.List;
import java.util.stream.Collectors;

public class FarmService {
    private final FarmDAO farmDAO = new FarmDAO();
    private final FarmMapper farmMapper = FarmMapper.INSTANCE;

    public void createFarm(FarmDTO dto) {
        Farm farm = farmMapper.toEntity(dto);
        farmDAO.save(farm);
    }

    public void updateFarm(FarmDTO dto) {
        Farm farm = farmDAO.findById(dto.getId());
        if (farm != null) {
            farmMapper.updateEntityFromDTO(dto, farm);
            farmDAO.update(farm);
        }
    }

    public void deleteFarm(Long id) {
        farmDAO.delete(id);
    }

    public List<FarmDTO> getAllFarms() {
        return farmDAO.findAll().stream()
                .map(farmMapper::toDTO)
                .collect(Collectors.toList());
    }
}
