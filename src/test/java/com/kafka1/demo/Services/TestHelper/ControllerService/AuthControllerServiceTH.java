package com.kafka1.demo.Services.TestHelper.ControllerService;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Form.LoginForm;
import com.kafka1.demo.Form.RegisterForm;
import com.kafka1.demo.Services.Controller.AuthControllerService;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Services.EmailService;
import com.kafka1.demo.Services.JwtService;
import org.springframework.validation.BindingResult;
import static org.mockito.Mockito.*;

public class AuthControllerServiceTH {
    private final AuthControllerService authControllerService;
    private final BindingResult bindingResult;
    private final UserDbService userDBService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final String TEST_MAIL;
    private final String TEST_PASSWORD = "test_password";

    public AuthControllerServiceTH(AuthControllerService authControllerService, BindingResult bindingResult, UserDbService userDBService, JwtService jwtService, EmailService emailService, String testMail) {
        this.authControllerService = authControllerService;
        this.bindingResult = bindingResult;
        this.userDBService = userDBService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        TEST_MAIL = testMail;
    }

    public HttpResponseBody completeRegister(boolean validInputData, boolean userExists) {
        RegisterForm registerForm = new RegisterForm(TEST_MAIL, "testPassword", "lastName", "firstName");
        mockStatesOfExistence(userExists);
        mockBindingResult(validInputData);
        mockJWT();
        return authControllerService.completeRegister(registerForm, bindingResult);
    }

    private void mockBindingResult(boolean hasErrors) {
        when(bindingResult.hasErrors()).thenReturn(hasErrors);
    }

    private void mockJWT(){
        when(jwtService.generateToken(TEST_MAIL)).thenReturn("mocked_jwt_token");
    }

    private void mockStatesOfExistence(boolean isExists) {
        when(userDBService.existsByEmail(TEST_MAIL)).thenReturn(isExists);
    }

    public HttpResponseBody confirmEmail(boolean isCorrectCode) {
        mockFindByEmail();
        mockValidatingEmailCode(isCorrectCode);

        String code = "mocked_code";
        return authControllerService.confirmEmail(TEST_MAIL,code);
    }

    private void mockFindByEmail() {
        User mockUser = new User();
        when(userDBService.findUserByEmail(TEST_MAIL)).thenReturn(mockUser);
    }

    private void mockValidatingEmailCode(boolean isCorrectCode) {
        when(userDBService.isCorrectInputValidEmailCode(anyInt(),anyString())).thenReturn(isCorrectCode);
    }

    public HttpResponseBody resendCode(boolean isAllowed) {
        mockFindByEmail();
        mockIsResendEmailAllowed(isAllowed);
        return authControllerService.resendCode(TEST_MAIL);
    }

    private void mockIsResendEmailAllowed(boolean isAllowed) {
        when(userDBService.isResendEmailAllowed(TEST_MAIL)).thenReturn(isAllowed);
    }

    public HttpResponseBody login(boolean validInputData, boolean isCorrectPassword) {
        LoginForm loginForm = new LoginForm(TEST_MAIL,TEST_PASSWORD);
        mockBindingResult(validInputData);
        mockStatesOfExistence(true);
        mockIsCorrectPassword(isCorrectPassword);

        return authControllerService.login(loginForm,bindingResult);
    }

    private void mockIsCorrectPassword(boolean isCorrectPassword) {
        when(userDBService.isCorrectPassword(TEST_MAIL,TEST_PASSWORD)).thenReturn(isCorrectPassword);
    }

    public HttpResponseBody resendPassword(String email,boolean isExistsUser,boolean isResendAllowed){
        mockStatesOfExistence(isExistsUser);
        mockIsResendEmailAllowed(isResendAllowed);
        mockSendUrlForReSetPassword();
        mockFindByEmail();

        return authControllerService.resetPassword(email);
    }

    private void mockSendUrlForReSetPassword() {
        when(emailService.sendUrlForReSetPassword(TEST_MAIL)).thenReturn("mock_secret_Key");
    }
}
