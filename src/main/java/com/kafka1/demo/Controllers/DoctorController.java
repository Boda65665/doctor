package com.kafka1.demo.Controllers;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequestMapping("/doctor")
@Controller
public class DoctorController {
    private final JwtService jwtService;
    private final DoctorControllerService doctorControllerService;
    private final UserDbService userDbService;
    private final DoctorDbService doctorDbService;

    public DoctorController(JwtService jwtService, DoctorControllerService doctorControllerService, UserDbService userDbService, DoctorDbService doctorDbService) {
        this.jwtService = jwtService;
        this.doctorControllerService = doctorControllerService;
        this.userDbService = userDbService;
        this.doctorDbService = doctorDbService;
    }

    @GetMapping("/{id_doctor}")
    public String getDoctor(@PathVariable("id_doctor") int doctorId, Model model) {
        DoctorDTO doctorDTO = doctorDbService.findDoctorDtoById(doctorId);
        if (doctorDTO==null) return "/";
        model.addAttribute("doctor", doctorDTO);
        model.addAttribute("doctorSchedules", doctorDbService.getFreeTime(LocalDate.now(),doctorId));
        return "doctor/profile";
    }

    @PostMapping("/{id_doctor}")
    public String orderConsultation(@PathVariable("id_doctor") int doctorId, HttpServletRequest request, Model model) {
        String email = jwtService.extractEmail(request);
        String timeStartSessionString = request.getParameter("time");
        String longSessionInMinutesString = request.getParameter("minutes");
        LocalDate todayDate = LocalDate.now();
        HttpResponseBody responseBody = doctorControllerService.orderConsultation(doctorId, email, timeStartSessionString, longSessionInMinutesString, todayDate);
        if (responseBody.getHttpStatus() != OK) {
            HashMap<String, Object> dataResponse = responseBody.getData();
            model.addAttribute("doctor", dataResponse.get("doctor"));
            model.addAttribute("doctorSchedules", dataResponse.get("doctorSchedules"));

            if(responseBody.getHttpStatus() == BAD_GATEWAY) return "error/500";

            model.addAttribute("err", responseBody.getMessage());
            return "doctor/profile";
        }
        return "redirect:/doctor/"+doctorId;
    }

    @GetMapping("/editSchedules")
    public String editSchedules(HttpServletRequest request,Model model) {
        String email = jwtService.extractEmail(request);
        UserDTO userDTO = userDbService.findUserDtoByEmail(email);
        DoctorDTO doctorDTO = doctorDbService.findDoctorDtoByUser(userDTO.getId());
        model.addAttribute("schedules",doctorDTO.getDoctorSchedulesDTO());
        model.addAttribute("SchedulesBody" , new SchedulesBody());
        return "doctor/editSchedules";
    }

    @PostMapping("/editSchedules")
    public String editSchedules(HttpServletRequest request, Model model, @RequestParam String prefix, @ModelAttribute("SchedulesBody") @Valid SchedulesBody schedulesBody, BindingResult bindingResult) {
        String email = jwtService.extractEmail(request);
        UserDTO userDTO = userDbService.findUserDtoByEmail(email);
        DoctorDTO doctorDTO = doctorDbService.findDoctorDtoByUser(userDTO.getId());
        HttpResponseBody responseBody = doctorControllerService.editSchedules(schedulesBody, prefix, bindingResult, doctorDTO);
        if (responseBody.getHttpStatus()!=OK) return "redirect:/doctor/editSchedules";

        model.addAttribute("schedules",doctorDTO.getDoctorSchedulesDTO());
        return "doctor/editSchedules";
    }
}