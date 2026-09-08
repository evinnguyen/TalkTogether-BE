package com.talktogether.backend.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.talktogether.backend.dto.request.ChangePasswordRequest;
import com.talktogether.backend.dto.request.UpdateProfileRequest;
import com.talktogether.backend.dto.response.UserResponse;
import com.talktogether.backend.entity.User;
import com.talktogether.backend.exception.AppException;
import com.talktogether.backend.exception.ErrorCode;
import com.talktogether.backend.repository.UserRepository;
import com.talktogether.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getCurrentUser() {
        User currentUser = getAuthenticatedUser();
        return mapToUserResponse(currentUser);
    }

    @Override
    public UserResponse updateProfile(UpdateProfileRequest request) {
        
        User currentUser = getAuthenticatedUser();

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
    public void changePassword(ChangePasswordRequest request) {
        User user = getAuthenticatedUser();

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if(request.getOldPassword().equals(request.getNewPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_CHANGED);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private User getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return user;
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

}
