package com.example.winfinal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "harvest_record", indexes = {
    @Index(name = "idx_harvest", columnList = "lot_id, harvest_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HarvestRecord {
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
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "harvest_date")
    @Temporal(TemporalType.DATE)
    private Date harvestDate;

    @Column(name = "yield_kg")
    private Double yieldKg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quality_grade_code")
    private QualityGrade qualityGrade;
}
