package com.kafka1.demo.Services.Tests.DB;

import com.kafka1.demo.DemoApplication;
import com.kafka1.demo.Repositoryes.MessageRepository;
import com.kafka1.demo.Services.TestHelper.DB.CleanerBD;
import com.kafka1.demo.Services.TestHelper.DB.MessageServiceTH;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(classes = {DemoApplication.class})
public class MessageDbServiceTest {
    @Autowired
    private CleanerBD cleanerBd;
    @Autowired
    private MessageServiceTH messageServiceTH;
    @Autowired
    private MessageRepository messageRepository;

    @BeforeEach
    void clearBd() {
        cleanerBd.clearBd();
    }

    @Test
    public void save(){
        assertEquals(0, messageRepository.count());
        messageServiceTH.save();
        assertEquals(1, messageRepository.count());
    }
}
