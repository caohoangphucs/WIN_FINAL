package com.example.winfinal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "crop_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crop_code", unique = true, length = 20)
    private String cropCode;

    @Column(length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CropCategory category;

    @Column(name = "growth_days")
    private Integer growthDays;
}
