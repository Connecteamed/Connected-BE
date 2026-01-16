package com.connecteamed.server.global.auth.controller;


import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.auth.dto.AuthReqDTO;
import com.connecteamed.server.global.auth.dto.AuthResDTO;
import com.connecteamed.server.global.auth.exception.code.AuthSuccessCode;
import com.connecteamed.server.global.auth.service.AuthCommandService;
import com.connecteamed.server.global.auth.service.AuthQueryService;
import io.swagger.v3.oas.annotations.Operation;
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

    //로그아웃
    @Operation(summary = "로그아웃", description = "토큰을 무효화하여 로그아웃 처리를 합니다.")
    @PostMapping("/api/auth/logout")
    public void logout() {
        // 실제 로직은 SecurityConfig의 LogoutHandler에서 처리
    }

    //토큰 재발급
    @PostMapping("/api/auth/refresh")
    public ApiResponse<AuthResDTO.RefreshResultDTO> reissue(@RequestBody AuthReqDTO.ReissueDTO request) {

        AuthResDTO.RefreshResultDTO result = authQueryService.reissue(request);

        return ApiResponse.onSuccess(AuthSuccessCode.REISSUE_SUCCESS, result);
    }
}
