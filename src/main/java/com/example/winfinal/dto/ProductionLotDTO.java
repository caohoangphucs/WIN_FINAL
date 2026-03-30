package com.example.winfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionLotDTO {
    private Long id;
    private String lotCode;
    private Double areaM2;
    private String statusCode;
    private String locationDesc;
    private Date plantDate;
    private Date expectedHarvestDate;
    private Long farmId;
    private Long cropTypeId;
    private Long managerId;
}
