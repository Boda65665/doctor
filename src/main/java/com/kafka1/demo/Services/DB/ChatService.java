package com.kafka1.demo.Services.DB;

import com.kafka1.demo.Converters.MessageConverter;
import com.kafka1.demo.DTO.ChatDTO;
import com.kafka1.demo.DTO.DoctorDTO;
import com.kafka1.demo.DTO.MessageDTO;
import com.kafka1.demo.DTO.UserDTO;
import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.Message;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Repositoryes.DoctorRepository;
import com.kafka1.demo.Repositoryes.MessageRepository;
import com.kafka1.demo.Repositoryes.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChatService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final MessageConverter messageConverter = new MessageConverter();

    public ChatService(MessageRepository messageRepository, UserRepository userRepository, DoctorRepository doctorRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
    }

    public void save(Message message){
        messageRepository.save(message);
    }

    public ChatDTO findByUserAndDoctorOrderById(int userId, int doctorId){
        User user = userRepository.findById(userId);
        Doctor doctor = doctorRepository.findById(doctorId);
        if (user==null || doctor==null) return null;
        List<Message> messages = messageRepository.findByUserAndDoctorOrderById(user,doctor);
        if (messages.isEmpty()) return null;
        List<MessageDTO> messageDTOS = messageConverter.allMessageToMessageDTO(messages);
        UserDTO userDTO = messageDTOS.get(0).getUserDTO();
        DoctorDTO doctorDTO = messageDTOS.get(0).getDoctorDTO();
        return new ChatDTO(messageDTOS,userDTO,doctorDTO);
    }
}
