package com.kafka1.demo.Entity;

import com.kafka1.demo.Models.DayOfWeek;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "doctor_schedule")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorSchedules {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
    @Column(name = "day_week")
    @Enumerated(EnumType.ORDINAL)
    private DayOfWeek dayOfWeek;
    @Column(name = "doctor_id")
    private int doctorId;

    public DoctorSchedules(LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek, int doctorId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
        this.doctorId = doctorId;
    }

}