package com.kafka1.demo.Services.TestHelper.DB;

import com.kafka1.demo.Entity.DoctorSchedules;
import com.kafka1.demo.Models.DayOfWeek;
import com.kafka1.demo.Repositoryes.DoctorRepository;
import com.kafka1.demo.Services.DB.SchedulesDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class SchedulesDbServiceTH {
    @Autowired
    private DoctorServiceTH doctorServiceTH;
    @Autowired
    private SchedulesDbService schedulesDbService;
    @Autowired
    private DoctorRepository doctorRepository;

    public void save(int count,int doctorId){
        save(count,doctorId,DayOfWeek.MONDAY,null,null);
    }

    public void save(int count, int doctorId, DayOfWeek day,LocalTime startTime,LocalTime ednTime){
        for (int i = 0; i < count; i++) {
            save(doctorId,day,startTime,ednTime);
        }
    }

    public void save(int doctorId, DayOfWeek day,LocalTime startTime,LocalTime ednTime){
        if (doctorRepository.findById(doctorId)==null)doctorServiceTH.createDoctor();
        DoctorSchedules doctorSchedules = new DoctorSchedules();
        doctorSchedules.setDoctorId(doctorId);
        doctorSchedules.setDayOfWeek(day);
        doctorSchedules.setStartTime(startTime);
        doctorSchedules.setEndTime(ednTime);
        schedulesDbService.save(doctorSchedules);
    }
}
