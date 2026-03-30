package com.example.winfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgriSupplyDTO {
    private Long id;
    private String supplyCode;
    private String name;
    private Long categoryId;
    private String unit;
    private Double stockQty;
    private Double minStock;
    private Long supplierId;
}
