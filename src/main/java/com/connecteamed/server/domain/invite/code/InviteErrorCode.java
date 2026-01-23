package com.connecteamed.server.domain.invite.code;

import com.connecteamed.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InviteErrorCode implements BaseErrorCode {


    INVALID_INVITE_CODE(HttpStatus.UNAUTHORIZED,"INVALID_INVITE_CODE","유효하지 않은 초대 코드입니다."),
    INVITE_CODE_EXPIRED(HttpStatus.UNAUTHORIZED,"INVITE_CODE_EXPIRED","만료된 초대 코드입니다."),
    INVITE_ALREADY_INVITED(HttpStatus.UNAUTHORIZED,"INVITE_ALREADY_INVITED","이미 초대되어 있는 상태입니다."),
   INVITE_UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED,"INVITE_UNAUTHORIZED_MEMBER","초대 코드 발급 권한이 없습니다."),
    INVITE_CODE_GENERATION_FAILED(HttpStatus.BAD_REQUEST,".INVITE_CODE_GENERATION_FAILED","초대 코드 생성에 실패하였습니다.")
    ;


    private final HttpStatus status;
    private final String code;
    private final String message;
}
