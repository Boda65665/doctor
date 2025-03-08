package com.kafka1.demo.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;


@Slf4j
@Service
public class EmailService {
    private final EmailSenderService emailSender;
    @Value("${hostname}")
    private String hostname;

    public EmailService(EmailSenderService emailSender) {
        this.emailSender = emailSender;
    }

    private void sendMessage(String to, String subject, String messageString){
        emailSender.sendMessage(to,subject,messageString);
    }

    public int sendCode(String to){
        int randomCode = generateRandomCode();
        sendMessage(to, "Код потверждения", "Your code is " + randomCode);
        return randomCode;
    }

    public int generateRandomCode() {
        Random random = new Random();

        int min = 100001;
        int max = 999999;
        return random.nextInt(max - min + 1) + min;
    }

    public String sendUrlForReSetPassword(String to){
        String key = generateRandomSecretKey();
        sendMessage(to, "Сслка для востановления" , String.format("Your url: http://%s/auth/re-set-password/%s", hostname, key));
        return key;
    }

    public String generateRandomSecretKey(){
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(30);
        for (int i = 0; i < 30; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
