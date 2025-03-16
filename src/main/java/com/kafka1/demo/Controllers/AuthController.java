package com.kafka1.demo.Controllers;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Form.LoginForm;
import com.kafka1.demo.Form.RegisterForm;
import com.kafka1.demo.Security.SHA256;
import com.kafka1.demo.Services.Controller.AuthControllerService;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("/auth")
@Controller
public class AuthController {
    private final UserDbService userDBService;
    private final JwtService jwtService;
    private final AuthControllerService authControllerService;

    @Autowired
    public AuthController(UserDbService userDBService, JwtService jwtService, AuthControllerService authControllerService) {
        this.userDBService = userDBService;
        this.jwtService = jwtService;
        this.authControllerService = authControllerService;
    }

    @GetMapping("/reg")
    public String reg(Model model) {
        RegisterForm registerForm = new RegisterForm();
        model.addAttribute("registerForm", registerForm);
        return "auth/reg";
    }

    @PostMapping("/reg")
    public String reg(@ModelAttribute("registerForm") @Valid RegisterForm registerForm, BindingResult bindingResult,HttpServletResponse response,Model model) {
        HttpResponseBody responseBody = authControllerService.completeRegister(registerForm,bindingResult);
        if (responseBody.getHttpStatus()==HttpStatus.BAD_REQUEST) {
            return "auth/reg";
        }
        else if (responseBody.getHttpStatus()!=HttpStatus.OK){
            model.addAttribute("user_already_created", "User with so email already created");
            return "auth/reg";
        }
        setJWT(response, (String) responseBody.get("JWT"));
        return "redirect:/auth/email_confirmation";
    }

    @GetMapping("/email_confirmation")
    public String emailConfirmationTemplate() {
        return "auth/email_confirmation";
    }

    @PostMapping("/email_confirmation")
    public String emailConfirmation(HttpServletRequest request,Model model) {
        HttpResponseBody responseBody = authControllerService.confirmEmail(request.getParameter("email"),request.getParameter("code"));
        if (responseBody.getHttpStatus()!=HttpStatus.OK) {
            model.addAttribute("err", responseBody.getMessage());
            return "auth/email_confirmation";
        }
        return "redirect:/";
    }

    @PostMapping("/email_confirmation/resend")
    public String resendCodeForEmailConfirmation(HttpServletRequest request,Model model){
        HttpResponseBody responseBody = authControllerService.resendCode(jwtService.extractEmail(request));
        if (responseBody.getHttpStatus()!=HttpStatus.OK){
            model.addAttribute("err", responseBody.getMessage());
            return "auth/email_confirmation";
        }
        return "redirect:/auth/email_confirmation";
    }

    @GetMapping("/login")
    public String login(Model model) {
        LoginForm loginForm = new LoginForm();
        model.addAttribute("loginForm", loginForm);
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(Model model, @ModelAttribute("loginForm") @Valid LoginForm loginForm,BindingResult bindingResult, HttpServletResponse response) {
        HttpResponseBody responseBody = authControllerService.login(loginForm,bindingResult);
        if (responseBody.getHttpStatus()==HttpStatus.BAD_REQUEST) {
            return "auth/login";
        }
        if (responseBody.getHttpStatus()==HttpStatus.UNAUTHORIZED)
        {
            model.addAttribute("err", "Email or password not corrected");
            return "auth/login";
        }
        setJWT(loginForm.getEmail(),response);
        return "redirect:/";
    }

    private void setJWT(String email, HttpServletResponse response) {
        String token = jwtService.generateToken(email);
        Cookie newCookie = new Cookie("JWT",token);
        newCookie.setMaxAge(60 * 60);
        newCookie.setPath("/");
        response.addCookie(newCookie);
    }

    private void setJWT(HttpServletResponse response,String JWT) {
        Cookie newCookie = new Cookie("JWT",JWT);
        newCookie.setMaxAge(60 * 60);
        newCookie.setPath("/");
        response.addCookie(newCookie);
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response){
        Cookie cookie = new Cookie("JWT","");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/auth/login";
    }

    @GetMapping("/re-set-password")
    public String sendUrlForReSetPassword(){
        return "/auth/send_url_for_re_set_password";
    }

    @PostMapping("/re-set-password")
    public String sendUrlForReSetPassword(HttpServletRequest request,Model model){
        String email = request.getParameter("email");
        HttpResponseBody responseBody = authControllerService.resetPassword(email);
        if (responseBody.getHttpStatus()!=HttpStatus.OK){
            model.addAttribute("err", responseBody.getMessage());
            return "/auth/send_url_for_re_set_password";
        }
        model.addAttribute("notification", responseBody.getMessage());
        return "/auth/send_url_for_re_set_password";
    }

    @GetMapping("/re-set-password/{secret_key}")
    public String reSetPassword(@PathVariable("secret_key") String key){
        if (key==null || userDBService.findUserBySecretKey(key)==null) {
            return "redirect:/auth/re-set-password";
        }
        return "/auth/re_set_password";
    }

    @PostMapping("/re-set-password/{secret_key}")
    public String reSetPassword(@PathVariable("secret_key") String key, HttpServletRequest request, Model model){
        if (key==null || userDBService.findUserBySecretKey(key)==null) {
            return "redirect:/auth/re-set-password";
        }
        User user = userDBService.findUserBySecretKey(key);
        String newPassword = request.getParameter("password");
        if (newPassword==null || newPassword.length()<3){
            model.addAttribute("err", "Длинна пароля меньше 3 символов");
            return "/auth/re_set_password";
        }
        user.setPassword(SHA256.hash(newPassword));
        user.setSecretKey(null);
        userDBService.save(user);
        return "redirect:/auth/login";
    }
}