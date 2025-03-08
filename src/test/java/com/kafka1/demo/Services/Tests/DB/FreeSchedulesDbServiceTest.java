package com.kafka1.demo.Services.Tests.DB;

import com.kafka1.demo.DTO.DoctorSchedulesDTO;
import com.kafka1.demo.DemoApplication;
import com.kafka1.demo.Models.DayOfWeek;
import com.kafka1.demo.Models.TimeInterval;
import com.kafka1.demo.Services.DB.FreeSchedulesDbService;
import com.kafka1.demo.Services.DB.SchedulesDbService;
import com.kafka1.demo.Services.TestHelper.DB.CleanerBD;
import com.kafka1.demo.Services.TestHelper.DB.DoctorServiceTH;
import com.kafka1.demo.Services.TestHelper.DB.SchedulesDbServiceTH;
import com.kafka1.demo.Services.TestHelper.DB.SessionServiceTH;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@SpringBootTest(classes = {DemoApplication.class})
class FreeSchedulesDbServiceTest {
    @Autowired
    private SchedulesDbServiceTH schedulesDbServiceTH;
    @Autowired
    private FreeSchedulesDbService freeSchedulesDbService;
    @Autowired
    private CleanerBD cleanerBd;
    @Autowired
    private DoctorServiceTH doctorServiceTH;
    @Autowired
    private SessionServiceTH sessionServiceTH;
    @Autowired
    private SchedulesDbService schedulesDbService;

    @BeforeEach
    void clearBd() {
        cleanerBd.clearBd();
    }

    @Test
    @DisplayName("get free doctor's schedules")
    void getFreeTimeTest(){
        List<TimeInterval> freeTimeIntervals = getFreeTime();

        assertEquals(6, freeTimeIntervals.size());


        TimeInterval firstTimeInterval = freeTimeIntervals.get(0);
        assertAll(
                () -> assertEquals(firstTimeInterval.start(),LocalTime.of(1,0)),
                () -> assertEquals(firstTimeInterval.end(),LocalTime.of(2,0))
        );

        TimeInterval secondTimeInterval = freeTimeIntervals.get(1);
        assertAll(
                () -> assertEquals(secondTimeInterval.start(),LocalTime.of(3,30)),
                () -> assertEquals(secondTimeInterval.end(),LocalTime.of(3,35))
        );

        TimeInterval thirdTimeInterval = freeTimeIntervals.get(2);
        assertAll(
                () -> assertEquals(thirdTimeInterval.start(),LocalTime.of(6,0)),
                () -> assertEquals(thirdTimeInterval.end(),LocalTime.of(7,0))
        );

        TimeInterval fourthTimeInterval = freeTimeIntervals.get(3);
        assertAll(
                () -> assertEquals(fourthTimeInterval.start(),LocalTime.of(10,35)),
                () -> assertEquals(fourthTimeInterval.end(),LocalTime.of(10,40))
        );

        TimeInterval fifthTimeInterval = freeTimeIntervals.get(4);
        assertAll(
                () -> assertEquals(fifthTimeInterval.start(),LocalTime.of(11,0)),
                () -> assertEquals(fifthTimeInterval.end(),LocalTime.of(12,0))
        );

        TimeInterval sixthTimeInterval = freeTimeIntervals.get(5);
        assertAll(
                () -> assertEquals(sixthTimeInterval.start(),LocalTime.of(22,30)),
                () -> assertEquals(sixthTimeInterval.end(),LocalTime.of(23,0))
        );


    }

    private List<TimeInterval> getFreeTime() {
        init();
        LocalDate now = LocalDate.now();
        LocalDate nextMonday = now.with(TemporalAdjusters.next(java.time.DayOfWeek.MONDAY));
        LocalTime startTime = LocalTime.of(0,0);

        DoctorSchedulesDTO doctorSchedulesDTO = freeSchedulesDbService.getFreeTime(nextMonday,1,schedulesDbService.findSchedulesByDoctorAndDayAndAfter(1,0,startTime),startTime);
        return doctorSchedulesDTO.getTimeIntervals();
    }

    private void init() {
        doctorServiceTH.createDoctor();
        initSchedules();
        initSession();
    }

    private void initSchedules() {
        schedulesDbServiceTH.save(1, DayOfWeek.MONDAY, LocalTime.of(1,0),LocalTime.of(2,0));
        schedulesDbServiceTH.save(1, DayOfWeek.MONDAY, LocalTime.of(3,30),LocalTime.of(4,0));
        schedulesDbServiceTH.save(1, DayOfWeek.MONDAY, LocalTime.of(5,30),LocalTime.of(7,0));
        schedulesDbServiceTH.save(1, DayOfWeek.MONDAY, LocalTime.of(10,30),LocalTime.of(12,0));
        schedulesDbServiceTH.save(1, DayOfWeek.MONDAY, LocalTime.of(21,30),LocalTime.of(22,0));
        schedulesDbServiceTH.save(1, DayOfWeek.MONDAY, LocalTime.of(22,30),LocalTime.of(23,0));

    }

    private void initSession() {
        LocalDate now = LocalDate.now();
        LocalDate nextMonday = now.with(TemporalAdjusters.next(java.time.DayOfWeek.MONDAY));
        LocalDate nextTuesday = now.with(TemporalAdjusters.next(java.time.DayOfWeek.TUESDAY));

        sessionServiceTH.createSession(nextMonday,new TimeInterval(LocalTime.of(3,35),LocalTime.of(4,0)));

        sessionServiceTH.createSession(nextMonday,new TimeInterval(LocalTime.of(5,30),LocalTime.of(6,0)));
        sessionServiceTH.createSession(nextMonday,new TimeInterval(LocalTime.of(10,30),LocalTime.of(10,35)));
        sessionServiceTH.createSession(nextMonday,new TimeInterval(LocalTime.of(10,40),LocalTime.of(11,0)));
        sessionServiceTH.createSession(nextMonday,new TimeInterval(LocalTime.of(21,30),LocalTime.of(21,56)));


        sessionServiceTH.createSession(nextTuesday,new TimeInterval(LocalTime.of(1,0),LocalTime.of(2,0)));
        sessionServiceTH.createSession(nextTuesday,new TimeInterval(LocalTime.of(15,30),LocalTime.of(19,0)));
    }
}