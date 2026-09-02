package com.talktogether.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talktogether.backend.dto.request.LoginRequest;
import com.talktogether.backend.dto.request.RefreshTokenRequest;
import com.talktogether.backend.dto.request.RegisterRequest;
import com.talktogether.backend.dto.response.ApiResponse;
import com.talktogether.backend.dto.response.AuthResponse;
import com.talktogether.backend.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse result = authService.login(request);
        return ApiResponse.<AuthResponse>builder()
                .code(1000)
                .message("Đăng nhập thành công")
                .result(result)
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse result = authService.register(request);
        return ApiResponse.<AuthResponse>builder()
                .code(1000)
                .message("Đăng ký tài khoản thành công")
                .result(result)
                .build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse result = authService.refreshToken(request);
        return ApiResponse.<AuthResponse>builder()
                .code(1000)
                .message("Cấp lại Refresh token thành công")
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Đăng xuất thành công")
                .result("Đăng xuất thành công")
                .build();
    }

}
