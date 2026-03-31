package com.example.winfinal.entity.inventory;

import com.example.winfinal.entity.core.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "supply_import")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyImport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "import_code", unique = true, length = 20)
    private String importCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "import_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date importDate;

    @OneToMany(mappedBy = "supplyImport", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<SupplyImportDetail> details = new java.util.ArrayList<>();
}
