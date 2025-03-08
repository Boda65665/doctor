package com.kafka1.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "medications")
@Data
public class Medication {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "manual")
    private String manual;
    @Column(name = "recommendations")
    private String recommendations;
    @ElementCollection
    @CollectionTable(name = "medications_href_resources", joinColumns = @JoinColumn(name = "medication_id"))
    @Column(name = "url")
    private List<String> hrefs;
}
