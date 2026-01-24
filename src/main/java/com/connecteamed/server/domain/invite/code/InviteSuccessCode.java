package com.connecteamed.server.domain.invite.code;

import com.connecteamed.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InviteSuccessCode implements BaseSuccessCode {


    INVITE_OK(HttpStatus.OK,"INVITE_OK","요청에 성공하였습니다."),
    INVITE_CODE_GENERATE_SUCCESS(HttpStatus.OK,"INVITE_CODE_GENERATE_SUCCESS","초대 코드 발급에 성공하였습니다.")
    ;


    private final HttpStatus status;
    private final String code;
    private final String message;
}
