package com.talktogether.backend.security;

import org.springframework.security.core.context.SecurityContextHolder;

import com.talktogether.backend.entity.User;
import com.talktogether.backend.exception.AppException;
import com.talktogether.backend.exception.ErrorCode;

public final class SecurityUtils {
    
    private SecurityUtils() {}

    public static User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return user;
    }
}
