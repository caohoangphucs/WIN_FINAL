package com.example.winfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyImportDetailDTO {
    private Long id;
    private Long importId;
    private String importCode;
    private java.util.Date importDate;
    private Long supplyId;
    private String supplierName;
    private Double quantity;
    private BigDecimal unitPrice;
}
