package com.kafka1.demo.Controllers.TestHelper;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Controllers.AuthController;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Form.LoginForm;
import com.kafka1.demo.Form.RegisterForm;
import com.kafka1.demo.Services.ApiService.AuthControllerService;
import com.kafka1.demo.Services.DB.UserDbService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

public class AuthControllerTH {
    private final AuthController authController;
    private final UserDbService userDbService;
    private final AuthControllerService authControllerService;
    private final Model model;
    private final BindingResult bindingResult;
    private final HttpServletResponse httpServletResponse;
    private final HttpServletRequest httpServletRequest;
    private final String TEST_MAIL;

    public AuthControllerTH(AuthController authController, UserDbService userDbService, AuthControllerService authControllerService, Model model, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, String testMail) {
        this.authController = authController;
        this.userDbService = userDbService;
        this.authControllerService = authControllerService;
        this.model = model;
        this.bindingResult = bindingResult;
        this.httpServletResponse = httpServletResponse;
        this.httpServletRequest = httpServletRequest;
        TEST_MAIL = testMail;
    }

    public String reg(HttpStatus status) {
        RegisterForm mockRegisterForm = new RegisterForm();
        mockCompleteRegister(mockRegisterForm, status);
        return authController.reg(mockRegisterForm, bindingResult, httpServletResponse, model);
    }

    private void mockCompleteRegister(RegisterForm registerForm, HttpStatus status) {
        HttpResponseBody responseBody = createHttpResponseBody(status);
        when(authControllerService.completeRegister(
                registerForm,
                bindingResult
        )).thenReturn(responseBody);
    }

    HttpResponseBody createHttpResponseBody(HttpStatus status) {
        HttpResponseBody mockResponseBody = new HttpResponseBody();
        mockResponseBody.setHttpStatus(status);

        return mockResponseBody;
    }

    public String emailConfirmation(HttpStatus status) {
        mockConfirmEmail(status);

        return authController.emailConfirmation(httpServletRequest, model);
    }

    private void mockConfirmEmail(HttpStatus status) {
        HttpResponseBody responseBody = createHttpResponseBody(status);

        when(authControllerService.confirmEmail(
                nullable(String.class),
                nullable(String.class)
        )).thenReturn(responseBody);
    }

    public String resendCodeForEmailConfirmation(HttpStatus status) {
        mockResendCode(status);

        return authController.resendCodeForEmailConfirmation(httpServletRequest, model);
    }

    private void mockResendCode(HttpStatus status) {
        HttpResponseBody responseBody = createHttpResponseBody(status);

        when(authControllerService.resendCode(nullable(String.class))).thenReturn(responseBody);
    }

    public String login(HttpStatus status) {
        LoginForm mockLoginForm = new LoginForm();
        mockLogin(status, mockLoginForm);

        return authController.login(model, mockLoginForm, bindingResult, httpServletResponse);

    }

    private void mockLogin(HttpStatus status, LoginForm mockLoginForm) {
        HttpResponseBody responseBody = createHttpResponseBody(status);

        when(authControllerService.login(
                mockLoginForm,
                bindingResult)
        ).thenReturn(responseBody);
    }

    public String resetPassword(String key, boolean isCorrectSecretKey, String password) {

        mockFindUserBySecretKey(key, isCorrectSecretKey);
        mockRequest("password", password);

        return authController.reSetPassword(key, httpServletRequest, model);
    }

    private void mockFindUserBySecretKey(String mockSecretKey, boolean isCorrectSecretKey) {
        User user = null;
        if (isCorrectSecretKey) user = new User();

        when(userDbService.findUserBySecretKey(mockSecretKey)
        ).thenReturn(user);
    }

    private void mockRequest(String nameParam, String password) {
        when(httpServletRequest.getParameter(nameParam))
                .thenReturn(password);
    }
}
