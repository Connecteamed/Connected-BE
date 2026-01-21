package com.connecteamed.server.domain.project.code;

import com.connecteamed.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProjectErrorCode implements BaseErrorCode {

    PROJECT_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "PROJECT_NAME_REQUIRED", "프로젝트명은 필수 입력 값입니다."),
    PROJECT_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "PROJECT_NAME_ALREADY_EXISTS", "이미 존재하는 프로젝트명입니다."),
    PROJECT_GOAL_REQUIRED(HttpStatus.BAD_REQUEST, "PROJECT_GOAL_REQUIRED", "프로젝트 목표는 필수 입력 값입니다."),
    PROJECT_REQUIRED_ROLES_REQUIRED(HttpStatus.BAD_REQUEST, "PROJECT_REQUIRED_ROLES_REQUIRED", "필요 역할은 필수 입력 값입니다."),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROLE_NOT_FOUND", "요청한 역할을 찾을 수 없습니다."),
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT_NOT_FOUND", "프로젝트를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "요청이 유효하지 않습니다."),
    ;


    private final HttpStatus status;
    private final String code;
    private final String message;
}

