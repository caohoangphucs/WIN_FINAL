package com.example.winfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CultivationLogDTO {
    private Long id;
    private Long lotId;
    private String lotCode;
    private Long supplyId;
    private String supplyName;
    private String supplyUnit;
    private Long employeeId;
    private String employeeFullName;
    private String activityTypeCode;
    private Date appliedAt;
    private Double dosageUsed;
}
