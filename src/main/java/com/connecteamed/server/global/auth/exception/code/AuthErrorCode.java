package com.connecteamed.server.global.auth.exception.code;

import com.connecteamed.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {


    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED,"MEMBER4011", "비밀번호가 틀렸습니다"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH4013", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4014", "유효하지 않은 토큰입니다."),
    // 회원가입 시 중복된 아이디가 있을 경우
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "AUTH4091", "이미 존재하는 아이디입니다."),
    EMPTY_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "AUTH4011", "인증 정보가 존재하지 않습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
