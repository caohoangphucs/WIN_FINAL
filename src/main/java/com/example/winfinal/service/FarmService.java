package com.example.winfinal.service;

import com.example.winfinal.dao.FarmDAO;
import com.example.winfinal.dto.FarmDTO;
import com.example.winfinal.entity.core.*;
import com.example.winfinal.entity.lookup.*;
import com.example.winfinal.entity.master.*;
import com.example.winfinal.entity.production.*;
import com.example.winfinal.entity.inventory.*;
import com.example.winfinal.entity.operation.*;
import com.example.winfinal.entity.output.*;
import java.util.List;
import java.util.stream.Collectors;

public class FarmService {
    private final FarmDAO farmDAO = new FarmDAO();

    public void createFarm(FarmDTO dto) {
        Farm farm = new Farm();
        updateEntityFromDTO(farm, dto);
        farmDAO.save(farm);
    }

    public void updateFarm(FarmDTO dto) {
        Farm farm = farmDAO.findById(dto.getId());
        if (farm != null) {
            updateEntityFromDTO(farm, dto);
            farmDAO.update(farm);
        }
    }

    public void deleteFarm(Long id) {
        farmDAO.delete(id);
    }

    private void updateEntityFromDTO(Farm farm, FarmDTO dto) {
        farm.setFarmCode(dto.getFarmCode());
        farm.setName(dto.getName());
        farm.setAddress(dto.getAddress());
        farm.setTotalArea(dto.getTotalArea());
        farm.setOwnerName(dto.getOwnerName());
        farm.setPhone(dto.getPhone());
    }

    public List<FarmDTO> getAllFarms() {
        return farmDAO.findAll().stream().map(farm -> {
            FarmDTO dto = new FarmDTO();
            dto.setId(farm.getId());
            dto.setFarmCode(farm.getFarmCode());
            dto.setName(farm.getName());
            dto.setAddress(farm.getAddress());
            dto.setTotalArea(farm.getTotalArea());
            dto.setOwnerName(farm.getOwnerName());
            dto.setPhone(farm.getPhone());
            return dto;
        }).collect(Collectors.toList());
    }
}
