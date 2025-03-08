package com.kafka1.demo.Converters;

import com.kafka1.demo.DTO.SessionDTO;
import com.kafka1.demo.Entity.Session;

import java.util.ArrayList;
import java.util.List;

public class SessionConverter {
    private final UserConverter userConverter = new UserConverter();
    private final DoctorConverter doctorConverter  = new DoctorConverter();

    public SessionDTO sessionToSessionDto(Session session){
        SessionDTO sessionConverted = new SessionDTO();
        sessionConverted.setId(session.getId());
        sessionConverted.setUser(userConverter.userToUserDTO(session.getUser()));
        sessionConverted.setDoctor(doctorConverter.doctorToDoctorDto(session.getDoctor()));
        sessionConverted.setTimeEnd(session.getTimeEnd());
        sessionConverted.setTimeStart(session.getTimeStart());
        sessionConverted.setUrl(session.getUrl());
        sessionConverted.setTimeReallyStart(session.getTimeReallyStart());
        return sessionConverted;
    }

    public List<SessionDTO> allSessionToSessionDto(List<Session> sessions){
        List<SessionDTO> sessionDTOS = new ArrayList<>();
        for (Session session : sessions) {
            sessionDTOS.add(sessionToSessionDto(session));
        }
        return sessionDTOS;
    }
}
