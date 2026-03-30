package com.example.winfinal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cultivation_log", indexes = {
    @Index(name = "idx_lot_date", columnList = "lot_id, applied_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CultivationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private ProductionLot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_id")
    private AgriSupply supply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_type_code")
    private ActivityType activityType;

    @Column(name = "applied_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date appliedAt;

    @Column(name = "dosage_used")
    private Double dosageUsed;
}
