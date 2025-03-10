package com.kafka1.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
public class Message {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "text")
    private String text;
    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Message(User user, Doctor doctor, String text, User owner) {
        this.user = user;
        this.doctor = doctor;
        this.text = text;
        this.owner = owner;
    }
}
