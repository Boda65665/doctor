package com.kafka1.demo.Controllers.Tests;

import com.kafka1.demo.Controllers.DoctorController;
import com.kafka1.demo.Controllers.TestHelper.DoctorControllerTH;
import com.kafka1.demo.Services.Controller.DoctorControllerService;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
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

public class DoctorControllerTest {
    @InjectMocks
    private DoctorController doctorController;
    @Mock
    private DoctorControllerService doctorControllerService;
    @Mock
    private JwtService jwtService;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private Model model;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private UserDbService userDbService;
    @Mock
    private DoctorDbService doctorDbService;

    private DoctorControllerTH doctorControllerTH;

    private static final int TEST_DOCTOR_ID = 1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorControllerTH = new DoctorControllerTH(doctorController, doctorControllerService, jwtService, httpServletRequest, model, bindingResult, userDbService, doctorDbService, TEST_DOCTOR_ID);
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForOrderConsultation")
    @DisplayName("Order Consultation - Parameterized Test")
    public void orderConsultation(HttpStatus status, String excepted){
        String result = doctorControllerTH.orderConsultation(status);

        assertEquals(excepted, result);
    }

    public static Stream<Arguments> provideTestCasesForOrderConsultation() {
        return Stream.of(
        Arguments.of(BAD_GATEWAY, "error/500"),
        Arguments.of(BAD_REQUEST, "doctor/profile"),
        Arguments.of(OK, "redirect:/doctor/"+TEST_DOCTOR_ID)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForEditSchedules")
    @DisplayName("Edit Schedules - Parameterized Test")
    public void editSchedules(HttpStatus status, String excepted){
        String result = doctorControllerTH.editSchedules(status);

        assertEquals(excepted, result);
    }

    public static Stream<Arguments> provideTestCasesForEditSchedules() {
        return Stream.of(
                Arguments.of(BAD_REQUEST, "redirect:/doctor/editSchedules"),
                Arguments.of(OK, "doctor/editSchedules")
        );
    }
}
