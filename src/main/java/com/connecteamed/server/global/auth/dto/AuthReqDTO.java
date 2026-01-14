package com.connecteamed.server.global.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthReqDTO {

    //회원가입
    public record JoinDTO(
            @NotBlank
            String name,
            @NotBlank
            String loginId,
            @NotBlank
            String password
    ){}

    // 로그인
    public record LoginDTO(
            @NotBlank
            String loginId,
            @NotBlank
            String password
    ){}
}
