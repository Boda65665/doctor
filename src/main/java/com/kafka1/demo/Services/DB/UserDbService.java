package com.kafka1.demo.Services.DB;

import com.kafka1.demo.Converters.UserConverter;
import com.kafka1.demo.DTO.UserDTO;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Repositoryes.UserRepository;
import com.kafka1.demo.Security.SHA256;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserDbService {
    private static final long TIME_BETWEEN_RESEND = 2;
    private final UserConverter userConverter = new UserConverter();
    private final UserRepository userRepository;

    public UserDbService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User newUser){
        userRepository.save(newUser);
    }

    public void update(User user){
        userRepository.save(user);
    }

    public UserDTO findUserDtoByEmail(String email){
        User user = findUserByEmail(email);
        if (user!=null) return userConverter.userToUserDTO(user);
        return null;
    }

    public User findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User findUserById(int id){
        return userRepository.findById(id);
    }

    public boolean isCorrectInputValidEmailCode(int userId,String inputCode){
        User user = findUserById(userId);
        String correctCode = user.getEmailCode();
        return (inputCode!=null && correctCode!=null) && correctCode.equals(SHA256.hash(inputCode));
    }

    public boolean isResendEmailAllowed(String email) {
        User user = findUserByEmail(email);
        return user.getTimeLastSendCode().plusMinutes(TIME_BETWEEN_RESEND).isBefore(LocalDateTime.now());
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsById(int id) {
        return userRepository.existsById(id);
    }

    public boolean isCorrectPassword(String email, String inputPassword) {
        User user = findUserByEmail(email);
        return user.getPassword().equals(SHA256.hash(inputPassword));
    }

    public User findUserBySecretKey(String key) {
        return userRepository.findBySecretKey(SHA256.hash(key));
    }
}
