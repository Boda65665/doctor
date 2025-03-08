package com.kafka1.demo.Entity;

import com.kafka1.demo.Models.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "code_email")
    private String emailCode;
    @Column(name = "time_send_email_code")
    private LocalDateTime timeLastSendCode;
    @Column(name = "balance")
    private double balance;
    @Column(name = "secret_key")
    private String secretKey;


    public User(String email, String password, Role role, String lastName, String firstName, String emailCode, LocalDateTime timeLastSendCode,double balance) {
        this.email = email;
        this.balance=balance;
        this.password = password;
        this.role = role;
        this.lastName = lastName;
        this.firstName = firstName;
        this.emailCode = emailCode;
        this.timeLastSendCode = timeLastSendCode;
    }

    public User(String email, String password, Role role, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
