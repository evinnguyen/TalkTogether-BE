package com.talktogether.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.talktogether.backend.dto.request.ChangePasswordRequest;
import com.talktogether.backend.dto.request.UpdateProfileRequest;
import com.talktogether.backend.dto.response.ApiResponse;
import com.talktogether.backend.dto.response.UserResponse;
import com.talktogether.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo() {

        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Lấy thông tin của người dùng thành công")
                .result(userService.getCurrentUser())
                .build();

    }

    @PutMapping("/me")
    public ApiResponse<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserResponse userResponse = userService.updateProfile(request);
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Cập nhật thông tin thành công")
                .result(userResponse)
                .build();
    }

    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Đổi mật khẩu thành công")
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<UserResponse>> searchUsers(
            @RequestParam(value = "keyword", required = false) String keyword) {
        return ApiResponse.<List<UserResponse>>builder()
                .code(1000)
                .message("Tìm kiếm người dùng thành công")
                .result(userService.searchUsers(keyword))
                .build();
    }

}
