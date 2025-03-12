package com.kafka1.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chats")
@NoArgsConstructor
@Data
public class Chat {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne()
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @OneToOne()
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "chat")
    private List<Message> messages;

    public Chat(Doctor doctor, User user) {
        this.doctor = doctor;
        this.user = user;
    }
}
