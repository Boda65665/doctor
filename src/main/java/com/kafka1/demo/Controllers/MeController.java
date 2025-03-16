package com.kafka1.demo.Controllers;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.DTO.DoctorDTO;
import com.kafka1.demo.DTO.SessionDTO;
import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Services.Controller.SessionControllerService;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.SessionDbService;
import com.kafka1.demo.Services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/me")
@Controller
public class MeController {
    private final SessionDbService sessionDBService;
    private final DoctorDbService doctorDBService;
    private final SessionControllerService sessionControllerService;
    private final JwtService jwtService;

    public MeController(SessionDbService sessionDBService, DoctorDbService doctorDBService, SessionControllerService sessionControllerService, JwtService jwtService) {
        this.sessionDBService = sessionDBService;
        this.doctorDBService = doctorDBService;
        this.sessionControllerService = sessionControllerService;
        this.jwtService = jwtService;
    }

    @GetMapping("/sessions")
    public String me_sessions(HttpServletRequest request, Model model){
        String prefix = request.getParameter("prefix");
        String email = jwtService.extractEmail(request);
        HttpResponseBody sessionBody = sessionControllerService.getMeSession(prefix,email);
        model.addAttribute("type_session", sessionBody.get("type"));
        model.addAttribute("me_sessions", sessionBody.get("sessions"));
        return "/me/sessions";
    }

    @GetMapping("/sessions/{id}")
    public String me_session(@PathVariable("id") int id,HttpServletRequest request, Model model){
        String email = jwtService.extractEmail(request);
        User requestingUser = sessionControllerService.getUserByRequest(request);
        HttpResponseBody sessionBody = sessionControllerService.getSessionByID(email,id);
        if (sessionBody.getHttpStatus()!= HttpStatus.OK) return "redirect:/";
        model.addAttribute("me_session", sessionBody.get("session"));
        model.addAttribute("role", requestingUser.getRole());
        return "/me/session";
    }

    @PostMapping("/sessions/{id}")
    public String editStatusActivateSession(@PathVariable("id") int id,HttpServletRequest request, Model model){
        User doctorAccount = sessionControllerService.getUserByRequest(request);
        SessionDTO sessionDTO = sessionDBService.findSessionDtoById(id);
        if (sessionDTO==null) {
            return "redirect:/";
        }
        Doctor requestingDoctor = doctorDBService.findDoctorByUser(doctorAccount.getId());
        DoctorDTO doctorSession = sessionDTO.getDoctor();
        if (requestingDoctor==null || requestingDoctor.getId()!=doctorSession.getId()){
            return "redirect:/";
        }
        if (request.getParameter("prefix")==null) return "redirect:/sessions/"+id;
        model.addAttribute("me_session",sessionDTO);
        model.addAttribute("role", doctorAccount.getRole());
        HttpResponseBody responseBody = sessionControllerService.editStatusActivateSession(doctorAccount.getId(),id,request.getParameter("prefix"));
        if (responseBody.getHttpStatus()!=HttpStatus.OK) {
            model.addAttribute("error", responseBody.getMessage());
        }
        else {
            sessionDTO = sessionDBService.findSessionDtoById(id);
            model.addAttribute("me_session", sessionDTO);
            model.addAttribute("notification", "Успешно");
        }
        return "/me/session";
    }
}
