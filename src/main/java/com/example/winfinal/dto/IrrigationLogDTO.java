package com.example.winfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IrrigationLogDTO {
    private Long id;
    private Long lotId;
    private Long employeeId;
    private Date irrigatedAt;
    private Double waterAmount;
    private String source;
    private Integer durationMin;
}
