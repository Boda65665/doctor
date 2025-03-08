package com.kafka1.demo.Services.TestHelper.DB;

import com.kafka1.demo.Entity.Session;
import com.kafka1.demo.Models.TimeInterval;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.SessionDbService;
import com.kafka1.demo.Services.DB.UserDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

@Component
public class SessionServiceTH {
    private static final String DEFAULT_MAIL = "test_mail";
    @Autowired
    private SessionDbService sessionDbService;
    @Autowired
    private UserServiceTH userServiceTH;
    @Autowired
    private UserDbService userDbService;
    @Autowired
    private DoctorServiceTH doctorServiceTH;
    @Autowired
    private DoctorDbService doctorDBService;

    public void createSession() {
        createSession(DEFAULT_MAIL);
    }

    public void createSession(String email) {
        createSession(LocalDate.now(),new TimeInterval(LocalTime.now(), LocalTime.now()),email);
    }

    public void createSession(LocalDate dateSession,TimeInterval timeSession){
        createSession(dateSession,timeSession,DEFAULT_MAIL);
    }

    public void createSession(LocalDate dateSession,TimeInterval timeSession,String email) {
        if (userDbService.findUserById(1)==null)userServiceTH.createUser(email);
        if (doctorDBService.findDoctorById(1)==null)doctorServiceTH.createDoctor();
        Session session = new Session();
        session.setTimeStart(timeSession.start());
        session.setTimeEnd(timeSession.end());
        session.setDate(dateSession);
        session.setUser(userDbService.findUserById(1));
        session.setDoctor(doctorDBService.findDoctorById(1));

        sessionDbService.save(session);
    }

    public int createRandomCountNewSessions() {
        return createRandomCountNewSessions(DEFAULT_MAIL);
    }

    public int createRandomCountNewSessions(String email) {
        Random random = new Random();
        int count = random.nextInt(10)+1;

        for (int i = 1; i <= count; i++) {
            LocalDateTime timeEnd = LocalDateTime.now().plusMinutes(random.nextInt(60) + 1);
            createSession(timeEnd.toLocalDate(),new TimeInterval(null,timeEnd.toLocalTime()),email);
        }
        return count;
    }

    public int createRandomCountOldSessions(){
        return createRandomCountOldSessions(DEFAULT_MAIL);
    }

    public int createRandomCountOldSessions(String email) {
        Random random = new Random();
        int count = random.nextInt(10)+1;

        LocalDateTime now = LocalDateTime.now();
        for (int i = 1; i <= count; i++) {
            LocalDateTime timeEnd = now.minusMinutes(random.nextInt(60) + 1);
            createSession(timeEnd.toLocalDate(),new TimeInterval(null,timeEnd.toLocalTime()),email);
        }
        return count;
    }



    public void createSessionWithDateTime(LocalDate localDate, TimeInterval timeInterval){
        createSession(localDate,timeInterval);
    }
}
