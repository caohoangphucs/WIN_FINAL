package com.example.winfinal.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Collections;
import java.util.List;

import com.example.winfinal.dto.IrrigationLogDTO;
import com.example.winfinal.service.IrrigationLogService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IrrigationLogControllerTest {

    private IrrigationLogController controller;
    private MockedConstruction<IrrigationLogService> mockedService;
    private IrrigationLogService service;

    @BeforeEach
    void setUp() {
        mockedService = mockConstruction(IrrigationLogService.class);
        controller = new IrrigationLogController();
        service = mockedService.constructed().get(0);
    }

    @AfterEach
    void tearDown() {
        mockedService.close();
    }

    @Test
    void testCreateLog() {
        IrrigationLogDTO dto = new IrrigationLogDTO();
        controller.createLog(dto);
        verify(service).create(dto);
    }

    @Test
    void testUpdateLog() {
        IrrigationLogDTO dto = new IrrigationLogDTO();
        controller.updateLog(dto);
        verify(service).update(dto);
    }

    @Test
    void testDeleteLog() {
        controller.deleteLog(1L);
        verify(service).delete(1L);
    }

    @Test
    void testGetAllLogs() {
        when(service.getAll()).thenReturn(Collections.singletonList(new IrrigationLogDTO()));
        List<IrrigationLogDTO> res = controller.getAllLogs();
        assertNotNull(res);
        verify(service).getAll();
    }

    @Test
    void testGetById() {
        when(service.getById(1L)).thenReturn(new IrrigationLogDTO());
        IrrigationLogDTO res = controller.getById(1L);
        assertNotNull(res);
        verify(service).getById(1L);
    }

    @Test
    void testGetMonthlyWaterUsage() {
        controller.getMonthlyWaterUsage(1L, 2023);
        verify(service).getMonthlyWaterUsage(1L, 2023);
    }

    @Test
    void testFindByLot() {
        controller.findByLot(1L);
        verify(service).findByLot(1L);
    }

    @Test
    void testGetTraceabilityLogs() {
        controller.getTraceabilityLogs("code");
        verify(service).getTraceabilityLogs("code");
    }

    // ── Exception Handling & Edge Cases ────────────────

    @Test
    void testGetById_WhenServiceThrowsException() {
        when(service.getById(999L)).thenThrow(new RuntimeException("Not found"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.getById(999L));
        assertEquals("Not found", ex.getMessage());
        verify(service).getById(999L);
    }

    @Test
    void testGetAll_WhenEmpty_ReturnsEmptyList() {
        when(service.getAll()).thenReturn(Collections.emptyList());
        List<?> result = controller.getAllLogs();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(service).getAll();
    }

    @Test
    void testCreate_WhenNullEntity_ThrowsException() {
        doThrow(new IllegalArgumentException("Entity cannot be null")).when(service).create(null);
        assertThrows(IllegalArgumentException.class, () -> controller.createLog(null));
        verify(service).create(null);
    }
    
    @Test
    void testDelete_WhenIdNotExists_ThrowsException() {
        doThrow(new RuntimeException("ID not exists")).when(service).delete(999L);
        assertThrows(RuntimeException.class, () -> controller.deleteLog(999L));
        verify(service).delete(999L);
    }
}
