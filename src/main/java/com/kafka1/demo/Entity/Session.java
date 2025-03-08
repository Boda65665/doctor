package com.kafka1.demo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_sessions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "time_start")
    private LocalTime timeStart;
    @Column(name = "time_end")
    private LocalTime timeEnd;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @Column(name = "zoom_url")
    private String url;
    @Column(name = "date")
    private LocalDate date;
    @Column(name="time_really_start")
    private LocalTime timeReallyStart;

    public Session(LocalTime timeStart, LocalTime timeEnd, User user, Doctor doctor, String url, LocalDate date) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.user = user;
        this.doctor = doctor;
        this.url = url;
        this.date = date;
    }
}
