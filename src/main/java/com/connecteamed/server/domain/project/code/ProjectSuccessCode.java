package com.connecteamed.server.domain.project.code;

import com.connecteamed.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProjectSuccessCode implements BaseSuccessCode {

    CREATED(HttpStatus.CREATED, "PROJECT_CREATED", "프로젝트 생성에 성공했습니다"),
    OK(HttpStatus.OK, "PROJECT_OK", "요청에 성공했습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

