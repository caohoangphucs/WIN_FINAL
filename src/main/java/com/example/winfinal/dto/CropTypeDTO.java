package com.example.winfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropTypeDTO {
    private Long id;
    private String cropCode;
    private String name;
    private Long categoryId;
    private Integer growthDays;
}
