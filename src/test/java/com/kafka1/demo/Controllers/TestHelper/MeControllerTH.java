package com.kafka1.demo.Controllers.TestHelper;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Controllers.MeController;
import com.kafka1.demo.DTO.DoctorDTO;
import com.kafka1.demo.DTO.SessionDTO;
import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Services.Controller.SessionControllerService;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.SessionDbService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

public class MeControllerTH {
    private final MeController meController;
    private final SessionControllerService sessionControllerService;
    private final SessionDbService sessionDbService;
    private final DoctorDbService doctorDbService;
    private final HttpServletRequest request;
    private final Model model;
    private final int TEST_DOCTOR_ID_ONE = 1;
    private final int TEST_DOCTOR_ID_TWO = 2;
    private final int TEST_SESSION_ID;


    public MeControllerTH(MeController meController, SessionControllerService sessionControllerService, SessionDbService sessionDbService, DoctorDbService doctorDbService, HttpServletRequest request, Model model, int TEST_SESSION_ID) {
        this.meController = meController;
        this.sessionControllerService = sessionControllerService;
        this.sessionDbService = sessionDbService;
        this.doctorDbService = doctorDbService;
        this.request = request;
        this.model = model;
        this.TEST_SESSION_ID = TEST_SESSION_ID;
    }

    public String editStatusActivateSession(boolean isNullSessionDTO, boolean isNullRequestingDoctor, boolean isDoctorNotOwnSession, boolean isPrefixNull, HttpStatus status){
        DoctorDTO doctorOne = getDoctorOne();
        Doctor doctorTwo = getDoctorTwo(isDoctorNotOwnSession,isNullRequestingDoctor);

        mockGetUserByRequest();
        mockFindDoctorByUser(doctorTwo);
        mockFindSessionDtoById(isNullSessionDTO, doctorOne);
        mockPrefixInRequest(isPrefixNull);
        mockEditStatusActivateSession(status);

        return meController.editStatusActivateSession(TEST_SESSION_ID, request, model);
    }

    private DoctorDTO getDoctorOne() {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(TEST_DOCTOR_ID_ONE);

        return doctorDTO;
    }

    private Doctor getDoctorTwo(boolean isNullRequestingDoctor, boolean isDoctorNotOwnSession) {
        int id = TEST_DOCTOR_ID_ONE;
        if (isDoctorNotOwnSession) id = TEST_DOCTOR_ID_TWO;
        if (isNullRequestingDoctor) return null;
        Doctor doctor = new Doctor();
        doctor.setId(id);

        return doctor;
    }

    private void mockGetUserByRequest() {
        when(sessionControllerService.getUserByRequest(request)).thenReturn(new User());
    }

    private void mockFindDoctorByUser(Doctor doctorTwo) {
        when(doctorDbService.findDoctorByUser(nullable(Integer.class))).thenReturn(doctorTwo);
    }

    private void mockFindSessionDtoById(boolean isNullSessionDTO, DoctorDTO doctorDTO) {
        SessionDTO sessionDTO = null;
        if (!isNullSessionDTO) {
            sessionDTO = new SessionDTO();
            sessionDTO.setDoctor(doctorDTO);
        }

        when(sessionDbService.findSessionDtoById(TEST_SESSION_ID)).thenReturn(sessionDTO);
    }

    private void mockPrefixInRequest(boolean isPrefixNull) {
        String prefix = null;
        if (!isPrefixNull) prefix = "mock_prefix";

        when(request.getParameter("prefix")).thenReturn(prefix);
    }

    private void mockEditStatusActivateSession(HttpStatus status) {
        HttpResponseBody responseBody = new HttpResponseBody();
        responseBody.setHttpStatus(status);
        if (status!=HttpStatus.OK) responseBody.setMessage("error");

        when(sessionControllerService.editStatusActivateSession(nullable(Integer.class),nullable(Integer.class),nullable(String.class))).thenReturn(responseBody);
    }
}
