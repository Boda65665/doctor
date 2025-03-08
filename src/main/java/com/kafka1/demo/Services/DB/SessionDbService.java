package com.kafka1.demo.Services.DB;

import com.kafka1.demo.Converters.SessionConverter;
import com.kafka1.demo.DTO.SessionDTO;
import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.Session;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Models.Role;
import com.kafka1.demo.Models.TimeInterval;
import com.kafka1.demo.Models.TypeSession;
import com.kafka1.demo.Repositoryes.DoctorRepository;
import com.kafka1.demo.Repositoryes.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SessionDbService {
    private final SessionRepository sessionRepository;
    private final SessionConverter sessionConverter = new SessionConverter();
    private final UserDbService userDBService;
    private final DoctorRepository doctorRepository;

    public SessionDbService(SessionRepository sessionRepository, UserDbService userDBService, DoctorRepository doctorRepository) {
        this.sessionRepository = sessionRepository;
        this.userDBService = userDBService;
        this.doctorRepository = doctorRepository;
    }

    public void save(Session session){
        sessionRepository.save(session);
    }

    public SessionDTO findSessionDtoById(int id){
        Session session = findById(id);
        if (session==null)return null;
        return sessionConverter.sessionToSessionDto(session);
    }

    public Session findById(int id){
        return sessionRepository.findById(id);
    }

    public List<SessionDTO> findByTypeSessionAndUser(int userId, TypeSession typeSession){
        User user = userDBService.findUserById(userId);
        if (user==null) return new ArrayList<>();
        List<Session> sessions;
        if (user.getRole()!=Role.DOCTOR) {
            if (typeSession==TypeSession.OLD) sessions = sessionRepository.findOldSessionByUser(user, LocalTime.now(), LocalDate.now());
            else sessions = sessionRepository.findNewSessionByUser(user, LocalTime.now(), LocalDate.now());
        }
        else {
            if (typeSession==TypeSession.OLD) sessions = sessionRepository.findOldSessionByDoctor(doctorRepository.findByUser(user), LocalTime.now(), LocalDate.now());
            else sessions = sessionRepository.findNewSessionByDoctor(doctorRepository.findByUser(user), LocalTime.now(), LocalDate.now());
        }
        if (sessions==null) return new ArrayList<>();
        return sessionConverter.allSessionToSessionDto(sessions);
    }

    public void deleteByDateAndDoctor(LocalDate date,Doctor doctor,TimeInterval intervalDeleting){
        sessionRepository.deleteByDateAndDoctorAndTime(doctor,date,intervalDeleting.start(),intervalDeleting.end());
    }

    public List<Session> findByDoctorAndDateAndTimeEndAfterOrderByTimeStart(int doctorId, LocalDate date, LocalTime time) {
        return sessionRepository.findByDoctorAndDateAndTimeEndAfterOrderByTimeStart(doctorRepository.findById(doctorId), date, time);
    }
}
