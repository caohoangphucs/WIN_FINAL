package com.example.winfinal.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Collections;
import java.util.List;

import com.example.winfinal.dto.HarvestRecordDTO;
import com.example.winfinal.service.HarvestRecordService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HarvestRecordControllerTest {

    private HarvestRecordController controller;
    private MockedConstruction<HarvestRecordService> mockedService;
    private HarvestRecordService service;

    @BeforeEach
    void setUp() {
        mockedService = mockConstruction(HarvestRecordService.class, (mock, context) -> {
            // Setup default mock behaviors here if needed
        });
        controller = new HarvestRecordController();
        service = mockedService.constructed().get(0);
    }

    @AfterEach
    void tearDown() {
        mockedService.close();
    }

    @Test
    void testCreateHarvestRecord() {
        HarvestRecordDTO dto = new HarvestRecordDTO();
        controller.createHarvestRecord(dto);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testUpdateHarvestRecord() {
        HarvestRecordDTO dto = new HarvestRecordDTO();
        controller.updateHarvestRecord(dto);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testDeleteHarvestRecord() {
        controller.deleteHarvestRecord(1L);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testGetAllHarvestRecords() {
        List<HarvestRecordDTO> result = controller.getAllHarvestRecords();
        assertNotNull(result);
    }

    @Test
    void testGetAvgYieldByCropType() {
        List<Object[]> result = controller.getAvgYieldByCropType();
        assertNotNull(result);
    }

    @Test
    void testGetYieldBySeason() {
        List<Object[]> result = controller.getYieldBySeason();
        assertNotNull(result);
    }

    @Test
    void testGetYieldPerM2ByLot() {
        List<Object[]> result = controller.getYieldPerM2ByLot();
        assertNotNull(result);
    }

    @Test
    void testGetQualityGradeStats() {
        List<Object[]> result = controller.getQualityGradeStats();
        assertNotNull(result);
    }

    @Test
    void testGetYieldByFarm() {
        List<Object[]> result = controller.getYieldByFarm();
        assertNotNull(result);
    }

    @Test
    void testGetCustomerYieldStats() {
        List<Object[]> result = controller.getCustomerYieldStats();
        assertNotNull(result);
    }

    @Test
    void testFindByLotCode() {
        List<HarvestRecordDTO> result = controller.findByLotCode("test");
        assertNotNull(result);
    }

    @Test
    void testGetAll() {
        when(service.getAll()).thenReturn(Collections.singletonList(new HarvestRecordDTO()));
        List<HarvestRecordDTO> result = controller.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(service).getAll();
    }

    @Test
    void testGetById() {
        HarvestRecordDTO mockDto = new HarvestRecordDTO();
        when(service.getById(1L)).thenReturn(mockDto);
        HarvestRecordDTO result = controller.getById(1L);
        assertNotNull(result);
        verify(service).getById(1L);
    }

    @Test
    void testCreate() {
        HarvestRecordDTO mockDto = new HarvestRecordDTO();
        controller.create(mockDto);
        verify(service).create(mockDto);
    }

    @Test
    void testUpdate() {
        HarvestRecordDTO mockDto = new HarvestRecordDTO();
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
