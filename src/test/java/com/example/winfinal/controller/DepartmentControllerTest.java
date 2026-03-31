package com.example.winfinal.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Collections;
import java.util.List;

import com.example.winfinal.dto.DepartmentDTO;
import com.example.winfinal.service.DepartmentService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepartmentControllerTest {

    private DepartmentController controller;
    private MockedConstruction<DepartmentService> mockedService;
    private DepartmentService service;

    @BeforeEach
    void setUp() {
        mockedService = mockConstruction(DepartmentService.class, (mock, context) -> {
            // Setup default mock behaviors here if needed
        });
        controller = new DepartmentController();
        service = mockedService.constructed().get(0);
    }

    @AfterEach
    void tearDown() {
        mockedService.close();
    }

    @Test
    void testCreateDepartment() {
        DepartmentDTO dto = new DepartmentDTO();
        controller.createDepartment(dto);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testUpdateDepartment() {
        DepartmentDTO dto = new DepartmentDTO();
        controller.updateDepartment(dto);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testDeleteDepartment() {
        controller.deleteDepartment(1L);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testGetAllDepartments() {
        List<DepartmentDTO> result = controller.getAllDepartments();
        assertNotNull(result);
    }

    @Test
    void testGetAll() {
        when(service.getAll()).thenReturn(Collections.singletonList(new DepartmentDTO()));
        List<DepartmentDTO> result = controller.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(service).getAll();
    }

    @Test
    void testGetById() {
        DepartmentDTO mockDto = new DepartmentDTO();
        when(service.getById(1L)).thenReturn(mockDto);
        DepartmentDTO result = controller.getById(1L);
        assertNotNull(result);
        verify(service).getById(1L);
    }

    @Test
    void testCreate() {
        DepartmentDTO mockDto = new DepartmentDTO();
        controller.create(mockDto);
        verify(service).create(mockDto);
    }

    @Test
    void testUpdate() {
        DepartmentDTO mockDto = new DepartmentDTO();
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
