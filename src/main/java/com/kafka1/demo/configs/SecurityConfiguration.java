package com.kafka1.demo.configs;

import com.kafka1.demo.Models.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfiguration {
    private final JwtRequestFilter jwtAuthenticationFilter;

    public SecurityConfiguration(JwtRequestFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        disableCORS(http);
        configEndpoints(http);
        configSessionManagement(http);
        return http.build();
    }

    private void disableCORS(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }));
    }

    private void configEndpoints(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(registry -> {
            configureAuthEndpoints(registry);
            configureDoctorEndpoints(registry);
            configureMeEndpoints(registry);
            configurePaymentEndpoints(registry);
            configureApiEndpoints(registry);
            configureAdminEndpoints(registry);
            registry.anyRequest().permitAll();
        });
    }

    private void configSessionManagement(HttpSecurity http) throws Exception {
        http.sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    private void configureAuthEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        String basePath = "/auth";
        registry
                .requestMatchers(basePath+"/email_confirmation").hasAuthority(Permission.UNVERIFIED.getPermission())
                .requestMatchers(basePath+"/reg", basePath+"/auth/login", basePath+"/re-set-password/**").anonymous()
                .requestMatchers(basePath+"/endpointService").permitAll()
                .requestMatchers(basePath+"/logout").authenticated();
    }

    private void configureDoctorEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        String basePath = "/doctor";
        registry
                .requestMatchers(basePath + "/editSchedules/").hasAuthority(Permission.DOCTOR.getPermission())
                .requestMatchers(basePath + "/*").hasAuthority(Permission.USER.getPermission());
    }


    private void configureMeEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        String basePath = "/me";
        registry
                .requestMatchers(basePath + "/sessions/").hasAuthority(Permission.USER.getPermission())
                .requestMatchers(basePath + "/").hasAuthority(Permission.USER.getPermission());
    }

    private void configurePaymentEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        String basePath = "/pay";
        registry
                .requestMatchers(basePath + "/postback").permitAll()
                .requestMatchers(basePath + "/").hasAuthority(Permission.USER.getPermission());
    }

    private void configureApiEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        String basePath = "/api/auth";
        registry
                .requestMatchers(basePath + "/register", basePath + "/login").anonymous()
                .requestMatchers(basePath + "/confirm_email", basePath + "/resend_code").hasAuthority(Permission.UNVERIFIED.getPermission())
                .requestMatchers(basePath + "/re-set-password").permitAll()
                .requestMatchers("/api/me/sessions/**").hasAnyAuthority(Permission.USER.getPermission());

    }

    private void configureAdminEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers("/admin/illnesses/**").permitAll();
    }
}