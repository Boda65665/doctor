package com.kafka1.demo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "article")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "text")
    private String text;

    public Article(String name, String text) {
        this.name = name;
        this.text = text;
    }
}
