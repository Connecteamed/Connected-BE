package com.connecteamed.server.domain.member.exception.code;

import com.connecteamed.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum MemberSuccessCode implements BaseSuccessCode {

    FOUND(HttpStatus.OK,
            "MEMBER200_1",
            "성공적으로 사용자를 조회했습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
