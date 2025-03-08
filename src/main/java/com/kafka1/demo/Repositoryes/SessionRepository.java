package com.kafka1.demo.Repositoryes;

import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.Session;
import com.kafka1.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    Session findById(int id);
    List<Session> findByDoctorAndDateAndTimeEndAfterOrderByTimeStart(Doctor doctor, LocalDate date, LocalTime nowTime);
    @Query("select s from Session s where s.user=:user AND s.timeEnd>:time AND s.date>=:date")
    List<Session> findNewSessionByUser(@Param("user") User user, @Param("time") LocalTime time, @Param("date") LocalDate date);
    @Query("select s from Session s where s.user=:user AND (s.date<:date OR (s.date=:date AND s.timeEnd<:time))")
    List<Session> findOldSessionByUser(@Param("user") User user, @Param("time") LocalTime time, @Param("date") LocalDate date);
    @Query("select s from Session s where s.doctor=:doctor AND s.timeEnd>:time AND s.date>=:date")
    List<Session> findNewSessionByDoctor(@Param("doctor") Doctor doctor, @Param("time") LocalTime time, @Param("date") LocalDate date);
    @Query("select s from Session s where s.doctor=:doctor AND (s.date<:date OR (s.date=:date AND s.timeEnd<:time))")
    List<Session> findOldSessionByDoctor(@Param("doctor") Doctor doctor, @Param("time") LocalTime time, @Param("date") LocalDate date);
    @Query("select s from Session s where s.doctor=:doctor AND s.user=:user AND (s.date<:date OR (s.date=:date AND s.timeEnd<:time))")
    List<Session> findOldSessionByDoctorAndUser(@Param("doctor") Doctor doctor,User user, @Param("time") LocalTime time, @Param("date") LocalDate date);
    @Modifying
    @Transactional
    @Query("delete from Session s where s.doctor=:doctor AND s.date=:date AND s.timeStart BETWEEN :startTime AND :endTime")
    void deleteByDateAndDoctorAndTime(@Param("doctor") Doctor doctor, LocalDate date, @Param("startTime")LocalTime startTime,@Param("endTime") LocalTime endTime);
}
