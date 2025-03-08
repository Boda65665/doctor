package com.kafka1.demo.Repositoryes;

import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.DoctorSchedules;
import com.kafka1.demo.Models.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface DoctorSchedulesRepository extends JpaRepository<DoctorSchedules,Integer> {
    void deleteById(int id);
    List<DoctorSchedules> findByDoctorIdAndDayOfWeekAndEndTimeAfterOrderByStartTime(int doctorId, DayOfWeek dayOfWeek, LocalTime time);
    DoctorSchedules findById(int id);}
