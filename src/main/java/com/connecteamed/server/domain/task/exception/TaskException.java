package com.connecteamed.server.domain.task.exception;

import com.connecteamed.server.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class TaskException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public TaskException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public TaskException(BaseErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}
