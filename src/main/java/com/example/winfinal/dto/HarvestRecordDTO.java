package com.example.winfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HarvestRecordDTO {
    private Long id;
    private Long lotId;
    private Long employeeId;
    private Long customerId;
    private Date harvestDate;
    private Double yieldKg;
    private String qualityGradeCode;
}
