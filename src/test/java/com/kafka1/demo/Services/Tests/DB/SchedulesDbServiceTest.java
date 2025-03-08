package com.kafka1.demo.Services.Tests.DB;

import com.kafka1.demo.DTO.DoctorSchedulesDTO;
import com.kafka1.demo.DemoApplication;
import com.kafka1.demo.Models.DayOfWeek;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.SchedulesDbService;
import com.kafka1.demo.Services.TestHelper.DB.CleanerBD;
import com.kafka1.demo.Services.TestHelper.DB.SchedulesDbServiceTH;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {DemoApplication.class})
class SchedulesDbServiceTest {
    @Autowired
    private DoctorDbService doctorDbService;
    @Autowired
    private SchedulesDbServiceTH schedulesDbServiceTH;
    @Autowired
    private SchedulesDbService schedulesDbService;
    @Autowired
    private CleanerBD cleanerBd;

    private static final int DOCTOR_1_ID = 1;
    private static final int DOCTOR_2_ID = 2;

    @BeforeEach
    void setUp() {
        cleanerBd.clearBd();
    }

    @Test
    @DisplayName("Save schedules for doctors")
    void saveSchedules() {
        saveSchedulesForDoctor(DOCTOR_1_ID, 5);
        verifyDoctorScheduleCount(DOCTOR_1_ID, 5);

        saveSchedulesForDoctor(DOCTOR_2_ID, 3);
        verifyDoctorScheduleCount(DOCTOR_2_ID, 3);
    }

    @Test
    @DisplayName("Delete schedule by ID")
    void deleteSchedulesById() {
        saveSchedulesForDoctor(DOCTOR_1_ID, 5);
        verifyDoctorScheduleCount(DOCTOR_1_ID, 5);

        int idScheduleIntervalDeleted = 1;
        schedulesDbService.deleteSchedulesById(idScheduleIntervalDeleted);
        verifyDoctorScheduleCount(DOCTOR_1_ID, 4);
    }

    @Test
    @DisplayName("Find schedule by doctor, day of week, and time")
    void findSchedulesByDoctorAndDayAndTimeAfter() {
        createSchedules();

        DoctorSchedulesDTO doctorSchedules = doctorDbService.findSchedulesByDoctorAndDayAndTimeAfter(
                DOCTOR_1_ID, DayOfWeek.MONDAY.dayCode, LocalTime.of(5, 0));

        assertNotNull(doctorSchedules);
        assertEquals(2, doctorSchedules.getTimeIntervals().size());
    }

    private void createSchedules() {
        schedulesDbServiceTH.save(DOCTOR_1_ID, DayOfWeek.MONDAY, LocalTime.of(5, 0), LocalTime.of(6, 0));
        schedulesDbServiceTH.save(DOCTOR_1_ID, DayOfWeek.MONDAY, LocalTime.of(6, 0), LocalTime.of(7, 0));
        schedulesDbServiceTH.save(DOCTOR_1_ID, DayOfWeek.MONDAY, LocalTime.of(1, 0), LocalTime.of(2, 0));
        schedulesDbServiceTH.save(DOCTOR_1_ID, DayOfWeek.SUNDAY, LocalTime.of(5, 0), LocalTime.of(6, 0));
    }

    private void saveSchedulesForDoctor(int doctorId, int numberOfSchedules) {
        schedulesDbServiceTH.save(numberOfSchedules, doctorId);
    }

    private void verifyDoctorScheduleCount(int doctorId, int expectedCount) {
        int actualCount = doctorDbService.findDoctorById(doctorId).getDoctorSchedulesList().size();
        assertEquals(expectedCount, actualCount, "The number of schedules for doctor " + doctorId + " should be " + expectedCount);
    }
}
