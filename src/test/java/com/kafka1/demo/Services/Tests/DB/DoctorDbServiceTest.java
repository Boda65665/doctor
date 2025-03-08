package com.kafka1.demo.Services.Tests.DB;

import com.kafka1.demo.DemoApplication;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.TestHelper.DB.CleanerBD;
import com.kafka1.demo.Services.TestHelper.DB.DoctorServiceTH;
import com.kafka1.demo.Services.TestHelper.DB.UserServiceTH;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = {DemoApplication.class})
class DoctorDbServiceTest {
    @Autowired
    private DoctorDbService doctorDbServiced;
    @Autowired
    private DoctorServiceTH doctorServiceTH;
    @Autowired
    private UserServiceTH userServiceTH;
    @Autowired
    private CleanerBD cleanerBd;

    @BeforeEach
    void clearBd() {
        cleanerBd.clearBd();
    }

    @Test
    @DisplayName("Saving a doctor and verifying its existence in the database")
    void save() {
        doctorServiceTH.createDoctor();
        assertNotNull(doctorDbServiced.findDoctorById(1));
        assertNull(doctorDbServiced.findDoctorById(2));

        doctorServiceTH.createDoctor();
        assertNotNull(doctorDbServiced.findDoctorById(2));
    }

    @Test
    @DisplayName("Finding a doctor by associated user ID")
    void findDoctorByUser() {
        userServiceTH.createUser();
        doctorServiceTH.createDoctor();
        assertNotNull(doctorDbServiced.findDoctorByUser(1));
        assertNull(doctorDbServiced.findDoctorByUser(2));
    }

    @Test
    @DisplayName("Finding a doctor by ID")
    void findDoctorById(){
        doctorServiceTH.createDoctor();
        assertNotNull(doctorDbServiced.findDoctorById(1));
        assertNull(doctorDbServiced.findDoctorById(2));
    }
}