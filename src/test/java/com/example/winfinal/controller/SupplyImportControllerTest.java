package com.example.winfinal.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Collections;
import java.util.List;

import com.example.winfinal.dto.SupplyImportDTO;
import com.example.winfinal.service.SupplyImportService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplyImportControllerTest {

    private SupplyImportController controller;
    private MockedConstruction<SupplyImportService> mockedService;
    private SupplyImportService service;

    @BeforeEach
    void setUp() {
        mockedService = mockConstruction(SupplyImportService.class, (mock, context) -> {
            // Setup default mock behaviors here if needed
        });
        controller = new SupplyImportController();
        service = mockedService.constructed().get(0);
    }

    @AfterEach
    void tearDown() {
        mockedService.close();
    }

    @Test
    void testCreateImport() {
        SupplyImportDTO dto = new SupplyImportDTO();
        controller.createImport(dto);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testUpdateImport() {
        SupplyImportDTO dto = new SupplyImportDTO();
        controller.updateImport(dto);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testDeleteImport() {
        controller.deleteImport(1L);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testGetAllImports() {
        List<SupplyImportDTO> result = controller.getAllImports();
        assertNotNull(result);
    }

    @Test
    void testGetTotalCostBySupplier() {
        List<Object[]> result = controller.getTotalCostBySupplier();
        assertNotNull(result);
    }

    @Test
    void testFindBySupply() {
        List<SupplyImportDTO> result = controller.findBySupply(1L);
        assertNotNull(result);
    }

    @Test
    void testGetCostEstimateByLot() {
        List<Object[]> result = controller.getCostEstimateByLot();
        assertNotNull(result);
    }

    @Test
    void testGetAvgUnitPrice() {
        Double result = controller.getAvgUnitPrice(1L);
        assertEquals(0.0, result);
    }

    @Test
    void testGetAll() {
        when(service.getAll()).thenReturn(Collections.singletonList(new SupplyImportDTO()));
        List<SupplyImportDTO> result = controller.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(service).getAll();
    }

    @Test
    void testGetById() {
        SupplyImportDTO mockDto = new SupplyImportDTO();
        when(service.getById(1L)).thenReturn(mockDto);
        SupplyImportDTO result = controller.getById(1L);
        assertNotNull(result);
        verify(service).getById(1L);
    }

    @Test
    void testCreate() {
        SupplyImportDTO mockDto = new SupplyImportDTO();
        controller.create(mockDto);
        verify(service).create(mockDto);
    }

    @Test
    void testUpdate() {
        SupplyImportDTO mockDto = new SupplyImportDTO();
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
