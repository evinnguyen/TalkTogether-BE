package com.talktogether.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi hệ thống không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_EXISTED(1001, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(1003, "Email đã được sử dụng", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(1004, "Email hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(1005, "Chưa được xác thực hoặc token không hợp lệ", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(1006, "Refresh Token đã hết hạn. Vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(1007, "Refresh Token không hợp lệ hoặc không tồn tại", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}
