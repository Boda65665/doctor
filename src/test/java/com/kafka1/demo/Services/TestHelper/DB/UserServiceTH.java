package com.kafka1.demo.Services.TestHelper.DB;

import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Security.SHA256;
import com.kafka1.demo.Services.DB.UserDbService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Component
public class UserServiceTH {
    @Autowired
    private UserDbService userDbService;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public int createUserWithRandomEmailKey(){
        User user = new User();

        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000);
        user.setEmailCode(SHA256.hash(String.valueOf(randomNumber)));
        userDbService.save(user);
        return randomNumber;
    }

    public String createUserWithRandomPassword(){
        User user = createUser();

        String randomString = generateRandomString();
        user.setPassword(SHA256.hash(randomString));
        userDbService.save(user);
        return randomString;
    }

    private String generateRandomString() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }
        return stringBuilder.toString();
    }

    public User createUser(){
        return createUser("test_email");
    }

    public User createUser(String email){
        User user = new User();
        user.setEmail(email);
        userDbService.save(user);
        return userDbService.findUserByEmail(email);
    }

    public String createUserWithRandomSecretKey(){
        User user = new User();
        user.setId(1);

        String randomString = generateRandomString();
        user.setSecretKey(SHA256.hash(randomString));
        userDbService.save(user);
        return randomString;
    }

    public User createUserWithTimeLastSendCode(String email,LocalDateTime localDateTime){
        User user = createUser(email);
        user.setTimeLastSendCode(localDateTime);
        userDbService.save(user);
        return user;
    }

    public void createUserWithEmailCode(String email,String emailCode){
        User user = createUser(email);
        user.setEmailCode(SHA256.hash(emailCode));
        userDbService.save(user);
    }

    public void createUserWithPassword(String email,String password){
        User user = createUser(email);
        user.setPassword(SHA256.hash(password));
        userDbService.save(user);
    }
}
