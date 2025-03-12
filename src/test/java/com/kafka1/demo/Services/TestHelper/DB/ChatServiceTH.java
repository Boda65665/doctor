package com.kafka1.demo.Services.TestHelper.DB;

import com.kafka1.demo.Entity.Chat;
import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Services.DB.ChatDbService;
import com.kafka1.demo.Services.DB.DoctorDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatServiceTH {
    @Autowired
    private ChatDbService chatDbService;
    @Autowired
    private UserServiceTH userServiceTH;
    @Autowired
    private DoctorServiceTH doctorServiceTH;


    public void save(){
        User user = userServiceTH.createUser();
        Doctor doctor = doctorServiceTH.createDoctor();
        Chat chat = new Chat(doctor, user);

        chatDbService.save(chat);
    }
}
