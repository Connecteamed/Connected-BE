package com.connecteamed.server.global.apiPayload.exception;

import com.connecteamed.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * [사용 예시]
 * 1. 기본 메시지 사용 (Enum에 정의된 기본 문구)
 * throw new GeneralException(GeneralErrorCode._BAD_REQUEST);
 *
 * 2. 상세 메시지 사용 (명세서에 따른 구체적 사유)
 * throw new GeneralException(GeneralErrorCode._BAD_REQUEST, "이미 존재하는 닉네임입니다.");
 */
@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private final BaseErrorCode code;
    private final String customMessage;


    // 기본 메시지 사용 case를 위한 생성자 정의
    public GeneralException(BaseErrorCode code) {
        this.code = code;
        this.customMessage = code.getMessage();
    }
}
