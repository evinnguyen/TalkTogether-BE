package com.talktogether.backend.service;

import java.util.List;

import com.talktogether.backend.dto.request.ChangePasswordRequest;
import com.talktogether.backend.dto.request.UpdateProfileRequest;
import com.talktogether.backend.dto.response.UserResponse;

public interface UserService {
    UserResponse getCurrentUser();

    UserResponse updateProfile(UpdateProfileRequest request);

    void changePassword(ChangePasswordRequest request);

    List<UserResponse> searchUsers(String keyword);
}
