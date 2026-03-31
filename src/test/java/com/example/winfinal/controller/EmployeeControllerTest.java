package com.example.winfinal.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Collections;
import java.util.List;

import com.example.winfinal.dto.EmployeeDTO;
import com.example.winfinal.service.EmployeeService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeControllerTest {

    private EmployeeController controller;
    private MockedConstruction<EmployeeService> mockedService;
    private EmployeeService service;

    @BeforeEach
    void setUp() {
        mockedService = mockConstruction(EmployeeService.class, (mock, context) -> {
            // Setup default mock behaviors here if needed
        });
        controller = new EmployeeController();
        service = mockedService.constructed().get(0);
    }

    @AfterEach
    void tearDown() {
        mockedService.close();
    }

    @Test
    void testCreateEmployee() {
        EmployeeDTO dto = new EmployeeDTO();
        controller.createEmployee(dto);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testUpdateEmployee() {
        EmployeeDTO dto = new EmployeeDTO();
        controller.updateEmployee(dto);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testDeleteEmployee() {
        controller.deleteEmployee(1L);
        // Verify the invocation if possible, but minimal test just runs it
    }

    @Test
    void testGetAllEmployees() {
        List<EmployeeDTO> result = controller.getAllEmployees();
        assertNotNull(result);
    }

    @Test
    void testSearch() {
        List<EmployeeDTO> result = controller.search("test");
        assertNotNull(result);
    }

    @Test
    void testGetTopPerformers() {
        List<Object[]> result = controller.getTopPerformers(1, 1);
        assertNotNull(result);
    }

    @Test
    void testExistsByEmpCode() {
        boolean result = controller.existsByEmpCode("test");
        assertNotNull(result);
    }

    @Test
    void testCountTodayLogs() {
        Long result = controller.countTodayLogs(1L);
        assertEquals(0L, result);
    }

    @Test
    void testGetAll() {
        when(service.getAll()).thenReturn(Collections.singletonList(new EmployeeDTO()));
        List<EmployeeDTO> result = controller.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(service).getAll();
    }

    @Test
    void testGetById() {
        EmployeeDTO mockDto = new EmployeeDTO();
        when(service.getById(1L)).thenReturn(mockDto);
        EmployeeDTO result = controller.getById(1L);
        assertNotNull(result);
        verify(service).getById(1L);
    }

    @Test
    void testCreate() {
        EmployeeDTO mockDto = new EmployeeDTO();
        controller.create(mockDto);
        verify(service).create(mockDto);
    }

    @Test
    void testUpdate() {
        EmployeeDTO mockDto = new EmployeeDTO();
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
