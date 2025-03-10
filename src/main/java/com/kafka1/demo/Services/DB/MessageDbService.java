package com.kafka1.demo.Services.DB;

import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.Message;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Repositoryes.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageDbService {
    private final MessageRepository messageRepository;

    public MessageDbService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void save(Message message) {
        messageRepository.save(message);
    }
}
