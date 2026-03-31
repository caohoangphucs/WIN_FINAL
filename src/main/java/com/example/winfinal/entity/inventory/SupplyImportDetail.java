package com.example.winfinal.entity.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "supply_import_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyImportDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_id")
    private SupplyImport supplyImport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_id")
    private AgriSupply supply;

    private Double quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;
}
