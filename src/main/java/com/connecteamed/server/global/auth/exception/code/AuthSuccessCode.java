package com.connecteamed.server.global.auth.exception.code;

import com.connecteamed.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthSuccessCode implements BaseSuccessCode {
    LOGIN_SUCCESS(HttpStatus.OK, "MEMBER2001", "로그인에 성공하였습니다."),
    SIGNUP_SUCCESS(HttpStatus.CREATED, "MEMBER2011", "회원가입이 완료되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK,"MEMBER2002","로그아웃에 성공하였습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
