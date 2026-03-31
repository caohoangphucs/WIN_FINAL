package com.example.winfinal.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Collections;
import java.util.List;

import com.example.winfinal.dto.ProductionLotDTO;
import com.example.winfinal.service.ProductionLotService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductionLotControllerTest {

    private ProductionLotController controller;
    private MockedConstruction<ProductionLotService> mockedService;
    private ProductionLotService service;

    @BeforeEach
    void setUp() {
        mockedService = mockConstruction(ProductionLotService.class, (mock, context) -> {
            // Setup default mock behaviors here if needed
        });
        controller = new ProductionLotController();
        service = mockedService.constructed().get(0);
    }

    @AfterEach
    void tearDown() {
        mockedService.close();
    }

    @Test
    void testCreateLot() {
        ProductionLotDTO dto = new ProductionLotDTO();
        controller.createLot(dto);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testUpdateLot() {
        ProductionLotDTO dto = new ProductionLotDTO();
        controller.updateLot(dto);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testDeleteLot() {
        controller.deleteLot(1L);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testGetAllLots() {
        List<ProductionLotDTO> result = controller.getAllLots();
        assertNotNull(result);
    }

    @Test
    void testFindByLotCode() {
        ProductionLotDTO result = controller.findByLotCode("test");
        assertNull(result); // Default mock returns null or empty
    }

    @Test
    void testFindByStatus() {
        List<ProductionLotDTO> result = controller.findByStatus("test");
        assertNotNull(result);
    }

    @Test
    void testFindByFarm() {
        List<ProductionLotDTO> result = controller.findByFarm(1L);
        assertNotNull(result);
    }

    @Test
    void testSearch() {
        List<ProductionLotDTO> result = controller.search(1L, "test", 1L, 1L);
        assertNotNull(result);
    }

    @Test
    void testGetUpcomingHarvest() {
        List<ProductionLotDTO> result = controller.getUpcomingHarvest(null);
        assertNotNull(result);
    }

    @Test
    void testGetFullTraceabilityInfo() {
        ProductionLotDTO result = controller.getFullTraceabilityInfo("test");
        assertNull(result); // Default mock returns null or empty
    }

    @Test
    void testGetTopYieldingLots() {
        List<Object[]> result = controller.getTopYieldingLots(1);
        assertNotNull(result);
    }

    @Test
    void testExistsByLotCode() {
        boolean result = controller.existsByLotCode("test");
        assertNotNull(result);
    }

    @Test
    void testGetAll() {
        when(service.getAll()).thenReturn(Collections.singletonList(new ProductionLotDTO()));
        List<ProductionLotDTO> result = controller.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(service).getAll();
    }

    @Test
    void testGetById() {
        ProductionLotDTO mockDto = new ProductionLotDTO();
        when(service.getById(1L)).thenReturn(mockDto);
        ProductionLotDTO result = controller.getById(1L);
        assertNotNull(result);
        verify(service).getById(1L);
    }

    @Test
    void testCreate() {
        ProductionLotDTO mockDto = new ProductionLotDTO();
        controller.create(mockDto);
        verify(service).create(mockDto);
    }

    @Test
    void testUpdate() {
        ProductionLotDTO mockDto = new ProductionLotDTO();
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
