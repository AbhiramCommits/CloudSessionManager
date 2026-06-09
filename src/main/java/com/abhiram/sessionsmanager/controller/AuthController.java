package com.abhiram.sessionsmanager.controller;

import com.abhiram.sessionsmanager.model.LoginRequest;
import com.abhiram.sessionsmanager.model.LoginResponse;
import com.abhiram.sessionsmanager.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String DEMO_PASSWORD = "demo123";

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (!DEMO_PASSWORD.equals(request.getPassword())) {
            log.warn("event=login_failed userId={}", request.getUserId());
            return ResponseEntity.status(401).build();
        }

        String token = jwtService.generateToken(request.getUserId());
        log.info("event=login_success userId={}", request.getUserId());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
