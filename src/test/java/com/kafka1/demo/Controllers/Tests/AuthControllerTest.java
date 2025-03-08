package com.kafka1.demo.Controllers.Tests;

import com.kafka1.demo.Controllers.AuthController;
import com.kafka1.demo.Controllers.TestHelper.AuthControllerTH;
import com.kafka1.demo.Services.ApiService.AuthControllerService;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

class AuthControllerTest {
    @InjectMocks
    private AuthController authController;
    @Mock
    private UserDbService userDbService;
    @Mock
    private AuthControllerService authControllerService;
    @Mock
    private Model model;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private JwtService jwtService;

    private AuthControllerTH authControllerTH;

    private final String TEST_MAIL = "test_mail";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authControllerTH = new AuthControllerTH(authController, userDbService, authControllerService, model, bindingResult, httpServletResponse, httpServletRequest, TEST_MAIL);
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForReg")
    @DisplayName("Reg - Parameterized Test")
    void reg(HttpStatus status, String exceptedResult) {
        String result = authControllerTH.reg(status);
        assertEquals(exceptedResult, result);
    }

    public static Stream<Arguments> provideTestCasesForReg() {
        String errorResult = "auth/reg";
        return Stream.of(
                Arguments.of(BAD_REQUEST, errorResult),
                Arguments.of(CONFLICT, errorResult),
                Arguments.of(OK, "redirect:/auth/email_confirmation")
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForEmailConfirmation")
    @DisplayName("Email Confirmation - Parameterized Test")
    void emailConfirmation(HttpStatus status, String exceptedResult) {
        String result = authControllerTH.emailConfirmation(status);
        assertEquals(exceptedResult, result);
    }

    public static Stream<Arguments> provideTestCasesForEmailConfirmation() {
        return Stream.of(
                Arguments.of(BAD_REQUEST, "auth/email_confirmation"),
                Arguments.of(OK, "redirect:/")
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForResendCodeForEmailConfirmation")
    @DisplayName("Resend Code For Email Confirmation - Parameterized Test")
    void resendCodeForEmailConfirmation(HttpStatus status, String exceptedResult) {
        String result = authControllerTH.resendCodeForEmailConfirmation(status);
        assertEquals(exceptedResult, result);
    }

    public static Stream<Arguments> provideTestCasesForResendCodeForEmailConfirmation() {
        return Stream.of(
                Arguments.of(TOO_MANY_REQUESTS, "auth/email_confirmation"),
                Arguments.of(OK, "redirect:/auth/email_confirmation")
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForLogin")
    @DisplayName("Login - Parameterized Test")
    void login(HttpStatus status, String exceptedResult) {
        String result = authControllerTH.login(status);
        assertEquals(exceptedResult, result);
    }

    public static Stream<Arguments> provideTestCasesForLogin() {
        String errorResult = "auth/login";
        return Stream.of(
                Arguments.of(BAD_REQUEST, errorResult),
                Arguments.of(UNAUTHORIZED, errorResult),
                Arguments.of(OK, "redirect:/")
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForResetPassword")
    @DisplayName("Reset Password - Parameterized Test")
    void reSetPassword(String key,boolean isCorrectKey,String password,String exceptedResult) {
        String result = authControllerTH.resetPassword(key, isCorrectKey, password);
        assertEquals(exceptedResult, result);
    }

    public static Stream<Arguments> provideTestCasesForResetPassword() {
        String mock_key = "incorrect_key";
        String passwordLessThanThreeCharacters = "ab";
        String passwordMoreTwoCharacters = "abc";
        return Stream.of(
                Arguments.of(null, true, null, "redirect:/auth/re-set-password"), //key is null
                Arguments.of(mock_key, false, null, "redirect:/auth/re-set-password"), //key is incorrect
                Arguments.of(mock_key, true, null, "/auth/re_set_password"), // password is null
                Arguments.of(mock_key, true, passwordLessThanThreeCharacters, "/auth/re_set_password"), //password is short
                Arguments.of(mock_key, true, passwordMoreTwoCharacters, "redirect:/auth/login") //Successfully
        );
    }
}