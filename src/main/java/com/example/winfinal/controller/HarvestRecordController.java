package com.example.winfinal.controller;

import com.example.winfinal.dto.HarvestRecordDTO;
import com.example.winfinal.service.HarvestRecordService;
import java.util.List;

public class HarvestRecordController extends BaseController<HarvestRecordDTO> {
    public HarvestRecordController() {
        super(new HarvestRecordService());
    }

    public void createRecord(HarvestRecordDTO dto) { create(dto); }
    public void updateRecord(HarvestRecordDTO dto) { update(dto); }
    public void deleteRecord(Long id) { delete(id); }
    public List<HarvestRecordDTO> getAllRecords() { return getAll(); }
}
