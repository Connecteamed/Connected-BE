package com.connecteamed.server.domain.team.code;

import com.connecteamed.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TeamErrorCode implements BaseErrorCode {


;
    private final HttpStatus status;
    private final String code;
    private final String message;
}
