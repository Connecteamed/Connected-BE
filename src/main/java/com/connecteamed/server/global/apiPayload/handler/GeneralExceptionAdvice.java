package com.connecteamed.server.global.apiPayload.handler;

import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.apiPayload.code.BaseErrorCode;
import com.connecteamed.server.global.apiPayload.code.GeneralErrorCode;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GeneralExceptionAdvice {

    //애플리 케이션에서 발생하는 커스텀 예외 처리
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<?>> handleException(GeneralException ex, HttpServletRequest request){
        // Log exception details and request info so we can trace occurrences where DB changes happened but client got a 500
        log.warn("Handled GeneralException - {} {} - code: {} - message: {}", request.getMethod(), request.getRequestURI(), ex.getCode().getCode(), ex.getCustomMessage(), ex);
        return ResponseEntity.status(ex.getCode().getStatus())
                .body(ApiResponse.onFailure(ex.getCode(),ex.getCustomMessage())
                );
    }

    //사용자가 정의 하는 범위 외 발생 예외 처리- 실패응답 1 구조를 따름
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex, HttpServletRequest request){
        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;
        // Log full stacktrace and request info to aid debugging when logs previously showed nothing
        log.error("Unhandled exception - {} {} - returning {}: {}", request.getMethod(), request.getRequestURI(), code.getCode(), code.getMessage(), ex);
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onFailure(code)
                );

    }
}
