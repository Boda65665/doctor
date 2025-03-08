package com.kafka1.demo.Entity;

import com.kafka1.demo.Models.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @Column(name = "text")
    private String text;
    @Column(name = "owner")
    @Enumerated(EnumType.STRING)
    private Role owner;

    public Message(User user, Doctor doctor, String text, Role owner) {
        this.user = user;
        this.doctor = doctor;
        this.text = text;
        this.owner = owner;
    }
}
