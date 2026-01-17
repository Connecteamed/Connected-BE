package com.connecteamed.server.global.apiPayload.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.apiPayload.code.BaseErrorCode;
import com.connecteamed.server.global.apiPayload.code.GeneralErrorCode;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GeneralExceptionAdvice {
    
    // 1) @RequestParam / @PathVariable 검증 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        BaseErrorCode code = GeneralErrorCode.BAD_REQUEST;

        // 메시지 예: "detail.documentId: must be greater than or equal to 1"
        String message = (ex.getConstraintViolations().isEmpty())
                ? code.getMessage()
                : ex.getConstraintViolations().iterator().next().getMessage();

        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onFailure(code, message));
    }

    // 1-2) @RequestParam 파라미터 자체가 누락되었을 때 처리
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParams(MissingServletRequestParameterException ex) {
        BaseErrorCode code = GeneralErrorCode.BAD_REQUEST;

        // "loginId 파라미터가 누락되었습니다." 형태의 메시지 생성
        String message = String.format("%s 파라미터가 누락되었습니다.", ex.getParameterName());

        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onFailure(code, message));
    }

    // 2) JSON 파싱 자체가 실패 (문법 오류/타입 불일치 등)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException ex) {
        BaseErrorCode code = GeneralErrorCode.BAD_REQUEST;

        // 너무 내부정보를 노출하지 않으려면 고정 메시지 추천
        String message = "요청 본문(JSON) 형식이 올바르지 않습니다.";

        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onFailure(code, message));
    }

    //애플리 케이션에서 발생하는 커스텀 예외 처리
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<?>> handleException(GeneralException ex){
        return ResponseEntity.status(ex.getCode().getStatus())
                .body(ApiResponse.onFailure(ex.getCode(),ex.getCustomMessage())
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        BaseErrorCode code = GeneralErrorCode.BAD_REQUEST;

        FieldError fieldError = ex.getBindingResult().getFieldErrors().isEmpty()
                ? null
                : ex.getBindingResult().getFieldErrors().get(0);

        String message = (fieldError != null && fieldError.getDefaultMessage() != null)
                ? fieldError.getDefaultMessage()
                : code.getMessage();

        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onFailure(code, message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        BaseErrorCode code = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onFailure(code, ex.getMessage()));
    }

    //사용자가 정의 하는 범위 외 발생 예외 처리- 실패응답 1 구조를 따름
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex){
        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onFailure(code)
                );

    }
}
