package com.talktogether.backend.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.talktogether.backend.dto.request.LoginRequest;
import com.talktogether.backend.dto.request.RefreshTokenRequest;
import com.talktogether.backend.dto.request.RegisterRequest;
import com.talktogether.backend.dto.response.AuthResponse;
import com.talktogether.backend.dto.response.UserResponse;
import com.talktogether.backend.entity.RefreshToken;
import com.talktogether.backend.entity.User;
import com.talktogether.backend.exception.AppException;
import com.talktogether.backend.exception.ErrorCode;
import com.talktogether.backend.repository.RefreshTokenRepository;
import com.talktogether.backend.repository.UserRepository;
import com.talktogether.backend.security.jwt.JwtTokenProvider;
import com.talktogether.backend.service.AuthService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // 1. Tìm user theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        // 2. Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. Sinh JWT Token
        String accessToken = jwtTokenProvider.generateToken(user);
        RefreshToken refreshToken = createOrUpdateRefreshToken(user);

        // 4. Map User entity sang UserResponse DTO
        UserResponse userResponse = mapToUserResponse(user);

        // 5. Trả về AuthResponse
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional
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
        String accessToken = jwtTokenProvider.generateToken(user);
        RefreshToken refreshToken = createOrUpdateRefreshToken(user);

        // 4. Map User entity sang UserResponse DTO
        UserResponse userResponse = mapToUserResponse(user);

        return AuthResponse.builder().accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.generateToken(user);
        RefreshToken updateRefreshToken = createOrUpdateRefreshToken(user);

        UserResponse userResponse = mapToUserResponse(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(updateRefreshToken.getToken())
                .tokenType("Bearer")
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshTokenStr) {
        refreshTokenRepository.deleteByToken(refreshTokenStr);
    }

    private RefreshToken createOrUpdateRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseGet(() -> RefreshToken.builder().user(user).build());

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration));

        return refreshTokenRepository.save(refreshToken);
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

}
