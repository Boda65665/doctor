package com.kafka1.demo.Services.Controller;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.DTO.DoctorDTO;
import com.kafka1.demo.DTO.SessionDTO;
import com.kafka1.demo.DTO.UserDTO;
import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.Session;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Models.Role;
import com.kafka1.demo.Models.TypeSession;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.SessionDbService;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import static org.springframework.http.HttpStatus.*;

@Service
public class SessionControllerService {
    private final JwtService jwtService;
    private final UserDbService userDBService;
    private final SessionDbService sessionDBService;
    private final DoctorDbService doctorDBService;

    public SessionControllerService(JwtService jwtService, UserDbService userDBService, SessionDbService sessionDBService, DoctorDbService doctorDBService) {
        this.jwtService = jwtService;
        this.userDBService = userDBService;
        this.sessionDBService = sessionDBService;
        this.doctorDBService = doctorDBService;
    }

    public HttpResponseBody getMeSession(String prefix, String email){
        HttpResponseBody sessionsBody = new HttpResponseBody();
        User user = userDBService.findUserByEmail(email);
        TypeSession typeSession = getSessionTypeByPrefix(prefix);
        sessionsBody.putData("sessions", getMeSessions(user,typeSession));
        sessionsBody.putData("type",typeSession);
        sessionsBody.setHttpStatus(OK);
        sessionsBody.setMessage("successfully");
        return sessionsBody;
    }

    private List<SessionDTO> getMeSessions(User user, TypeSession typeSession){
        return sessionDBService.findByTypeSessionAndUser(user.getId(), typeSession);
    }

    private TypeSession getSessionTypeByPrefix(String prefix){
        return (prefix!=null && prefix.equals("old"))?TypeSession.OLD:TypeSession.NEW;
    }

    public HttpResponseBody getSessionByID(String email, int id){
        User user = userDBService.findUserByEmail(email);
        SessionDTO sessionDTO = sessionDBService.findSessionDtoById(id);
        HttpResponseBody sessionBody = new HttpResponseBody();
        sessionBody.putData("session",sessionDTO);
        if (sessionDTO==null){
            sessionBody.setHttpStatus(NOT_FOUND);
            return sessionBody;
        }
        DoctorDTO sessionDoctor = sessionDTO.getDoctor();
        UserDTO ownerSession = sessionDTO.getUser();
        boolean isYouOwnSession = user.getId() == ownerSession.getId();
        if (user.getRole() == Role.DOCTOR){
            DoctorDTO doctor = doctorDBService.findDoctorDtoByUser(user.getId());
            isYouOwnSession = doctor.getId() == sessionDoctor.getId();
        }
        if (!isYouOwnSession){
            sessionBody.setHttpStatus(FORBIDDEN);
            sessionBody.putData("session",null);
            return sessionBody;
        }
        sessionBody.setHttpStatus(OK);
        return sessionBody;
    }

    public User getUserByRequest(HttpServletRequest request) {
        String email = jwtService.extractEmail(request);
        return userDBService.findUserByEmail(email);
    }

    public HttpResponseBody editStatusActivateSession(int idDoctorAccount, int idSession, String sessionAction) {
        HttpResponseBody httpResponseBody = new HttpResponseBody();
        Session session = sessionDBService.findById(idSession);

        if (session == null) {
            httpResponseBody.setHttpStatus(NOT_FOUND);
            httpResponseBody.setMessage("Сессия не найдена");
            return httpResponseBody;
        }

        if ("start".equals(sessionAction)) {
            handleStartSession(httpResponseBody, session, idDoctorAccount);
        } else {
            handleStopSession(httpResponseBody, session, idDoctorAccount);
        }
        return httpResponseBody;
    }

    private void handleStartSession(HttpResponseBody httpResponseBody, Session session, int idDoctorAccount) {
        LocalTime now = LocalTime.now();

        if (session.getTimeEnd().isBefore(now) && session.getTimeReallyStart() == null) {
            httpResponseBody.setHttpStatus(GONE);
            httpResponseBody.setMessage("Срок действия сессии истек");
        } else if (session.getTimeStart().isAfter(now)) {
            httpResponseBody.setHttpStatus(BAD_REQUEST);
            httpResponseBody.setMessage("Вы не можете начать сессию раньше времени, выбранного пользователем, а именно " + session.getTimeStart());
        } else if (session.getTimeReallyStart() != null) {
            httpResponseBody.setHttpStatus(CONFLICT);
            httpResponseBody.setMessage("Уже запущена");
        } else {
            httpResponseBody.setMessage("Успешно");
            httpResponseBody.setHttpStatus(OK);
            editStatusActivateSession("start", session, idDoctorAccount);
            session.setTimeReallyStart(now);
            sessionDBService.save(session);
        }
    }

    private void handleStopSession(HttpResponseBody httpResponseBody, Session session, int idDoctorAccount) {
        if (session.getTimeReallyStart() == null) {
            httpResponseBody.setHttpStatus(CONFLICT);
            httpResponseBody.setMessage("Нельзя остановить сессию, так как она еще не запущена!");
        } else {
            httpResponseBody.setMessage("Успешно");
            httpResponseBody.setHttpStatus(OK);
            editStatusActivateSession("stop", session, idDoctorAccount);
        }
    }

    private void editStatusActivateSession(String sessionAction, Session session, int idDoctorAccount) {
        if (sessionAction.equals("start")) activateSession(session);
        else deactivateSession(session,idDoctorAccount);
    }

    private void activateSession(Session session) {
        session.setTimeReallyStart(LocalTime.now());
        sessionDBService.save(session);
    }

    private void deactivateSession(Session session, int idDoctorAccount) {
        editBalance(session,idDoctorAccount);
        session.setTimeReallyStart(null);
        sessionDBService.save(session);
    }

    private void editBalance(Session session, int idDoctor) {
        User doctorAccount = userDBService.findUserById(idDoctor);
        Doctor requestingDoctor = doctorDBService.findDoctorByUser(doctorAccount.getId());
        User userOwnerSession = session.getUser();
        Long reallyDurationSessionInMinutes = getReallyDurationSessionInMinutes(session);
        int priceSession = (int) (reallyDurationSessionInMinutes*requestingDoctor.getPricePerMinute());
        double doctorBalance = doctorAccount.getBalance();
        if (userOwnerSession.getBalance()<priceSession){
            doctorAccount.setBalance(doctorBalance + priceSession);
            userOwnerSession.setBalance(0);
        }
        else {
            doctorAccount.setBalance(doctorBalance + priceSession);
            userOwnerSession.setBalance(userOwnerSession.getBalance()-priceSession);
        }
        userDBService.save(doctorAccount);
        userDBService.save(userOwnerSession);
    }

    private Long getReallyDurationSessionInMinutes(Session session) {
        LocalTime timeExceptedStart = session.getTimeStart();
        LocalTime timeExceptedEnd = session.getTimeEnd();
        Duration maxDurationSession = Duration.between(timeExceptedStart,timeExceptedEnd);
        long maxDurationSessionInMinutes = maxDurationSession.toMinutes();
        LocalTime timeReallyStart = session.getTimeReallyStart();
        LocalTime timeNow = LocalTime.now();
        Duration reallyDurationSession = Duration.between(timeReallyStart,timeNow);
        long reallyDurationSessionInMinutes = reallyDurationSession.toMinutes();
        if (reallyDurationSessionInMinutes > maxDurationSessionInMinutes){
            reallyDurationSessionInMinutes = maxDurationSessionInMinutes;
        }
        return reallyDurationSessionInMinutes;
    }
}
