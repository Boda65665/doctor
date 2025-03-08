package com.kafka1.demo.Services.Tests.DB;

import com.kafka1.demo.DemoApplication;
import com.kafka1.demo.Entity.Session;
import com.kafka1.demo.Models.TimeInterval;
import com.kafka1.demo.Models.TypeSession;
import com.kafka1.demo.Repositoryes.SessionRepository;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.SessionDbService;
import com.kafka1.demo.Services.TestHelper.DB.CleanerBD;
import com.kafka1.demo.Services.TestHelper.DB.SessionServiceTH;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {DemoApplication.class})
class SessionDbServiceTest {
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private SessionServiceTH sessionServiceTH;
    @Autowired
    private SessionDbService sessionDbService;
    @Autowired
    private DoctorDbService doctorDBService;
    @Autowired
    private CleanerBD cleanerBd;

    @BeforeEach
    public void clearBd() {
        cleanerBd.clearBd();
    }

    @Test
    @DisplayName("Saving a session")
    void save() {
        sessionServiceTH.createSession();
        assertEquals(1, sessionRepository.count());
    }

    @Test
    @DisplayName("Finding a session DTO by ID")
    void findSessionDtoById() {
        sessionServiceTH.createSession();

        int existingId = 1;
        int notExistingId = 9213213;
        assertAll(
                () -> assertNotNull(sessionDbService.findSessionDtoById(existingId)),
                () -> assertNull(sessionDbService.findSessionDtoById(notExistingId))
        );
    }

    @Test
    @DisplayName("Finding a session by ID")
    void findById() {
        sessionServiceTH.createSession();

        assertNotNull(sessionDbService.findById(1));
        assertNull(sessionDbService.findById(2));
    }

    @Test
    @DisplayName("Finding new sessions by user")
    void findNewSessionByUser() {
        sessionServiceTH.createRandomCountOldSessions();

        int count = sessionServiceTH.createRandomCountNewSessions();
        assertEquals(count, sessionDbService.findByTypeSessionAndUser(1, TypeSession.NEW).size());
    }

    @Test
    @DisplayName("Finding old sessions by user")
    void findByOldSession() {
        sessionServiceTH.createRandomCountNewSessions();

        int count = sessionServiceTH.createRandomCountOldSessions();
        assertEquals(count, sessionDbService.findByTypeSessionAndUser(1, TypeSession.OLD).size());
    }

    @Test
    @DisplayName("Deleting a session by date, doctor, and time interval")
    void deleteByDateAndDoctor() {
        LocalDate dateFirstSession = LocalDate.now();
        TimeInterval timeIntervalFirstSession = new TimeInterval(LocalTime.now(), LocalTime.now().plusMinutes(1));
        sessionServiceTH.createSessionWithDateTime(dateFirstSession, timeIntervalFirstSession);

        LocalDate dateSecondSession = LocalDate.now().minusDays(1);
        TimeInterval timeIntervalSecondSession = new TimeInterval(LocalTime.now(), LocalTime.now().plusMinutes(5));
        sessionServiceTH.createSessionWithDateTime(dateSecondSession, timeIntervalSecondSession);

        assertEquals(2, sessionRepository.count());

        sessionDbService.deleteByDateAndDoctor(dateSecondSession, doctorDBService.findDoctorById(1), timeIntervalSecondSession);
        assertEquals(1, sessionRepository.count());
    }

    @Test
    @DisplayName("Finding sessions by doctor, date, and time end after a specific time, ordered by time start")
    void findByDoctorAndDateAndTimeEndAfterOrderByTimeStart() {
        LocalDate dateFirstSession = LocalDate.now();
        LocalTime startTimeFirstSession = LocalTime.of(10, 0);
        LocalTime endTimeFirstSession = LocalTime.of(11, 0);
        TimeInterval timeIntervalFirstSession = new TimeInterval(startTimeFirstSession, endTimeFirstSession);
        sessionServiceTH.createSessionWithDateTime(dateFirstSession, timeIntervalFirstSession);

        LocalDate dateSecondSession = LocalDate.now().minusDays(1);
        LocalTime startTimeSecondSession = LocalTime.of(12, 0);
        LocalTime endTimeSecondSession = LocalTime.of(13, 0);
        TimeInterval timeIntervalSecondSession = new TimeInterval(startTimeSecondSession, endTimeSecondSession);
        sessionServiceTH.createSessionWithDateTime(dateSecondSession, timeIntervalSecondSession);

        List<Session> sessions = sessionDbService.findByDoctorAndDateAndTimeEndAfterOrderByTimeStart(1, dateFirstSession, endTimeFirstSession.minusMinutes(1));
        assertEquals(1, sessions.size());
        assertEquals(1, sessions.get(0).getId());
    }
}