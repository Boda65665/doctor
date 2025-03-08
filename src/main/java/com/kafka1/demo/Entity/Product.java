package com.kafka1.demo.Entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "urls")
    private String urls;
    @Column(name = "categories")
    private String categories;
    @Column(name = "price")
    private double price;
}
