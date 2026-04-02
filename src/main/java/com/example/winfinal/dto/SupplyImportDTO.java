package com.example.winfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyImportDTO {
    private Long id;
    private String importCode;
    private Long supplierId;
    private String supplierName;
    private Long employeeId;
    private Date importDate;
    private List<SupplyImportDetailDTO> details;
}
