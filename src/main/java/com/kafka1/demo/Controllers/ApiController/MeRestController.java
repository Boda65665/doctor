package com.kafka1.demo.Controllers.ApiController;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Services.Controller.SessionControllerService;
import com.kafka1.demo.Services.DB.SessionDbService;
import com.kafka1.demo.Services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class MeRestController {
    private final SessionControllerService sessionControllerService;
    private final SessionDbService sessionDBService;
    private final JwtService jwtService;

    public MeRestController(SessionControllerService sessionControllerService, SessionDbService sessionDBService, JwtService jwtService) {
        this.sessionControllerService = sessionControllerService;
        this.sessionDBService = sessionDBService;
        this.jwtService = jwtService;
    }

    @GetMapping("/sessions")
    public ResponseEntity<?> getMeSessions(HttpServletRequest request){
        String prefix = request.getParameter("prefix");
        String email = jwtService.extractEmail(request);
        return ResponseEntity.ok(sessionControllerService.getMeSession(prefix,email));
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<?> me_session(@PathVariable("id") int id, HttpServletRequest request){
        String email = jwtService.extractEmail(request);
        HttpResponseBody sessionBody = sessionControllerService.getSessionByID(email,id);
        if (sessionBody.getHttpStatus()==HttpStatus.FORBIDDEN) sessionBody.setMessage("У вас нет прав для получения данных об этой сесии");
        else if (sessionBody.getHttpStatus()==HttpStatus.NOT_FOUND) sessionBody.setMessage("Данной сесии не существует");
        else sessionBody.setMessage("Успешно");
        return new ResponseEntity<>(sessionBody,null, sessionBody.getHttpStatus());
    }

    @PostMapping("/sessions/{id}")
    public ResponseEntity<?> editStatusActivateSession(@PathVariable("id") int id,HttpServletRequest request){
        User doctorAccount = sessionControllerService.getUserByRequest(request);
        HttpResponseBody httpResponseBody = sessionControllerService.editStatusActivateSession(doctorAccount.getId(),id,request.getParameter("prefix"));
        return new ResponseEntity<>(httpResponseBody,null, httpResponseBody.getHttpStatus());
    }
}
