package com.kafka1.demo.Controllers.TestHelper;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Body.SchedulesBody;
import com.kafka1.demo.Controllers.DoctorController;
import com.kafka1.demo.DTO.DoctorDTO;
import com.kafka1.demo.DTO.UserDTO;
import com.kafka1.demo.Services.Controller.DoctorControllerService;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class DoctorControllerTH {
    private final DoctorController doctorController;
    private final DoctorControllerService doctorControllerService;
    public final JwtService jwtService;
    private final HttpServletRequest httpServletRequest;
    private final Model model;
    private final BindingResult bindingResult;
    private final UserDbService userDbService;
    private final DoctorDbService doctorDbService;
    private final int TEST_DOCTOR_ID;

    public DoctorControllerTH(DoctorController doctorController, DoctorControllerService doctorControllerService, JwtService jwtService, HttpServletRequest httpServletRequest, Model model, BindingResult bindingResult, UserDbService userDbService, DoctorDbService doctorDbService, int doctorId) {
        this.doctorController = doctorController;
        this.doctorControllerService = doctorControllerService;
        this.jwtService = jwtService;
        this.httpServletRequest = httpServletRequest;
        this.model = model;
        this.bindingResult = bindingResult;
        this.userDbService = userDbService;
        this.doctorDbService = doctorDbService;
        this.TEST_DOCTOR_ID = doctorId;
    }

    public String orderConsultation(HttpStatus status){
        mockOrderConsult(status);
        return doctorController.orderConsultation(TEST_DOCTOR_ID, httpServletRequest, model);
    }

    private void mockOrderConsult(HttpStatus status) {
        HttpResponseBody responseBody = new HttpResponseBody();
        responseBody.setHttpStatus(status);

        when(doctorControllerService.orderConsultation(
                anyInt(),
                nullable(String.class),
                nullable(String.class),
                nullable(String.class),
                any()))
                .thenReturn(responseBody);
    }

    public String editSchedules(HttpStatus status) {
        mockExtractEmail();
        mockFindUserDtoByEmail();
        mockEditSchedules(status);
        mockFindDoctorDtoByUser();

        SchedulesBody mockSchedulesBody = new SchedulesBody();
        return doctorController.editSchedules(httpServletRequest, model, "mock", mockSchedulesBody, bindingResult);

    }

    private void mockFindDoctorDtoByUser() {
        when(doctorDbService.findDoctorDtoByUser(anyInt())).thenReturn(new DoctorDTO());
    }

    private void mockExtractEmail() {
        when(jwtService.extractEmail(httpServletRequest)).thenReturn("mock");
    }

    private void mockFindUserDtoByEmail() {
        when(userDbService.findUserDtoByEmail(anyString())).thenReturn(new UserDTO());
    }

    private void mockEditSchedules(HttpStatus status) {
        HttpResponseBody responseBody = new HttpResponseBody();
        responseBody.setHttpStatus(status);

        when(doctorControllerService.editSchedules(
                any(),
                any(),
                any(),
                any()))
                .thenReturn(responseBody);
    }
}

