package com.example.winfinal.entity.lookup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "activity_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityType {
    @Id
    @Column(length = 30)
    private String code;
}
