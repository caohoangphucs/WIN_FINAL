package com.example.winfinal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "agri_supply")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgriSupply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supply_code", unique = true, length = 20)
    private String supplyCode;

    @Column(length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private SupplyCategory category;

    @Column(length = 20)
    private String unit;

    @Column(name = "stock_qty")
    private Double stockQty;

    @Column(name = "min_stock")
    private Double minStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
}
