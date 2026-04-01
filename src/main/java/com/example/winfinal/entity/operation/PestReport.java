package com.example.winfinal.entity.operation;

import com.example.winfinal.entity.core.Employee;
import com.example.winfinal.entity.lookup.SeverityLevel;
import com.example.winfinal.entity.production.ProductionLot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "pest_report")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PestReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private ProductionLot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "severity_code")
    private SeverityLevel severity;

    @Column(name = "pest_name", length = 100)
    private String pestName;

    @Column(name = "treatment", length = 255)
    private String treatment;

    @Column(name = "damage_pct")
    private Double damagePct;

    @Column(name = "reported_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reportedAt;
}
