package com.example.winfinal.entity.operation;

import com.example.winfinal.entity.core.Employee;
import com.example.winfinal.entity.production.ProductionLot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "irrigation_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IrrigationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private ProductionLot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "irrigated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date irrigatedAt;
}
