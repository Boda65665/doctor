package com.kafka1.demo.Services.Tests.ControllerService;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Services.Controller.AuthControllerService;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Services.EmailService;
import com.kafka1.demo.Services.JwtService;
import com.kafka1.demo.Services.TestHelper.ControllerService.AuthControllerServiceTH;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import java.util.stream.Stream;
import static javax.security.auth.callback.ConfirmationCallback.OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.beans.factory.config.YamlProcessor.MatchStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.*;

class AuthControllerServiceTest {
    @InjectMocks
    private AuthControllerService authControllerService;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private UserDbService userDBService;
    @Mock
    private JwtService jwtService;
    @Mock
    private EmailService emailService;
    private AuthControllerServiceTH authControllerServiceTH;
    private static final String TEST_MAIL = "test_email";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authControllerServiceTH = new AuthControllerServiceTH(authControllerService, bindingResult, userDBService, jwtService,emailService, TEST_MAIL);
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForCompleteRegister")
    @DisplayName("Complete Register - Parameterized Test")
    void completeRegisterParameterizedTest(boolean invalidInputData, boolean userExists, HttpStatus expectedStatus) {
        HttpResponseBody response = authControllerServiceTH.completeRegister(invalidInputData, userExists);
        assertEquals(expectedStatus, response.getHttpStatus());
    }

    private static Stream<Arguments> provideTestCasesForCompleteRegister() {
        return Stream.of(
                Arguments.of(false, false, OK),          // Успешная регистрация
                Arguments.of(true, false, BAD_REQUEST), // Ошибка валидации
                Arguments.of(false, true, CONFLICT)    // Пользователь уже существует
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForLogin")
    @DisplayName("Login - Parameterized Test")
    void loginParameterizedTest(boolean invalidInputData, boolean isCorrectPassword, HttpStatus expectedStatus) {
        HttpResponseBody response = authControllerServiceTH.login(invalidInputData, isCorrectPassword);
        assertEquals(expectedStatus, response.getHttpStatus());
    }

    public static Stream<Arguments> provideTestCasesForLogin() {
        return Stream.of(
                Arguments.of(false, true, OK),                         // Успешная авторизация
                Arguments.of(true, true, BAD_REQUEST),                // Ошибка валидации
                Arguments.of(false, false, HttpStatus.UNAUTHORIZED)  // Неверные auth данные

        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForConfirmEmail")
    @DisplayName("Confirm Email - Parameterized Test")
    void confirmEmailParameterizedTest(boolean isCorrectCode, HttpStatus expectedStatus) {
        HttpResponseBody response = authControllerServiceTH.confirmEmail(isCorrectCode);
        assertEquals(expectedStatus, response.getHttpStatus());
    }

    public static Stream<Arguments> provideTestCasesForConfirmEmail() {
        return Stream.of(
                Arguments.of(true, OK),            // Введен корректный код
                Arguments.of(false, BAD_REQUEST)  // Введен не корректный код
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForResendCode")
    @DisplayName("Resend Code - Parameterized Test")
    void resendCodeParameterizedTest(boolean isResendEmailAllowed, HttpStatus expectedStatus) {
        HttpResponseBody response = authControllerServiceTH.resendCode(isResendEmailAllowed);
        assertEquals(expectedStatus, response.getHttpStatus());
    }

    public static Stream<Arguments> provideTestCasesForResendCode() {
        return Stream.of(
                Arguments.of(true, OK),                  // Введен корректный код
                Arguments.of(false, TOO_MANY_REQUESTS)  // Введен не корректный код
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForResetPassword")
    @DisplayName("Reset Password - Parameterized Test")
    void resetPasswordParameterizedTest(String email,boolean isExistsUser, boolean isResendAllowed, HttpStatus expectedStatus) {
        HttpResponseBody response = authControllerServiceTH.resendPassword(email, isExistsUser, isResendAllowed);
        assertEquals(expectedStatus, response.getHttpStatus());
    }

    public static Stream<Arguments> provideTestCasesForResetPassword() {
        return Stream.of(
                Arguments.of(null, true, true, NOT_FOUND),                  // email is null
                Arguments.of(TEST_MAIL, false, true, NOT_FOUND),           // user is not exists
                Arguments.of(TEST_MAIL, true, false, TOO_MANY_REQUESTS),  // resend password reset link once every 2 minutes
                Arguments.of(TEST_MAIL, true, true, OK)                  // Password reset link sent successfully
        );
    }
}
