package com.kafka1.demo.Services.Controller;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Body.MessageBody;
import com.kafka1.demo.Entity.Chat;
import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.Message;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Models.Role;
import com.kafka1.demo.Services.DB.ChatDbService;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.UserDbService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class ChatControllerService {
    private final UserDbService userDbService;
    private final DoctorDbService doctorDbService;
    private final ChatDbService chatDbService;

    public ChatControllerService(UserDbService userDbService, DoctorDbService doctorDbService, ChatDbService chatDbService) {
        this.userDbService = userDbService;
        this.doctorDbService = doctorDbService;
        this.chatDbService = chatDbService;
    }

    public HttpResponseBody sendMessage(String email, MessageBody messageBody, BindingResult bindingResult) {
        HttpResponseBody response = new HttpResponseBody();
        HttpResponseBody errorResponse = validSendMessage(response, messageBody, email, bindingResult);
        if (errorResponse != null) return errorResponse;

        User owner = userDbService.findUserByEmail(email);

        User sender = userDbService.findUserByEmail(email);
        User recipient= userDbService.findUserByEmail(email);
        Chat chat = getChatBySenderAndRecipient(sender, recipient);

        Message message = new Message(messageBody.getText(), owner, chat);
        chatDbService.addMessage(message, chat.getId());

        response.setHttpStatus(HttpStatus.OK);
        response.setMessage("Message sent");
        return response;
    }

    private Chat getChatBySenderAndRecipient(User sender, User recipient) {
        User user;
        Doctor doctor;

        if (isUser(sender)) {
            user = sender;
            doctor = doctorDbService.findDoctorByUser(recipient.getId());
        }
        else {
            user = recipient;
            doctor = doctorDbService.findDoctorByUser(sender.getId());
        }

        if (chatDbService.existsByUserAndDoctor(user, doctor)) return chatDbService.findByUserAndDoctor(user.getId(), doctor.getId());
        return createChat(user, doctor);
    }

    private Chat createChat(User user, Doctor doctor) {
        Chat chat = new Chat(doctor, user);
        chatDbService.save(chat);

        return chatDbService.findByUserAndDoctor(user.getId(), doctor.getId());
    }

    private HttpResponseBody validSendMessage(HttpResponseBody response, MessageBody messageBody, String email, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return createResponse(response, "Some required fields are empty", HttpStatus.BAD_REQUEST);

        if (isNotExistsRecipient(messageBody.getIdRecipient())) return createResponse(response, "Recipient is not exists", HttpStatus.NOT_FOUND);

        User sender = userDbService.findUserByEmail(email);
        if (isSendingToSelf(sender.getId(), messageBody.getIdRecipient())) return createResponse(response, "Recipient is not exists", HttpStatus.FORBIDDEN);

        User recipient = userDbService.findUserById(messageBody.getIdRecipient());
        if (isInValidMessageExchange(sender, recipient)) return createResponse(response, "Recipient is invalid", HttpStatus.FORBIDDEN);
        return null;
    }

    private HttpResponseBody createResponse(HttpResponseBody response, String message, HttpStatus status) {
        response.setMessage(message);
        response.setHttpStatus(status);
        return response;
    }

    private boolean isNotExistsRecipient(int idRecipient) {
        return !userDbService.existsById(idRecipient);
    }

    private boolean isSendingToSelf(int idSender, int idRecipient) {
        return idSender == idRecipient;
    }

    private boolean isInValidMessageExchange(User sender, User recipient) {
        if (isUser(sender)){
            return isUser(recipient);
        }
        return !isUser(sender) && !isUser(recipient);
    }

    private boolean isUser(User sender) {
        return sender.getRole() == Role.USER;
    }

}