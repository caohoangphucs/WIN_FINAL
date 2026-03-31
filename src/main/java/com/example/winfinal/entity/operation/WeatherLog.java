package com.example.winfinal.entity.operation;

import com.example.winfinal.entity.core.Farm;
import com.example.winfinal.entity.production.ProductionLot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "weather_log", indexes = {
    @Index(name = "idx_weather", columnList = "farm_id, weather_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id")
    private Farm farm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private ProductionLot lot;

    @Column(name = "weather_date")
    @Temporal(TemporalType.DATE)
    private Date weatherDate;

    private Double temperature;

    @Column(name = "rainfall_mm")
    private Double rainfallMm;
}
