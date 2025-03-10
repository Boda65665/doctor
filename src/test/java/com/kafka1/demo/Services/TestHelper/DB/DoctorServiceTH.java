package com.kafka1.demo.Services.TestHelper.DB;

import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.UserDbService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DoctorServiceTH {
    @Autowired
    DoctorDbService doctorDBService;
    @Autowired
    UserDbService userDbService;


    public Doctor createDoctor(){
        Doctor doctor = new Doctor();
        doctor.setUser(userDbService.findUserById(1));
        doctor.setDoctorSchedulesList(new ArrayList<>());

        doctorDBService.save(doctor);

        return doctorDBService.findDoctorById(1);
    }
}
