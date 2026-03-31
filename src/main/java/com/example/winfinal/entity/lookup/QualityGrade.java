package com.example.winfinal.entity.lookup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "quality_grade")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QualityGrade {
    @Id
    @Column(length = 10)
    private String code;
}
