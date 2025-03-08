package com.kafka1.demo.Converters;

import com.kafka1.demo.DTO.DoctorDTO;
import com.kafka1.demo.Entity.Doctor;

public class DoctorConverter {
    private final UserConverter userConverter = new UserConverter();
    private final DoctorSchedulesConverter doctorSchedulesConverter = new DoctorSchedulesConverter();

    public DoctorDTO doctorToDoctorDto(Doctor doctor){
        DoctorDTO doctorConverted = new DoctorDTO();
        doctorConverted.setId(doctor.getId());
        doctorConverted.setUser(userConverter.userToUserDTO(doctor.getUser()));
        doctorConverted.setSpeciality(doctor.getSpeciality());
        doctorConverted.setPricePerMinute(doctor.getPricePerMinute());
        doctorConverted.setRating(doctor.getRating());
        doctorConverted.setDoctorSchedulesDTO(doctorSchedulesConverter.schedulesToSchedulesDTO(doctor.getDoctorSchedulesList()));
        return doctorConverted;
    }
}
