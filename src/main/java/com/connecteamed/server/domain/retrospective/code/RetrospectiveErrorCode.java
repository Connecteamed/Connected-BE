package com.connecteamed.server.domain.retrospective.code;

import com.connecteamed.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RetrospectiveErrorCode implements BaseErrorCode {


    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "요청이 유효하지 않습니다."),
    RETROSPECTIVE_NOT_FOUND(HttpStatus.BAD_REQUEST, "RETROSPECTIVE_NOT_FOUND", "해당 ID의 회고를 찾을 수 없습니다."),
    RETROSPECTIVE_NOT_WRITER(HttpStatus.BAD_REQUEST, "RETROSPECTIVE_NOT_WRITER", "해당 회고의 작성자가 아닙니다."),
    RETROSPECTIVE_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "RETROSPECTIVE_ALREADY_DELETED", "이미 삭제된 회고입니다."),
    ;


    private final HttpStatus status;
    private final String code;
    private final String message;
}
