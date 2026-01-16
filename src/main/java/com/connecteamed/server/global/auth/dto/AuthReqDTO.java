package com.connecteamed.server.global.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

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

    //토큰 재발급
    @Builder
    public record ReissueDTO(
            String refreshToken
    ){}
}
