package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.AuthResponse;
import com.efacility.ticketing.dto.request.LoginRequest;
import com.efacility.ticketing.dto.request.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
