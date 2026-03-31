package com.example.winfinal.controller;

import com.example.winfinal.dto.CultivationLogDTO;
import com.example.winfinal.service.CultivationLogService;
import java.util.List;

public class CultivationLogController extends BaseController<CultivationLogDTO> {
    public CultivationLogController() {
        super(new CultivationLogService());
    }

    public void createLog(CultivationLogDTO dto) { create(dto); }
    public void updateLog(CultivationLogDTO dto) { update(dto); }
    public void deleteLog(Long id) { delete(id); }
    public List<CultivationLogDTO> getAllLogs() { return getAll(); }
}
