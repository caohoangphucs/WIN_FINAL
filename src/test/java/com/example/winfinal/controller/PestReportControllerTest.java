package com.example.winfinal.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Collections;
import java.util.List;

import com.example.winfinal.dto.PestReportDTO;
import com.example.winfinal.service.PestReportService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PestReportControllerTest {

    private PestReportController controller;
    private MockedConstruction<PestReportService> mockedService;
    private PestReportService service;

    @BeforeEach
    void setUp() {
        mockedService = mockConstruction(PestReportService.class);
        controller = new PestReportController();
        service = mockedService.constructed().get(0);
    }

    @AfterEach
    void tearDown() {
        mockedService.close();
    }

    @Test
    void testCreateReport() {
        PestReportDTO dto = new PestReportDTO();
        controller.createReport(dto);
        verify(service).create(dto);
    }

    @Test
    void testUpdateReport() {
        PestReportDTO dto = new PestReportDTO();
        controller.updateReport(dto);
        verify(service).update(dto);
    }

    @Test
    void testDeleteReport() {
        controller.deleteReport(1L);
        verify(service).delete(1L);
    }

    @Test
    void testGetAllReports() {
        when(service.getAll()).thenReturn(Collections.singletonList(new PestReportDTO()));
        List<PestReportDTO> res = controller.getAllReports();
        assertNotNull(res);
        verify(service).getAll();
    }

    @Test
    void testGetById() {
        when(service.getById(1L)).thenReturn(new PestReportDTO());
        PestReportDTO res = controller.getById(1L);
        assertNotNull(res);
        verify(service).getById(1L);
    }

    @Test
    void testGetHighSeverityReports() {
        controller.getHighSeverityReports();
        verify(service).getHighSeverityReports();
    }

    @Test
    void testFindByLot() {
        controller.findByLot(1L);
        verify(service).findByLot(1L);
    }

    @Test
    void testGetSeverityStatsBySeason() {
        controller.getSeverityStatsBySeason(1L);
        verify(service).getSeverityStatsBySeason(1L);
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
        List<?> result = controller.getAllReports();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(service).getAll();
    }

    @Test
    void testCreate_WhenNullEntity_ThrowsException() {
        doThrow(new IllegalArgumentException("Entity cannot be null")).when(service).create(null);
        assertThrows(IllegalArgumentException.class, () -> controller.createReport(null));
        verify(service).create(null);
    }
    
    @Test
    void testDelete_WhenIdNotExists_ThrowsException() {
        doThrow(new RuntimeException("ID not exists")).when(service).delete(999L);
        assertThrows(RuntimeException.class, () -> controller.deleteReport(999L));
        verify(service).delete(999L);
    }
}
