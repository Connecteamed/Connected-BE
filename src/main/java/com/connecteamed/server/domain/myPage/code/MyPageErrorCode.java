package com.connecteamed.server.domain.myPage.code;

import com.connecteamed.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MyPageErrorCode implements BaseErrorCode {

    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT_NOT_FOUND", "프로젝트를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "요청이 유효하지 않습니다."),
    PROJECT_NOT_OWNER(HttpStatus.BAD_REQUEST,"PROJECT_NOT_OWNER","프로젝트 삭제 권한이 없습니다."),
    PROJECT_NOT_COMPLETED(HttpStatus.BAD_REQUEST,"PROJECT_NOT_COMPLETED", "진행중인 프로젝트입니다."),
    RETROSPECTIVE_NOT_FOUND(HttpStatus.NOT_FOUND, "RETROSPECTIVE_NOT_FOUND", "해당 ID의 회고를 찾을 수 없습니다."),
    RETROSPECTIVE_NOT_WRITER(HttpStatus.BAD_REQUEST, "RETROSPECTIVE_NOT_WRITER", "해당 회고의 작성자가 아닙니다."),
    RETROSPECTIVE_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "RETROSPECTIVE_ALREADY_DELETED", "이미 삭제된 회고입니다."),
    RETROSPECTIVE_MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "RETROSPECTIVE_MEMBER_NOT_FOUND", "해당 ID의 사용자를 찾을 수 없습니다"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
