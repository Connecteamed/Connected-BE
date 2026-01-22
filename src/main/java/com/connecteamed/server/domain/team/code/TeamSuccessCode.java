package com.connecteamed.server.domain.team.code;

import com.connecteamed.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TeamSuccessCode implements BaseSuccessCode {
    OK(HttpStatus.OK,"TEAM_OK","요청에 성공하였습니다.")
;
    private final HttpStatus status;
    private final String code;
    private final String message;
}
