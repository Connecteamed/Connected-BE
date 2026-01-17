package com.connecteamed.server.global.auth.exception;

import com.connecteamed.server.global.apiPayload.code.BaseErrorCode;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;

public class AuthException extends GeneralException {
    public AuthException(BaseErrorCode code) {
        super(code);
    }
}
