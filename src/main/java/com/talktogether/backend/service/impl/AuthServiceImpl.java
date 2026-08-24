package com.talktogether.backend.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.talktogether.backend.dto.request.LoginRequest;
import com.talktogether.backend.dto.request.RegisterRequest;
import com.talktogether.backend.dto.response.AuthResponse;
import com.talktogether.backend.dto.response.UserResponse;
import com.talktogether.backend.entity.User;
import com.talktogether.backend.exception.AppException;
import com.talktogether.backend.exception.ErrorCode;
import com.talktogether.backend.repository.UserRepository;
import com.talktogether.backend.security.jwt.JwtTokenProvider;
import com.talktogether.backend.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. Tìm user theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        // 2. Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. Sinh JWT Token
        String token = jwtTokenProvider.generateToken(user);

        // 4. Map User entity sang UserResponse DTO
        UserResponse userResponse = mapToUserResponse(user);

        // 5. Trả về AuthResponse
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .user(userResponse)
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // 1. Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // 2. Tạo User entity mới và mã hóa mật khẩu
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .avatarUrl(request.getAvatarUrl())
                .build();

        user = userRepository.save(user);

        // 3. Sinh JWT Token
        String token = jwtTokenProvider.generateToken(user);

        // 4. Map User entity sang UserResponse DTO
        UserResponse userResponse = mapToUserResponse(user);

        return AuthResponse.builder().accessToken(token)
                .tokenType("Bearer")
                .user(userResponse)
                .build();
    }

}
