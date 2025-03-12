package com.kafka1.demo.Services.DB;

import com.kafka1.demo.Entity.Chat;
import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.Message;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Repositoryes.ChatRepository;
import com.kafka1.demo.Repositoryes.DoctorRepository;
import com.kafka1.demo.Repositoryes.MessageRepository;
import com.kafka1.demo.Repositoryes.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ChatDbService {
    private final ChatRepository chatRepository;
    private final UserDbService userDbService;
    private final DoctorDbService doctorDbService;

    public ChatDbService(ChatRepository chatRepository, UserDbService userDbService, DoctorDbService doctorDbService) {
        this.chatRepository = chatRepository;

        this.userDbService = userDbService;
        this.doctorDbService = doctorDbService;
    }

    public void save(Chat chat) {
        chatRepository.save(chat);
    }

    public Chat findByUserAndDoctor(int userId, int doctorId) {
        User user = userDbService.findUserById(userId);
        Doctor doctor = doctorDbService.findDoctorById(doctorId);

        if (user == null || doctor == null) return null;

        return chatRepository.findByUserAndDoctor(user, doctor);
    }
}
