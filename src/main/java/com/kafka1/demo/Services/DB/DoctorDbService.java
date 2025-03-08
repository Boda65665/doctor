package com.kafka1.demo.Services.DB;

import com.kafka1.demo.Converters.DoctorConverter;
import com.kafka1.demo.DTO.DoctorDTO;

import com.kafka1.demo.DTO.DoctorSchedulesDTO;
import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.DoctorSchedules;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Repositoryes.DoctorRepository;
import com.kafka1.demo.Repositoryes.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class DoctorDbService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DoctorConverter doctorConverter = new DoctorConverter();
    private final SchedulesDbService schedulesDbService;

    public DoctorDbService(DoctorRepository doctorRepository, UserRepository userRepository, SchedulesDbService schedulesDbService) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.schedulesDbService = schedulesDbService;
    }

    public void save(Doctor doctor){
        doctorRepository.save(doctor);
    }

    public DoctorDTO findDoctorDtoById(int id){
        Doctor doctor = findDoctorById(id);
        if (doctor!=null) return doctorConverter.doctorToDoctorDto(doctor);
        return null;
    }

    public Doctor findDoctorById(int id){
        return doctorRepository.findById(id);
    }

    public DoctorDTO findDoctorDtoByUser(int userId){
        User user = userRepository.findById(userId);
        if (user==null)return null;
        Doctor doctor = doctorRepository.findByUser(user);
        if (doctor!=null) return doctorConverter.doctorToDoctorDto(doctor);
        return null;
    }

    public Doctor findDoctorByUser(int userId){
        User user = userRepository.findById(userId);
        if (user==null)return null;
        return doctorRepository.findByUser(user);
    }

    public void addTimeToSchedules(DoctorSchedules doctorSchedules) {
        schedulesDbService.save(doctorSchedules);
    }

    public void deleteSchedulesById(int idSchedules) {
        schedulesDbService.deleteSchedulesById(idSchedules);
    }

    public DoctorSchedulesDTO findSchedulesByDoctorAndDayAndTimeAfter(int doctorId, int dayCode,LocalTime startTime) {
        return schedulesDbService.findSchedulesByDoctorAndDayAndAfter(doctorId, dayCode, startTime);
    }

    public DoctorSchedulesDTO getFreeTime(LocalDate date, int doctorId){
        return schedulesDbService.getFreeTime(date, doctorId, LocalTime.now());
    }
}
