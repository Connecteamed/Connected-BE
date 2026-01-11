package com.connecteamed.server.global.apiPayload.handler;

import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.apiPayload.code.BaseErrorCode;
import com.connecteamed.server.global.apiPayload.code.GeneralErrorCode;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeneralExceptionAdvice {

    //애플리 케이션에서 발생하는 커스텀 예외 처리
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(GeneralException ex){

        return ResponseEntity.status(ex.getCode().getStatus())
                .body(ApiResponse.onFailure(ex.getCode())
                );
    }

    //그 외 정의되지 않은 모든 예외 처리 우선은 data부분에 ex.getMessage() 전달-> 수정필요?
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex){
        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onFailure(code,ex.getMessage()
                )
                );

    }
}
