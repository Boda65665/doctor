package com.kafka1.demo.Repositoryes;

import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.Message;
import com.kafka1.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Integer> {
    List<Message> findByUserAndDoctorOrderById(User user, Doctor doctor);
}
