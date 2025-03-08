package com.kafka1.demo.Services.DB;

import com.kafka1.demo.Converters.DoctorSchedulesConverter;
import com.kafka1.demo.DTO.DoctorSchedulesDTO;
import com.kafka1.demo.Entity.DoctorSchedules;
import com.kafka1.demo.Models.DayOfWeek;
import com.kafka1.demo.Repositoryes.DoctorSchedulesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class SchedulesDbService {
    private final DoctorSchedulesRepository schedulesRepository;
    private final DoctorSchedulesConverter doctorSchedulesConverter = new DoctorSchedulesConverter();
    private final FreeSchedulesDbService freeSchedulesDbService;

    public SchedulesDbService(DoctorSchedulesRepository schedulesRepository, FreeSchedulesDbService freeSchedulesDbService) {
        this.schedulesRepository = schedulesRepository;
        this.freeSchedulesDbService = freeSchedulesDbService;
    }

    public void save(DoctorSchedules doctorSchedules){
        schedulesRepository.save(doctorSchedules);
    }

    @Transactional
    public void deleteSchedulesById(int idSchedules) {
        schedulesRepository.deleteById(idSchedules);
    }

    public DoctorSchedulesDTO getFreeTime(LocalDate date, int doctorId,LocalTime startTime){
        int dayCode = date.getDayOfWeek().getValue()-1;
        return freeSchedulesDbService.getFreeTime(date,doctorId, findSchedulesByDoctorAndDayAndAfter(doctorId,dayCode,startTime),startTime);
    }

    public DoctorSchedulesDTO findSchedulesByDoctorAndDayAndAfter(int doctorId, int dayOfCode, LocalTime startTime){
        DayOfWeek dayOfWeek = DayOfWeek.fromDayCode(dayOfCode);
        List<DoctorSchedules> doctorSchedules = schedulesRepository.findByDoctorIdAndDayOfWeekAndEndTimeAfterOrderByStartTime(doctorId,dayOfWeek, startTime);
        if (doctorSchedules==null || doctorSchedules.isEmpty()) return null;
        return doctorSchedulesConverter.schedulesToSchedulesDTO(doctorSchedules).get(0);
    }
}
