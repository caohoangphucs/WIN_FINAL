package com.example.winfinal.entity.core;

import com.example.winfinal.entity.production.ProductionLot;
import com.example.winfinal.entity.operation.WeatherLog;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "farm")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Farm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "farm_code", unique = true, length = 20)
    private String farmCode;

    @Column(length = 100)
    private String name;

    @Column(length = 255)
    private String address;

    @Column(name = "total_area")
    private Double totalArea;

    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Column(length = 20)
    private String phone;

    // Enabled Cascade Delete: Deleting a farm will delete all its departments
    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Department> departments = new ArrayList<>();

    // Enabled Cascade Delete: Deleting a farm will delete all its production lots
    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionLot> productionLots = new ArrayList<>();

    // Enabled Cascade Delete: Deleting a farm will delete all its weather logs
    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeatherLog> weatherLogs = new ArrayList<>();

    @Column(name = "created_at", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_at", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
}
