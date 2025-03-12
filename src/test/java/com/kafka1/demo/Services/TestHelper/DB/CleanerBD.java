package com.kafka1.demo.Services.TestHelper.DB;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CleanerBD {
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void clearBd(){
        clearSchedules();

        clearSessions();


        clearMessage();

        clearChat();

        clearDoctors();

        clearUsers();
    }

    private void clearSchedules() {
        entityManager.createNativeQuery("DELETE FROM doctor_schedule").executeUpdate();
        entityManager.createNativeQuery("ALTER SEQUENCE doctor_schedule_id_seq RESTART WITH 1").executeUpdate();
    }

    private void clearSessions() {
        entityManager.createNativeQuery("DELETE FROM doctor_sessions").executeUpdate();
        entityManager.createNativeQuery("ALTER SEQUENCE doctor_sessions_id_seq RESTART WITH 1").executeUpdate();
    }

    private void clearMessage() {
        entityManager.createNativeQuery("DELETE FROM messages").executeUpdate();
        entityManager.createNativeQuery("ALTER SEQUENCE messages_id_seq RESTART WITH 1").executeUpdate();
    }

    private void clearChat() {
        entityManager.createNativeQuery("DELETE FROM chats").executeUpdate();
        entityManager.createNativeQuery("ALTER SEQUENCE chats_id_seq RESTART WITH 1").executeUpdate();
    }

    private void clearDoctors() {
        entityManager.createNativeQuery("DELETE FROM doctors").executeUpdate();
        entityManager.createNativeQuery("ALTER SEQUENCE doctors_id_seq RESTART WITH 1").executeUpdate();
    }

    private void clearUsers() {
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        entityManager.createNativeQuery("ALTER SEQUENCE users_id_seq RESTART WITH 1").executeUpdate();
    }
}
