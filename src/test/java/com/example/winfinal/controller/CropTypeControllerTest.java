package com.example.winfinal.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Collections;
import java.util.List;

import com.example.winfinal.dto.CropTypeDTO;
import com.example.winfinal.service.CropTypeService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CropTypeControllerTest {

    private CropTypeController controller;
    private MockedConstruction<CropTypeService> mockedService;
    private CropTypeService service;

    @BeforeEach
    void setUp() {
        mockedService = mockConstruction(CropTypeService.class, (mock, context) -> {
            // Setup default mock behaviors here if needed
        });
        controller = new CropTypeController();
        service = mockedService.constructed().get(0);
    }

    @AfterEach
    void tearDown() {
        mockedService.close();
    }

    @Test
    void testCreateCropType() {
        CropTypeDTO dto = new CropTypeDTO();
        controller.createCropType(dto);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testUpdateCropType() {
        CropTypeDTO dto = new CropTypeDTO();
        controller.updateCropType(dto);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testDeleteCropType() {
        controller.deleteCropType(1L);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testGetAllCropTypes() {
        List<CropTypeDTO> result = controller.getAllCropTypes();
        assertNotNull(result);
    }

    @Test
    void testGetAll() {
        when(service.getAll()).thenReturn(Collections.singletonList(new CropTypeDTO()));
        List<CropTypeDTO> result = controller.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(service).getAll();
    }

    @Test
    void testGetById() {
        CropTypeDTO mockDto = new CropTypeDTO();
        when(service.getById(1L)).thenReturn(mockDto);
        CropTypeDTO result = controller.getById(1L);
        assertNotNull(result);
        verify(service).getById(1L);
    }

    @Test
    void testCreate() {
        CropTypeDTO mockDto = new CropTypeDTO();
        controller.create(mockDto);
        verify(service).create(mockDto);
    }

    @Test
    void testUpdate() {
        CropTypeDTO mockDto = new CropTypeDTO();
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
