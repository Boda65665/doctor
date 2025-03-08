package com.kafka1.demo.Services.TestHelper.ControllerService;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.DTO.SessionDTO;
import com.kafka1.demo.DTO.UserDTO;
import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.Session;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Models.TypeSession;
import com.kafka1.demo.Services.ApiService.SessionControllerService;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.SessionDbService;
import com.kafka1.demo.Services.DB.UserDbService;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;


public class SessionControllerServiceTH {
    private final SessionControllerService sessionControllerService;
    private final UserDbService userDbService;
    private final SessionDbService sessionDbService;
    private final DoctorDbService doctorDbService;
    private final int TEST_USER_ID;
    private final String TEST_MAIL;
    private final int TEST_SESSION_ID;
    private final String TEST_MAIL_TWO;
    private final int TEST_USER_ID_TWO;

    public SessionControllerServiceTH(SessionControllerService sessionControllerService, UserDbService userDbService, SessionDbService sessionDbService, DoctorDbService doctorDbService, int testUserId, String testMail, int testSessionId, String testMailTwo, int testUserIdTwo) {
        this.sessionControllerService = sessionControllerService;
        this.userDbService = userDbService;
        this.sessionDbService = sessionDbService;
        this.doctorDbService = doctorDbService;
        TEST_USER_ID = testUserId;
        TEST_MAIL = testMail;
        TEST_SESSION_ID = testSessionId;
        TEST_MAIL_TWO = testMailTwo;
        TEST_USER_ID_TWO = testUserIdTwo;
    }

    public HttpResponseBody getMeSession(TypeSession typeSession){
        String stringType = (typeSession==TypeSession.OLD)?"old":"new";
        mockFindByEmail(TEST_MAIL,TEST_USER_ID);
        mockFindByTypeSessionAndUser(typeSession);

        return sessionControllerService.getMeSession(stringType,TEST_MAIL);
    }

    private void mockFindByEmail(String email,int id) {
        User mockUser = new User();
        mockUser.setId(id);
        when(userDbService.findUserByEmail(email)).thenReturn(mockUser);
    }

    private void mockFindByTypeSessionAndUser(TypeSession typeSession) {
        when(sessionDbService.findByTypeSessionAndUser(TEST_USER_ID, typeSession)).thenReturn(new ArrayList<>());
    }

    public HttpResponseBody getSessionById(String email,int sessionId) {
        mockFindSessionDtoById();
        mockFindByEmail(TEST_MAIL,TEST_USER_ID);
        mockFindByEmail(TEST_MAIL_TWO,TEST_USER_ID_TWO);

        return sessionControllerService.getSessionByID(email,sessionId);
    }

    private void mockFindSessionDtoById() {
        UserDTO user = new UserDTO();
        user.setId(TEST_USER_ID);

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setUser(user);

        when(sessionDbService.findSessionDtoById(TEST_SESSION_ID)).thenReturn(sessionDTO);
    }

    public HttpResponseBody editStatusActivateSession(LocalTime timeStartSession, LocalTime timeEndSession, LocalTime timeReallyStart,String sessionAction) {
        mockUserById();
        mockDoctorById();
        mockDoctorByUser();
        mockSessionById(timeStartSession,timeEndSession,timeReallyStart);

        return sessionControllerService.editStatusActivateSession(anyInt(),TEST_SESSION_ID,sessionAction);
    }

    private void mockUserById() {
        User user = new User();
        when(userDbService.findUserById(anyInt())).thenReturn(user);
    }

    private void mockDoctorById() {
        Doctor doctor = new Doctor();

        when(doctorDbService.findDoctorById(anyInt())).thenReturn(doctor);
    }

    private void mockDoctorByUser() {
        Doctor doctor = new Doctor();

        when(doctorDbService.findDoctorByUser(anyInt())).thenReturn(doctor);
    }

    private void mockSessionById(LocalTime timeStartSession, LocalTime timeEndSession, LocalTime timeReallyStart) {
        Session session = new Session();
        session.setTimeStart(timeStartSession);
        session.setTimeEnd(timeEndSession);
        session.setTimeReallyStart(timeReallyStart);
        session.setUser(new User());

        when(sessionDbService.findById(TEST_SESSION_ID)).thenReturn(session);
    }
}
