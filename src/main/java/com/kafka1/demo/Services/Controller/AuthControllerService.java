package com.kafka1.demo.Services.Controller;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Form.LoginForm;
import com.kafka1.demo.Form.RegisterForm;
import com.kafka1.demo.Models.Role;
import com.kafka1.demo.Security.SHA256;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Services.EmailService;
import com.kafka1.demo.Services.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;

@Service
public class AuthControllerService {
    private final UserDbService userDBService;
    private final EmailService emailService;
    private final JwtService jwtService;

    public AuthControllerService(UserDbService userDBService, EmailService emailService, JwtService jwtService) {
        this.userDBService = userDBService;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    public HttpResponseBody completeRegister(RegisterForm registerForm, BindingResult bindingResult){
        HttpResponseBody registerResponse = new HttpResponseBody();
        if (bindingResult.hasErrors()){
            registerResponse.setHttpStatus(BAD_REQUEST);
            registerResponse.setMessage("bad request");
        }
        else if (userDBService.existsByEmail(registerForm.getEmail())) {
            registerResponse.setHttpStatus(CONFLICT);
            registerResponse.setMessage("Пользователь с таким адресом уже существует");
        } else {
            int code = emailService.sendCode(registerForm.getEmail());
            User newUser = new User(registerForm.getEmail(),SHA256.hash(registerForm.getPassword()), Role.UNVERIFIED,registerForm.getLastName(),registerForm.getFirstName(),SHA256.hash(String.valueOf(code)), LocalDateTime.now(),0);
            userDBService.save(newUser);
            registerResponse.setHttpStatus(OK);
            registerResponse.setMessage("Успешно");
            registerResponse.putData("JWT",jwtService.generateToken(registerForm.getEmail()));
        }
        return registerResponse;
    }

    public HttpResponseBody confirmEmail(String email,String confirmCode){
        HttpResponseBody responseBody = new HttpResponseBody();
        User user = userDBService.findUserByEmail(email);
        if (userDBService.isCorrectInputValidEmailCode(user.getId(),confirmCode)){
            user.setRole(Role.USER);
            user.setEmailCode(generateNewHashedEmailCode());
            userDBService.update(user);
            responseBody.setHttpStatus(OK);
            responseBody.setMessage("Успешно");
        }
        else {
            responseBody.setHttpStatus(BAD_REQUEST);
            responseBody.setMessage("Неверный код подтверждения");
        }
        return responseBody;
    }

    private String generateNewHashedEmailCode() {
        return SHA256.hash(String.valueOf(emailService.generateRandomCode()));
    }

    public HttpResponseBody resendCode(String email){
        HttpResponseBody responseBody = new HttpResponseBody();
        User user = userDBService.findUserByEmail(email);
        if (userDBService.isResendEmailAllowed(email)){
            resendCode(user);
            responseBody.setHttpStatus(OK);
            responseBody.setMessage("Успешно");
        }
        else {
            responseBody.setHttpStatus(TOO_MANY_REQUESTS);
            responseBody.setMessage("Сообщение повторный раз запрашивать можно раз в 2 минуты");
        }
        return responseBody;
    }

    private void resendCode(User user) {
        int code = emailService.sendCode(user.getEmail());
        user.setEmailCode(SHA256.hash(String.valueOf(code)));
        user.setTimeLastSendCode(LocalDateTime.now());
        userDBService.save(user);
    }

    public HttpResponseBody login(LoginForm loginForm, BindingResult bindingResult){
        HttpResponseBody loginResponseBody = new HttpResponseBody();
        if (bindingResult.hasErrors()){
            loginResponseBody.setHttpStatus(BAD_REQUEST);
            loginResponseBody.setMessage("bad request");
            return loginResponseBody;
        }
        String email = loginForm.getEmail();
        if (isCorrectAuthData(email,loginForm.getPassword())){
            loginResponseBody.setHttpStatus(OK);
            loginResponseBody.setMessage("Успешно");
            loginResponseBody.putData("token",jwtService.generateToken(email));
            return loginResponseBody;
        }
        loginResponseBody.setHttpStatus(UNAUTHORIZED);
        loginResponseBody.setMessage("Не верная почта или пароль");
        return loginResponseBody;
    }

    private boolean isCorrectAuthData(String email, String password) {
        return userDBService.existsByEmail(email) && userDBService.isCorrectPassword(email, password);
    }

    public HttpResponseBody resetPassword(String email){
        HttpResponseBody responseBody = new HttpResponseBody();
        if (email == null || !userDBService.existsByEmail(email)) {
            responseBody.setHttpStatus(NOT_FOUND);
            responseBody.setMessage("Пользователь с таким email не найден");
            return responseBody;
        }
        if (!userDBService.isResendEmailAllowed(email)){
            responseBody.setMessage("Сообщение повторный раз запрашивать можно раз в 2 минуты");
            responseBody.setHttpStatus(TOO_MANY_REQUESTS);
            return responseBody;
        }
        responseBody.setHttpStatus(OK);
        responseBody.setMessage("Успешно");
        String secretKey = sendUrlForResetPassword(email);
        updateUserSecretKey(email,secretKey);
        return responseBody;
    }

    private void updateUserSecretKey(String email, String secretKey) {
        User user = userDBService.findUserByEmail(email);
        user.setSecretKey(SHA256.hash(secretKey));
        user.setTimeLastSendCode(LocalDateTime.now());
        userDBService.save(user);
    }

    private String sendUrlForResetPassword(String email) {
        return emailService.sendUrlForReSetPassword(email);
    }
}
