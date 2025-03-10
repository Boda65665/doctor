package com.kafka1.demo.Services.TestHelper.DB;

import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.Message;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Services.DB.MessageDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageServiceTH {
    @Autowired
    private DoctorServiceTH doctorServiceTH;
    @Autowired
    private UserServiceTH userServiceTH;
    @Autowired
    private MessageDbService messageDbService;

    public void save(){
        Doctor doctor = doctorServiceTH.createDoctor();
        User user = userServiceTH.createUser();


        Message message = new Message(user, doctor, "text", user);

        messageDbService.save(message);
    }
}
