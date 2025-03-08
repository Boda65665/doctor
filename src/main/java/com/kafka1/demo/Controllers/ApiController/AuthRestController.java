package com.kafka1.demo.Controllers.ApiController;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Form.LoginForm;
import com.kafka1.demo.Form.RegisterForm;
import com.kafka1.demo.Services.ApiService.AuthControllerService;
import com.kafka1.demo.Services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AuthControllerService authControllerService;
    public final JwtService jwtService;

    public AuthRestController(AuthControllerService authControllerService, JwtService jwtService) {
        this.authControllerService = authControllerService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterForm registerBody, BindingResult bindingResult){
        HttpResponseBody responseBody = authControllerService.completeRegister(registerBody,bindingResult);
        return new ResponseEntity<>(responseBody,null,responseBody.getHttpStatus());
    }

    @PostMapping("/confirm_email")
    public ResponseEntity<?> confirmCode(HttpServletRequest request,@RequestBody Map<String, String> requestBody){
        HttpResponseBody responseBody = authControllerService.confirmEmail(jwtService.extractEmail(request),requestBody.get("code"));
        return new ResponseEntity<>(responseBody,null,responseBody.getHttpStatus());
    }

    @PostMapping("/resend_code")
    public ResponseEntity<?> resendCode(HttpServletRequest request){
        HttpResponseBody responseBody = authControllerService.resendCode(jwtService.extractEmail(request));
        return new ResponseEntity<>(responseBody,null,responseBody.getHttpStatus());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginForm loginBody,BindingResult bindingResult) {
        HttpResponseBody responseBody = authControllerService.login(loginBody, bindingResult);
        return new ResponseEntity<>(responseBody, null, responseBody.getHttpStatus());
    }

    @PostMapping("/re-set-password")
    public ResponseEntity<?> sendUrlForReSetPasswordPost(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        HttpResponseBody responseBody = authControllerService.resetPassword(email);
        return new ResponseEntity<>(responseBody, null, responseBody.getHttpStatus());
    }
}
