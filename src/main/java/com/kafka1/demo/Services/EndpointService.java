package com.kafka1.demo.Services;


import com.kafka1.demo.Models.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EndpointService {

    private final JwtService jwtService;

    public EndpointService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String getFinalUrl(HttpServletRequest request){
        String token = jwtService.resolveToken(request);
        if (!jwtService.isTokenValid(token)) return "/auth/login";
        Role role = jwtService.getRole(token);
        if(role==Role.UNVERIFIED) return "/auth/email_confirmation";
        return "/";
    }
}
