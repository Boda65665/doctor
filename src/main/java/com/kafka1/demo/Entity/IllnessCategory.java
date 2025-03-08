package com.kafka1.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "illness_category")
@Data
public class IllnessCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
}