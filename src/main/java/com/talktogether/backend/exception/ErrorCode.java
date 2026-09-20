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
    PASSWORD_NOT_CHANGED(1009, "Mật khẩu mới không được giống mật khẩu cũ", HttpStatus.BAD_REQUEST),
    INVALID_CONFIRM_PASSWORD(1010, "Mật khẩu xác nhận không trùng khớp", HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN(1007, "Refresh Token không hợp lệ hoặc không tồn tại", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(1011, "Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),
    ROOM_NOT_FOUND(2001, "Phòng không tồn tại hoặc đã bị giải tán", HttpStatus.NOT_FOUND),
    ROOM_FULL(2002, "Phòng đã đủ số lượng người tham gia", HttpStatus.BAD_REQUEST),
    NOT_IN_ROOM(2003, "Bạn hiện không ở trong phòng nào", HttpStatus.BAD_REQUEST),
    ALREADY_IN_THIS_ROOM(2004, "Bạn đã ở trong phòng này rồi", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}
