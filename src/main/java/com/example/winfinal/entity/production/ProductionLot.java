package com.example.winfinal.entity.production;

import com.example.winfinal.entity.core.*;
import com.example.winfinal.entity.lookup.LotStatus;
import com.example.winfinal.entity.master.CropType;
import com.example.winfinal.entity.master.Season;
import com.example.winfinal.entity.operation.*;
import com.example.winfinal.entity.output.HarvestRecord;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "production_lot")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lot_code", unique = true, length = 20)
    private String lotCode;

    @Column(name = "area_m2")
    private Double areaM2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code")
    private LotStatus status;

    @Column(name = "location_desc", length = 255)
    private String locationDesc;

    @Column(name = "plant_date")
    @Temporal(TemporalType.DATE)
    private Date plantDate;

    @Column(name = "expected_harvest_date")
    @Temporal(TemporalType.DATE)
    private Date expectedHarvestDate;

    @Column(name = "actual_harvest_date")
    @Temporal(TemporalType.DATE)
    private Date actualHarvestDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id")
    private Farm farm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_type_id")
    private CropType cropType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    // Enabled Cascade Delete: Deleting a production lot will delete all associated logs and records
    @OneToMany(mappedBy = "lot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CultivationLog> cultivationLogs = new ArrayList<>();

    @OneToMany(mappedBy = "lot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IrrigationLog> irrigationLogs = new ArrayList<>();

    @OneToMany(mappedBy = "lot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PestReport> pestReports = new ArrayList<>();

    @OneToMany(mappedBy = "lot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeatherLog> weatherLogs = new ArrayList<>();

    @OneToMany(mappedBy = "lot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HarvestRecord> harvestRecords = new ArrayList<>();

    @Column(name = "created_at", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}
