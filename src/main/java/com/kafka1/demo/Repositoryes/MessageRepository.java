package com.kafka1.demo.Repositoryes;

import com.kafka1.demo.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {

}
