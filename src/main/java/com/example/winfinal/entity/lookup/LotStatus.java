package com.example.winfinal.entity.lookup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "lot_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotStatus {
    @Id
    @Column(length = 30)
    private String code;
}
