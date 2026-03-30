package com.example.winfinal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "severity_level")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeverityLevel {
    @Id
    @Column(length = 30)
    private String code;
}
