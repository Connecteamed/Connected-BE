package com.connecteamed.server.global.apiPayload.code;

import jakarta.annotation.Generated;
import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    HttpStatus getStatus();
    String getCode();
    String getMessage();

}
