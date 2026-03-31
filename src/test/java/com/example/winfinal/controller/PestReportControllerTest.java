package com.example.winfinal.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Collections;
import java.util.List;

import com.example.winfinal.entity.operation.PestReport;
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
        PestReport report = new PestReport();
        controller.createReport(report);
        verify(service).save(report);
    }

    @Test
    void testUpdateReport() {
        PestReport report = new PestReport();
        controller.updateReport(report);
        verify(service).update(report);
    }

    @Test
    void testDeleteReport() {
        controller.deleteReport(1L);
        verify(service).delete(1L);
    }

    @Test
    void testGetAllReports() {
        when(service.getAll()).thenReturn(Collections.singletonList(new PestReport()));
        List<PestReport> res = controller.getAllReports();
        assertNotNull(res);
        verify(service).getAll();
    }

    @Test
    void testGetById() {
        when(service.getById(1L)).thenReturn(new PestReport());
        PestReport res = controller.getById(1L);
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
        List<?> result = controller.getAllReports();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(service).getAll();
    }

    @Test
    void testCreate_WhenNullEntity_ThrowsException() {
        doThrow(new IllegalArgumentException("Entity cannot be null")).when(service).save(null);
        assertThrows(IllegalArgumentException.class, () -> controller.createReport(null));
        verify(service).save(null);
    }
    
    @Test
    void testDelete_WhenIdNotExists_ThrowsException() {
        doThrow(new RuntimeException("ID not exists")).when(service).delete(999L);
        assertThrows(RuntimeException.class, () -> controller.deleteReport(999L));
        verify(service).delete(999L);
    }
}
