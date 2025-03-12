package com.kafka1.demo.Repositoryes;

import com.kafka1.demo.Entity.Chat;
import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    Chat findByUserAndDoctor(User user, Doctor doctor);
}
