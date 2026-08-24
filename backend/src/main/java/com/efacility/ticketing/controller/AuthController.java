package com.efacility.ticketing.controller;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import com.efacility.ticketing.dto.AuthResponse;
import com.efacility.ticketing.dto.request.LoginRequest;
import com.efacility.ticketing.dto.request.RegisterRequest;
import com.efacility.ticketing.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin("http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Registered successfully", Map.of("auth", authResponse), HttpStatus.OK)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Login successful", Map.of("auth", authResponse), HttpStatus.OK)
        );
    }
}
