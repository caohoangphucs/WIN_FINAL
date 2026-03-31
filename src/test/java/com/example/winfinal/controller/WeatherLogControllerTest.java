package com.example.winfinal.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Collections;
import java.util.List;

import com.example.winfinal.entity.operation.WeatherLog;
import com.example.winfinal.service.WeatherLogService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherLogControllerTest {

    private WeatherLogController controller;
    private MockedConstruction<WeatherLogService> mockedService;
    private WeatherLogService service;

    @BeforeEach
    void setUp() {
        mockedService = mockConstruction(WeatherLogService.class);
        controller = new WeatherLogController();
        service = mockedService.constructed().get(0);
    }

    @AfterEach
    void tearDown() {
        mockedService.close();
    }

    @Test
    void testCreateLog() {
        WeatherLog log = new WeatherLog();
        controller.createLog(log);
        verify(service).save(log);
    }

    @Test
    void testUpdateLog() {
        WeatherLog log = new WeatherLog();
        controller.updateLog(log);
        verify(service).update(log);
    }

    @Test
    void testDeleteLog() {
        controller.deleteLog(1L);
        verify(service).delete(1L);
    }

    @Test
    void testGetAllLogs() {
        when(service.getAll()).thenReturn(Collections.singletonList(new WeatherLog()));
        List<WeatherLog> res = controller.getAllLogs();
        assertNotNull(res);
        verify(service).getAll();
    }

    @Test
    void testGetById() {
        when(service.getById(1L)).thenReturn(new WeatherLog());
        WeatherLog res = controller.getById(1L);
        assertNotNull(res);
        verify(service).getById(1L);
    }

    @Test
    void testGetMonthlyRainStats() {
        controller.getMonthlyRainStats(1L, 2023);
        verify(service).getMonthlyRainStats(1L, 2023);
    }

    @Test
    void testFindByFarmAndYear() {
        controller.findByFarmAndYear(1L, 2023);
        verify(service).findByFarmAndYear(1L, 2023);
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
        List<?> result = controller.getAllLogs();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(service).getAll();
    }

    @Test
    void testCreate_WhenNullEntity_ThrowsException() {
        doThrow(new IllegalArgumentException("Entity cannot be null")).when(service).save(null);
        assertThrows(IllegalArgumentException.class, () -> controller.createLog(null));
        verify(service).save(null);
    }
    
    @Test
    void testDelete_WhenIdNotExists_ThrowsException() {
        doThrow(new RuntimeException("ID not exists")).when(service).delete(999L);
        assertThrows(RuntimeException.class, () -> controller.deleteLog(999L));
        verify(service).delete(999L);
    }
}
