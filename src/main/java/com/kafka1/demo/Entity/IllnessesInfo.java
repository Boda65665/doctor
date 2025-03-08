package com.kafka1.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name="illnesses_info")
@ToString
public class IllnessesInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    private String label;
    private String therapy;
    private String alternativeMethods;
    private String medicalResearchAndDiagnostics;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private IllnessCategory category;
}
