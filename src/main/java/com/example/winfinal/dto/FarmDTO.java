package com.example.winfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmDTO {
    private Long id;
    private String farmCode;
    private String name;
    private String address;
    private Double totalArea;
    private String ownerName;
    private String phone;
}
