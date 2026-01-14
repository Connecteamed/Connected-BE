package com.connecteamed.server.global.auth.controller;


import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.auth.dto.AuthReqDTO;
import com.connecteamed.server.global.auth.dto.AuthResDTO;
import com.connecteamed.server.global.auth.exception.code1.AuthSuccessCode;
import com.connecteamed.server.global.auth.service.AuthCommandService;
import com.connecteamed.server.global.auth.service.AuthQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;
    private final AuthQueryService authQueryService;

    // 회원가입
    @PostMapping("/api/member/signup")
    public ApiResponse<AuthResDTO.JoinDTO> signUp(
            @RequestBody @Valid AuthReqDTO.JoinDTO dto
    ){
        return ApiResponse.onSuccess(AuthSuccessCode.SIGNUP_SUCCESS, authCommandService.signup(dto));
    }

    // 로그인
    @PostMapping("/api/auth/login")
    public ApiResponse<AuthResDTO.LoginDTO> login(
            @RequestBody @Valid AuthReqDTO.LoginDTO dto
    ){
        return ApiResponse.onSuccess(AuthSuccessCode.LOGIN_SUCCESS, authQueryService.login(dto));
    }
}
