package com.connecteamed.server.global.auth.dto;

import lombok.Builder;

import java.util.UUID;

public class AuthResDTO {

    //회원가입
    @Builder
    public record JoinDTO(
            UUID memberId,
            String name
    ){}



    //로그인
    @Builder
    public record LoginDTO(
            UUID memberId,
            String accessToken,
            //refreshToken 추가 예정
            String grantType,
            Long expiresIn
    ){}
}
