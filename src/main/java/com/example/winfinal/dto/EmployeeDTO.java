package com.example.winfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Long id;
    private String empCode;
    private String fullName;
    private String phone;
    private String email;
    private String roleCode;
    private Long departmentId;
}
