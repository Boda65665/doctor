package com.kafka1.demo.Converters;

import com.kafka1.demo.DTO.DoctorSchedulesDTO;
import com.kafka1.demo.Entity.DoctorSchedules;
import com.kafka1.demo.Models.DayOfWeek;
import com.kafka1.demo.Models.TimeInterval;
import java.util.ArrayList;
import java.util.List;


public class DoctorSchedulesConverter {
    public List<DoctorSchedulesDTO> schedulesToSchedulesDTO(List<DoctorSchedules> doctorSchedules){
        List<DoctorSchedulesDTO> doctorSchedulesDTOS = new ArrayList<>();
        List<TimeInterval> timeIntervals = new ArrayList<>();
        DayOfWeek day = DayOfWeek.MONDAY;
        for (DoctorSchedules doctorSchedule : doctorSchedules) {
            if (doctorSchedule.getDayOfWeek() != day) {
                if (!timeIntervals.isEmpty()) {
                    doctorSchedulesDTOS.add(new DoctorSchedulesDTO(day, timeIntervals));

                }
                day = doctorSchedule.getDayOfWeek();
                timeIntervals=new ArrayList<>();
            }
            timeIntervals.add(new TimeInterval(doctorSchedule.getStartTime(),doctorSchedule.getEndTime(),doctorSchedule.getId()));
        }
        if (!timeIntervals.isEmpty()) doctorSchedulesDTOS.add(new DoctorSchedulesDTO(day,timeIntervals));
        return doctorSchedulesDTOS;
    }
}