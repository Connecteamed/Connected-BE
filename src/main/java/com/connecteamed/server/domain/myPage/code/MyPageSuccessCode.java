package com.connecteamed.server.domain.myPage.code;

import com.connecteamed.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MyPageSuccessCode implements BaseSuccessCode {

    OK(HttpStatus.OK, "MYPAGE_OK", "요청에 성공했습니다."),
    RETROSPECTIVE_DELETED(HttpStatus.OK ,"RETROSPECTIVE_DELETED", "회고 삭제에 성공했습니다.")
    ;
    private final HttpStatus status;
    private final String code;
    private final String message;
}
