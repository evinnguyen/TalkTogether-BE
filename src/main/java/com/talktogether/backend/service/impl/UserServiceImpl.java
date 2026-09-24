package com.talktogether.backend.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.talktogether.backend.dto.request.ChangePasswordRequest;
import com.talktogether.backend.dto.request.UpdateProfileRequest;
import com.talktogether.backend.dto.response.UserResponse;
import com.talktogether.backend.entity.User;
import com.talktogether.backend.exception.AppException;
import com.talktogether.backend.exception.ErrorCode;
import com.talktogether.backend.repository.RefreshTokenRepository;
import com.talktogether.backend.repository.UserRepository;
import com.talktogether.backend.security.SecurityUtils;
import com.talktogether.backend.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserResponse getCurrentUser() {
        User currentUser = SecurityUtils.getCurrentUser();
        return mapToUserResponse(currentUser);
    }

    @Override
    public UserResponse updateProfile(UpdateProfileRequest request) {

        User currentUser = SecurityUtils.getCurrentUser();

        if (request.getAvatarUrl() != null) {
            currentUser.setAvatarUrl(request.getAvatarUrl());
        }

        if (request.getFullName() != null) {
            currentUser.setFullName(request.getFullName());
        }

        currentUser = userRepository.save(currentUser);

        return mapToUserResponse(currentUser);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = SecurityUtils.getCurrentUser();

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_CHANGED);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.INVALID_CONFIRM_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        refreshTokenRepository.deleteByUser(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .fullName(user.getFullName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public List<UserResponse> searchUsers(String keyword) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Pageable pageable = PageRequest.of(0, 20);

        List<User> users = userRepository.searchUsers(keyword.trim(), currentUser.getId(), pageable);

        return users.stream()
                .map(this::mapToUserResponse)
                .toList();

    }

}
