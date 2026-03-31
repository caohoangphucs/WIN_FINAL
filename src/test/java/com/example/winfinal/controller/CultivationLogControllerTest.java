package com.example.winfinal.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Collections;
import java.util.List;

import com.example.winfinal.dto.CultivationLogDTO;
import com.example.winfinal.service.CultivationLogService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CultivationLogControllerTest {

    private CultivationLogController controller;
    private MockedConstruction<CultivationLogService> mockedService;
    private CultivationLogService service;

    @BeforeEach
    void setUp() {
        mockedService = mockConstruction(CultivationLogService.class, (mock, context) -> {
            // Setup default mock behaviors here if needed
        });
        controller = new CultivationLogController();
        service = mockedService.constructed().get(0);
    }

    @AfterEach
    void tearDown() {
        mockedService.close();
    }

    @Test
    void testCreateLog() {
        CultivationLogDTO dto = new CultivationLogDTO();
        controller.createLog(dto);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testUpdateLog() {
        CultivationLogDTO dto = new CultivationLogDTO();
        controller.updateLog(dto);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testDeleteLog() {
        controller.deleteLog(1L);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testGetAllLogs() {
        List<CultivationLogDTO> result = controller.getAllLogs();
        assertNotNull(result);
    }

    @Test
    void testGetMaterialConsumptionByType() {
        List<Object[]> result = controller.getMaterialConsumptionByType();
        assertNotNull(result);
    }

    @Test
    void testGetUsageByLot() {
        List<Object[]> result = controller.getUsageByLot(1L);
        assertNotNull(result);
    }

    @Test
    void testGetDetailedActivityByLot() {
        List<Object[]> result = controller.getDetailedActivityByLot(1L);
        assertNotNull(result);
    }

    @Test
    void testFindByLotAndDateRange() {
        List<CultivationLogDTO> result = controller.findByLotAndDateRange(1L, null, null);
        assertNotNull(result);
    }

    @Test
    void testGetActivityStatsByLot() {
        List<Object[]> result = controller.getActivityStatsByLot(1L);
        assertNotNull(result);
    }

    @Test
    void testGetTraceabilityLogs() {
        List<Object[]> result = controller.getTraceabilityLogs("test");
        assertNotNull(result);
    }

    @Test
    void testGetAll() {
        when(service.getAll()).thenReturn(Collections.singletonList(new CultivationLogDTO()));
        List<CultivationLogDTO> result = controller.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(service).getAll();
    }

    @Test
    void testGetById() {
        CultivationLogDTO mockDto = new CultivationLogDTO();
        when(service.getById(1L)).thenReturn(mockDto);
        CultivationLogDTO result = controller.getById(1L);
        assertNotNull(result);
        verify(service).getById(1L);
    }

    @Test
    void testCreate() {
        CultivationLogDTO mockDto = new CultivationLogDTO();
        controller.create(mockDto);
        verify(service).create(mockDto);
    }

    @Test
    void testUpdate() {
        CultivationLogDTO mockDto = new CultivationLogDTO();
        controller.update(mockDto);
        verify(service).update(mockDto);
    }

    @Test
    void testDelete() {
        controller.delete(1L);
        verify(service).delete(1L);
    }


    // ── Nâng cao: Exception Handling & Edge Cases ────────────────

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
        List<?> result = controller.getAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(service).getAll();
    }

    @Test
    void testCreate_WhenNullDto_ThrowsException() {
        doThrow(new IllegalArgumentException("DTO cannot be null")).when(service).create(null);
        assertThrows(IllegalArgumentException.class, () -> controller.create(null));
        verify(service).create(null);
    }
    
    @Test
    void testDelete_WhenIdNotExists_ThrowsException() {
        doThrow(new RuntimeException("ID not exists")).when(service).delete(999L);
        assertThrows(RuntimeException.class, () -> controller.delete(999L));
        verify(service).delete(999L);
    }
}
