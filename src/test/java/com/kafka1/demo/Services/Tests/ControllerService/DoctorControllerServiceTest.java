package com.kafka1.demo.Services.Tests.ControllerService;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.DTO.DoctorSchedulesDTO;
import com.kafka1.demo.Models.DayOfWeek;
import com.kafka1.demo.Models.TimeInterval;
import com.kafka1.demo.Repositoryes.DoctorSchedulesRepository;
import com.kafka1.demo.Services.ApiService.DoctorControllerService;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.SessionDbService;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Services.TestHelper.ControllerService.DoctorControllerServiceTH;
import com.kafka1.demo.Zoom.ZoomApi;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

class DoctorControllerServiceTest {
    @InjectMocks
    private DoctorControllerService doctorControllerService;
    @Mock
    private DoctorDbService doctorDbService;
    @Mock
    private SessionDbService sessionDbService;
    @Mock
    private UserDbService userDbService;
    @Mock
    private ZoomApi zoomApi;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private DoctorSchedulesRepository doctorSchedulesRepository;

    private DoctorControllerServiceTH doctorControllerServiceTH;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorControllerServiceTH = new DoctorControllerServiceTH(doctorControllerService, doctorDbService, userDbService, zoomApi, bindingResult, doctorSchedulesRepository);
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForOrderConsultation")
    @DisplayName("Order Consultation - Parameterized Test")
    void orderConsultation(boolean haveFreeTime, String timeStartString, String longSessionInMinutesString, DoctorSchedulesDTO freeTime, int userBalance,
                           boolean isSuccessfulSessionCreation, HttpStatus expectedStatus) {
        HttpResponseBody responseBody = doctorControllerServiceTH.orderConsultation(haveFreeTime, timeStartString, longSessionInMinutesString, freeTime, userBalance,
                isSuccessfulSessionCreation);
        assertEquals(expectedStatus, responseBody.getHttpStatus());
    }

    public static Stream<Arguments> provideTestCasesForOrderConsultation() {
        String timeStartString = "12:00:00";
        DoctorSchedulesDTO freeTime = generateDoctorSchedulesDtoForTest();
        int userBalance = 100;
        return Stream.of(
                Arguments.of(false, null, null, null, 0, false, CONFLICT), // DoctorSchedulesDTO freeTime is null
                Arguments.of(true, null, null, freeTime, 0, false, BAD_REQUEST), // timeStartString is invalid
                Arguments.of(true, timeStartString, null, freeTime, 0, false, BAD_REQUEST), // longSessionInMinutesString is invalid
                Arguments.of(true, timeStartString, "4", freeTime, 0, false, BAD_REQUEST), // longSessionInMinutesString must be >= 5 but <= 200
                Arguments.of(true, timeStartString, "201", freeTime, 0, false, BAD_REQUEST), // longSessionInMinutesString must be >= 5 but <= 200
                Arguments.of(true, timeStartString, "60", freeTime, 0, false, BAD_REQUEST), // User Choosing Unavailable Time
                Arguments.of(true, timeStartString, "30", freeTime, 0, false, PAYMENT_REQUIRED), // User balance is insufficient
                Arguments.of(true, timeStartString, "30", freeTime, userBalance, false, BAD_GATEWAY), // Error at session creation
                Arguments.of(true, timeStartString, "30", freeTime, userBalance, true, OK) // successful order session
        );
    }

    private static DoctorSchedulesDTO generateDoctorSchedulesDtoForTest() {
        DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
        ArrayList<TimeInterval> timeIntervals = new ArrayList<>();
        timeIntervals.add(new TimeInterval(LocalTime.of(10,0), LocalTime.of(11,0)));
        timeIntervals.add(new TimeInterval(LocalTime.of(12, 0), LocalTime.of(12, 30)));

        return new DoctorSchedulesDTO(dayOfWeek, timeIntervals);
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForValidEditSchedules")
    @DisplayName("Valid Edit Schedules - Parameterized Test")
    void validEditSchedules(boolean hasErrors,boolean isNullPrefix, String timeStart, String timeEnd, String dayOfWeek, HttpStatus exceptedStatus) {
        HttpResponseBody responseBody = doctorControllerServiceTH.validEditSchedules(hasErrors, isNullPrefix, timeStart, timeEnd, dayOfWeek);

        assertEquals(exceptedStatus, responseBody.getHttpStatus());
    }

    public static Stream<Arguments> provideTestCasesForValidEditSchedules() {
        String incorrectData = "incorrect";
        return Stream.of(
                Arguments.of(true, true, incorrectData, incorrectData, incorrectData, BAD_REQUEST),
                Arguments.of(false,true, incorrectData, incorrectData, incorrectData, BAD_REQUEST),
                Arguments.of(false,false, "12:00:00", incorrectData, incorrectData, BAD_REQUEST),
                Arguments.of(false,false, "12:00:00", "12:04:00", incorrectData, BAD_REQUEST),
                Arguments.of(false,false, "12:00:00", "12:04:00", incorrectData, BAD_REQUEST),
                Arguments.of(false,false, "12:00:00", "12:05:00", incorrectData, BAD_REQUEST),
                Arguments.of(false,false, "12:00:00", "12:05:00", "1", OK));
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForValidateScheduleOverlap")
    @DisplayName("Valid Edit Schedules Overlap - Parameterized Test")
    void validScheduleOverlap(String timeStart, String timeEnd,TimeInterval timeInterval, HttpStatus exceptedStatus) {
        HttpResponseBody responseBody = doctorControllerServiceTH.validScheduleOverlap(timeStart, timeEnd, timeInterval);

        assertEquals(exceptedStatus, responseBody.getHttpStatus());
    }

    public static Stream<Arguments> provideTestCasesForValidateScheduleOverlap () {
        String timeStart = "12:00";
        String timeEnd = "13:00";
        return Stream.of(
                Arguments.of(timeStart, timeEnd, null, OK),
                Arguments.of(timeStart, timeEnd, new TimeInterval(LocalTime.of(10,0),LocalTime.of(12,10)), BAD_REQUEST),
                Arguments.of(timeStart, timeEnd, new TimeInterval(LocalTime.of(12,55),LocalTime.of(13,10)), BAD_REQUEST),
                Arguments.of(timeStart, timeEnd, new TimeInterval(LocalTime.of(12,0),LocalTime.of(13,0)), BAD_REQUEST),
                Arguments.of(timeStart, timeEnd, new TimeInterval(LocalTime.of(14,0),LocalTime.of(15,0)), OK));
    }
}