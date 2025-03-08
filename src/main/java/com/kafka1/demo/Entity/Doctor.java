package com.kafka1.demo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Table(name = "doctors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "speciality")
    private String speciality;
    @Column(name = "price_per_minute")
    private int pricePerMinute;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "rating")
    private int rating;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    @OrderBy("dayOfWeek ASC, startTime ASC")
    private List<DoctorSchedules> doctorSchedulesList;

    public Doctor(String speciality, int pricePerMinute, User user, int rating, List<DoctorSchedules> doctorSchedulesList) {
        this.speciality = speciality;
        this.pricePerMinute = pricePerMinute;
        this.user = user;
        this.rating = rating;
        this.doctorSchedulesList = doctorSchedulesList;
    }
}
