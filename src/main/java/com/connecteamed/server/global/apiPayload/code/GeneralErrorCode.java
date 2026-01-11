package com.connecteamed.server.global.apiPayload.code;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralErrorCode implements BaseErrorCode{

    //예시(수정필요)
    // 400 Bad Request: 클라이언트의 요청이 잘못됨
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),

    // 401 Unauthorized: 인증이 필요한 서비스에서 인증이 안 됨
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),

    // 403 Forbidden: 인증은 됐으나 해당 리소스에 권한이 없음
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "제한된 접근입니다."),

    // 404 Not Found: 요청한 리소스를 찾을 수 없음
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "해당 리소스를 찾을 수 없습니다."),

    // 409 Conflict: 서버의 현재 상태와 요청이 충돌함
    CONFLICT(HttpStatus.CONFLICT, "COMMON409", "데이터 충돌이 발생했습니다."),

    // 500 Internal Server Error: 서버 내부 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "예기치 않은 서버 에러가 발생했습니다.");
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
