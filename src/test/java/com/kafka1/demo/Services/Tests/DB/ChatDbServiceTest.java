package com.kafka1.demo.Services.Tests.DB;

import com.kafka1.demo.DemoApplication;
import com.kafka1.demo.Entity.Chat;
import com.kafka1.demo.Repositoryes.ChatRepository;
import com.kafka1.demo.Services.DB.ChatDbService;
import com.kafka1.demo.Services.TestHelper.DB.ChatServiceTH;
import com.kafka1.demo.Services.TestHelper.DB.CleanerBD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {DemoApplication.class})
public class ChatDbServiceTest {
    @Autowired
    private CleanerBD cleanerBd;
    @Autowired
    private ChatServiceTH chatServiceTH;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatDbService chatDbService;


    private final int USER_ID = 1;
    private final int DOCTOR_ID = 1;
    private final int CHAT_ID = 1;
    private final int MESSAGE_ID = 1;

    @BeforeEach
    void clearBd() {
        cleanerBd.clearBd();
    }

    @Test
    public void save(){
        assertEquals(0, chatRepository.count());
        chatServiceTH.save();
        assertEquals(1, chatRepository.count());
    }

    @Test
    public void findByUserAndDoctor(){
        Chat chat = chatDbService.findByUserAndDoctor(USER_ID, DOCTOR_ID);
        assertNull(chat);

        chatServiceTH.save();

        chat = chatDbService.findByUserAndDoctor(USER_ID, DOCTOR_ID);
        assertNotNull(chat);
    }

    @Test
    public void addMessage(){
        chatServiceTH.save();

        System.out.println(chatRepository.findAll().get(0).getId());
        Chat chat = chatRepository.findById(CHAT_ID);
        assertEquals(0, chat.getMessages().size());

        chatServiceTH.addMessage(CHAT_ID);
        chat = chatRepository.findById(CHAT_ID);

        assertEquals(1, chat.getMessages().size());
    }

    @Test
    public void removeMessage(){
        chatServiceTH.save();
        chatServiceTH.addMessage(CHAT_ID);
        Chat chat = chatRepository.findById(CHAT_ID);
        assertEquals(1, chat.getMessages().size());

        chatDbService.removeMessage(MESSAGE_ID ,CHAT_ID);
        chat = chatRepository.findById(CHAT_ID);
        assertEquals(0, chat.getMessages().size());
    }
}
