package com.talktogether.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.talktogether.backend.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        // Bắt các lỗi chưa xác định
        @ExceptionHandler(value = Exception.class)
        public ResponseEntity<ApiResponse<Object>> handlingRuntimeException(Exception exception) {
                log.error("Lỗi hệ thống chưa được phân loại: ", exception);
                ApiResponse<Object> apiResponse = ApiResponse.builder()
                                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                                .build();

                return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode())
                                .body(apiResponse);
        }

        // Bắt các lỗi nghiệp vụ (AppException)
        @ExceptionHandler(value = AppException.class)
        public ResponseEntity<ApiResponse<Object>> handlingAppException(AppException exception) {
                ErrorCode errorCode = exception.getErrorCode();

                ApiResponse<Object> apiResponse = ApiResponse.builder()
                                .code(errorCode.getCode())
                                .message(errorCode.getMessage())
                                .build();

                return ResponseEntity.status(errorCode.getStatusCode())
                                .body(apiResponse);
        }

        // Bắt các lỗi Validation dữ liệu đầu vào (@Valid DTO)
        @ExceptionHandler(value = MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Object>> handlingValidation(MethodArgumentNotValidException exception) {
                String errorMessage = exception.getFieldError() != null ? exception.getFieldError().getDefaultMessage()
                                : "Lỗi dữ liệu đầu vào";

                ApiResponse<Object> apiResponse = ApiResponse.builder().code(400).message(errorMessage)
                                .build();

                return ResponseEntity.badRequest().body(apiResponse);
        }

        @ExceptionHandler(value = HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse<Object>> handlingHttpMessageNotReadable(HttpMessageNotReadableException exception) {
                log.warn("Dữ liệu gửi lên không đọc được hoặc thiếu request body: {}", exception.getMessage());

                ApiResponse<Object> apiResponse = ApiResponse.builder()
                                .code(400)
                                .message("Dữ liệu gửi lên không hợp lệ hoặc thiếu Request Body")
                                .build();
                
                return ResponseEntity.badRequest().body(apiResponse);
        }
}
