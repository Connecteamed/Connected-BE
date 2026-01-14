package com.connecteamed.server.global.util;

import com.connecteamed.server.global.auth.exception.AuthException;
import com.connecteamed.server.global.auth.exception.code.AuthErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static String getCurrentLoginId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new AuthException(AuthErrorCode.EMPTY_AUTHENTICATION);
        }

        return authentication.getName(); // JwtUtil에서 subject로 넣었던 loginId가 나옵니다.
    }
}
