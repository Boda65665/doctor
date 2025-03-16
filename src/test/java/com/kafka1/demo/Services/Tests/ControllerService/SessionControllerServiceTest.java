package com.kafka1.demo.Services.Tests.ControllerService;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.DTO.SessionDTO;
import com.kafka1.demo.Models.TypeSession;
import com.kafka1.demo.Services.Controller.SessionControllerService;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.SessionDbService;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Services.TestHelper.ControllerService.SessionControllerServiceTH;
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

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SessionControllerServiceTest {
    @InjectMocks
    SessionControllerService sessionControllerService;
    @Mock
    UserDbService userDbService;
    @Mock
    SessionDbService sessionDbService;
    @Mock
    private DoctorDbService doctorDbService;
    private SessionControllerServiceTH sessionServiceTH;

    private static final int TEST_USER_ID = 1;
    private static final String TEST_MAIL = "test_email";

    private static final String TEST_MAIL_TWO = "test_mail_two";
    private static final int TEST_USER_ID_TWO = 2;

    private static final int TEST_SESSION_ID = 1;

    private static final HttpStatus OK = HttpStatus.OK;
    private static final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;
    private static final HttpStatus CONFLICT = HttpStatus.CONFLICT;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sessionServiceTH = new SessionControllerServiceTH(sessionControllerService, userDbService, sessionDbService, doctorDbService, TEST_USER_ID, TEST_MAIL, TEST_SESSION_ID, TEST_MAIL_TWO, TEST_USER_ID_TWO);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("get session list")
    void getMeSession() {
        HttpResponseBody httpResponseBody = sessionServiceTH.getMeSession(TypeSession.OLD);
        List<SessionDTO> sessionDTOs = (List<SessionDTO>) httpResponseBody.getData().get("sessions");

        assertNotNull(httpResponseBody);
        assertEquals(OK, httpResponseBody.getHttpStatus());
        assertNotNull(sessionDTOs);
    }


    @ParameterizedTest
    @MethodSource("provideTestCasesForGetSessionById")
    @DisplayName("Get Session By Id - Parameterized Test")
    void getSessionById(String email, int id, HttpStatus exceptedStatus) {
        HttpResponseBody httpResponseBody = sessionServiceTH.getSessionById(email, id);
        assertEquals(exceptedStatus, httpResponseBody.getHttpStatus());
    }

    public static Stream<Arguments> provideTestCasesForGetSessionById() {
        final int incorrectSessionId = 2;
        return Stream.of(
                Arguments.of(TEST_MAIL, TEST_SESSION_ID, OK),              // Успешное получения сесии
                Arguments.of(TEST_MAIL_TWO, TEST_SESSION_ID, HttpStatus.FORBIDDEN),  // Сессия не принадлежит пользователю
                Arguments.of(TEST_MAIL, incorrectSessionId, HttpStatus.NOT_FOUND)    // Данной сесии не существует
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForEditStatusActivateSession")
    @DisplayName("Get Session By Id - Parameterized Test")
    void editStatusActivateSession(LocalTime timeStartSession, LocalTime timeEndSession, LocalTime timeReallyStart, String sessionAction, HttpStatus exceptedStatus) {
        HttpResponseBody httpResponseBody = sessionServiceTH.editStatusActivateSession(timeStartSession, timeEndSession, timeReallyStart, sessionAction);
        assertEquals(exceptedStatus, httpResponseBody.getHttpStatus());
    }

    public static Stream<Arguments> provideTestCasesForEditStatusActivateSession() {
        LocalTime timeBeforeNow = LocalTime.now().minusMinutes(1);
        LocalTime timeAfterNow = LocalTime.now().plusMinutes(1);

        String actionStart = "start";
        String actionStop = "actionStop";

        LocalTime testPlug = LocalTime.now();
        return Stream.of(
                Arguments.of(null, timeBeforeNow, null, actionStart, HttpStatus.GONE),                  // Срок действия сесии истек
                Arguments.of(timeAfterNow, testPlug, testPlug, actionStart, BAD_REQUEST),           // сессию можно начать только после timeAfterNow
                Arguments.of(timeBeforeNow, testPlug, testPlug, actionStart, CONFLICT),                       // Сессия уже запущена
                Arguments.of(timeBeforeNow, timeAfterNow, null, actionStart, OK),                  // Успешный запуск сесии

                Arguments.of(testPlug, testPlug, null, actionStop, CONFLICT),                    // Сессия уже остановлена
                Arguments.of(testPlug, testPlug, LocalTime.now(), actionStop, OK)               // Успешная остановка сесии
        );
    }
}
