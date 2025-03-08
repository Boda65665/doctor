package com.kafka1.demo.Converters;

import com.kafka1.demo.DTO.MessageDTO;
import com.kafka1.demo.Entity.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageConverter {
    private final UserConverter userConverter = new UserConverter();
    private final DoctorConverter doctorConverter = new DoctorConverter();

    private MessageDTO messageToMessageDTO(Message message){
        MessageDTO messageConvert = new MessageDTO();
        messageConvert.setText(message.getText());
        messageConvert.setOwner(message.getOwner());
        messageConvert.setDoctorDTO(doctorConverter.doctorToDoctorDto(message.getDoctor()));
        messageConvert.setUserDTO(userConverter.userToUserDTO(message.getUser()));
        return messageConvert;
    }

    public List<MessageDTO> allMessageToMessageDTO(List<Message> messages){
        List<MessageDTO> messageDTOS = new ArrayList<>();
        for (Message message : messages) {
            messageDTOS.add(messageToMessageDTO(message));
        }
        return messageDTOS;
    }
}
