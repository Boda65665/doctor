package com.kafka1.demo.Controllers.ApiController;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Body.SchedulesBody;
import com.kafka1.demo.DTO.DoctorDTO;
import com.kafka1.demo.DTO.UserDTO;
import com.kafka1.demo.Services.ApiService.DoctorControllerService;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/doctor")
public class DoctorRestController {
    private final JwtService jwtService;
    private final DoctorControllerService doctorControllerService;
    private final DoctorDbService doctorDbService;
    private final UserDbService userDbService;

    public DoctorRestController(JwtService jwtService, DoctorControllerService doctorControllerService, DoctorDbService doctorDbService, UserDbService userDbService) {
        this.jwtService = jwtService;
        this.doctorControllerService = doctorControllerService;
        this.doctorDbService = doctorDbService;
        this.userDbService = userDbService;
    }

    @PostMapping("/{id_doctor}")
    public ResponseEntity<?> orderConsultation(@PathVariable("id_doctor") int doctorId, HttpServletRequest request) {
        String email = jwtService.extractEmail(request);
        String timeStartSessionString = request.getParameter("time");
        String longSessionInMinutesString = request.getParameter("minutes");
        LocalDate todayDate = LocalDate.now();
        HttpResponseBody responseBody = doctorControllerService.orderConsultation(doctorId, email, timeStartSessionString, longSessionInMinutesString, todayDate);
        return new ResponseEntity<>(responseBody, null, responseBody.getHttpStatus());
    }

    @PostMapping("/editSchedules")
    public ResponseEntity<?> editSchedules(HttpServletRequest request, @RequestParam String prefix, @ModelAttribute("SchedulesBody") @Valid SchedulesBody schedulesBody, BindingResult bindingResult) {
        String email = jwtService.extractEmail(request);
        UserDTO userDTO = userDbService.findUserDtoByEmail(email);
        DoctorDTO doctorDTO = doctorDbService.findDoctorDtoByUser(userDTO.getId());
        HttpResponseBody responseBody = doctorControllerService.editSchedules(schedulesBody, prefix, bindingResult, doctorDTO);
        return new ResponseEntity<>(responseBody, null, responseBody.getHttpStatus());
    }
}
