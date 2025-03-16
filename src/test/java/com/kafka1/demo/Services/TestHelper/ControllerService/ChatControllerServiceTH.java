package com.kafka1.demo.Services.TestHelper.ControllerService;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Body.MessageBody;
import com.kafka1.demo.Entity.Chat;
import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Models.Role;
import com.kafka1.demo.Services.Controller.ChatControllerService;
import com.kafka1.demo.Services.DB.ChatDbService;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.UserDbService;
import org.springframework.validation.BindingResult;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class ChatControllerServiceTH {
    private final ChatControllerService chatControllerService;
    private final DoctorDbService doctorDbService;
    private final UserDbService userDbService;
    private final ChatDbService chatDbService;
    private final BindingResult bindingResult;

    private final String TEST_MAIL = "test_mail";

    public ChatControllerServiceTH(ChatControllerService chatControllerService, DoctorDbService doctorDbService, UserDbService userDbService, ChatDbService chatDbService, BindingResult bindingResult) {
        this.chatControllerService = chatControllerService;
        this.doctorDbService = doctorDbService;
        this.userDbService = userDbService;
        this.chatDbService = chatDbService;
        this.bindingResult = bindingResult;
    }

    public HttpResponseBody sendMessage(boolean hasErrors, boolean existsRecipient, int idSender, int idRecipient, Role roleSender, Role roleRecipient) {
        mockBindingResult(hasErrors);
        mockExistsUser(idRecipient, existsRecipient);
        mockFindUserByEmail(idSender, roleSender);
        mockFindUserById(idRecipient, roleRecipient);
        mockFindDoctorByUser();
        mockFindByUserAndDoctor();

        MessageBody messageBody = new MessageBody();
        messageBody.setIdRecipient(idRecipient);

        return chatControllerService.sendMessage(TEST_MAIL, messageBody, bindingResult);
    }

    private void mockFindByUserAndDoctor() {
        Chat chat = new Chat();
        chat.setId(1);

        when(chatDbService.findByUserAndDoctor(anyInt(), anyInt())).thenReturn(chat);
    }

    private void mockBindingResult(boolean hasErrors) {
        when(bindingResult.hasErrors()).thenReturn(hasErrors);
    }

    private void mockExistsUser(int idRecipient, boolean existsRecipient) {
        when(userDbService.existsById(idRecipient)).thenReturn(existsRecipient);
    }

    private void mockFindUserByEmail(int idSender, Role role) {
        User user = generateUserWithIdAndRole(idSender, role);
        when(userDbService.findUserByEmail(TEST_MAIL)).thenReturn(user);
    }

    private User generateUserWithIdAndRole(int id, Role role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);

        return user;
    }

    private void mockFindUserById(int idRecipient, Role role) {
        User user = generateUserWithIdAndRole(idRecipient, role);
        when(userDbService.findUserById(idRecipient)).thenReturn(user);
    }

    private void mockFindDoctorByUser() {
        Doctor doctor = new Doctor();
        doctor.setId(1);

        when(doctorDbService.findDoctorByUser(anyInt())).thenReturn(doctor);
    }
}
