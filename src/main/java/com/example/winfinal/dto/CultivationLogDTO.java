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
    private Long supplyId;
    private Long employeeId;
    private String activityTypeCode;
    private Date appliedAt;
    private Double dosageUsed;
}
