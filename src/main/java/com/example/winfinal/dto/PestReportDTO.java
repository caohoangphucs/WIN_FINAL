package com.example.winfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PestReportDTO {
    private Long id;
    private Long lotId;
    private String lotCode;
    private Long employeeId;
    private String employeeName;
    private String severityCode;
    private String pestName;
    private String treatment;
    private Double damagePct;
    private Date reportedAt;
}
