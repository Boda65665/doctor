package com.kafka1.demo.Controllers.Tests;

import com.kafka1.demo.Controllers.MeController;
import com.kafka1.demo.Controllers.TestHelper.MeControllerTH;
import com.kafka1.demo.Services.Controller.SessionControllerService;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.SessionDbService;
import com.kafka1.demo.Services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import java.util.stream.Stream;
import static javax.security.auth.callback.ConfirmationCallback.OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.beans.factory.config.YamlProcessor.MatchStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.GONE;

class MeControllerTest {
    @InjectMocks
    private MeController meController;
    @Mock
    private SessionControllerService sessionControllerService;
    @Mock
    private SessionDbService sessionDbService;
    @Mock
    private DoctorDbService doctorDbService;
    @Mock
    private JwtService jwtService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Model model;

    private MeControllerTH meControllerTH;

    private static final int TEST_SESSION_ID = 1;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            meControllerTH = new MeControllerTH(meController, sessionControllerService, sessionDbService, doctorDbService, request, model, TEST_SESSION_ID);
        }

        @Test
        void editStatusActivateSession() {
        }

        @ParameterizedTest
        @MethodSource("provideTestCasesForEditStatusActivateSession")
        @DisplayName("Reg - Parameterized Test")
        void editStatusActivateSession(boolean isNullSessionDTO, boolean isNullRequestingDoctor, boolean isDoctorNotOwnSession, boolean isPrefixNull, HttpStatus status , String exceptedResult) {
            String result = meControllerTH.editStatusActivateSession(isNullSessionDTO, isNullRequestingDoctor, isDoctorNotOwnSession, isPrefixNull, status);
            assertEquals(exceptedResult, result);
        }

        public static Stream<Arguments> provideTestCasesForEditStatusActivateSession() {
            return Stream.of(
                    Arguments.of(true, false, true, false, null,"redirect:/"), //session is null
                    Arguments.of(false, true, true, false, null, "redirect:/"), //requestingDoctor is null
                    Arguments.of(false, false, true, false, null, "redirect:/"),    //doctor is not own session
                    Arguments.of(false, false, false, true, null, "redirect:/sessions/"+TEST_SESSION_ID),   //prefix is null
                    Arguments.of(false, false, false, false, GONE, "/me/session"),
                    Arguments.of(false, false, false, false, CONFLICT, "/me/session"),
                    Arguments.of(false, false, false, false, NOT_FOUND, "/me/session"),
                    Arguments.of(false, false, false, false, NOT_FOUND, "/me/session"),
                    Arguments.of(false, false, false, false, OK, "/me/session") //Successfully
            );
    }
}