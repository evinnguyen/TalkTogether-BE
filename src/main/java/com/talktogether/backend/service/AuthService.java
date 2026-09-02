package com.talktogether.backend.service;

import com.talktogether.backend.dto.request.LoginRequest;
import com.talktogether.backend.dto.request.RefreshTokenRequest;
import com.talktogether.backend.dto.request.RegisterRequest;
import com.talktogether.backend.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String refreshToken);
}
