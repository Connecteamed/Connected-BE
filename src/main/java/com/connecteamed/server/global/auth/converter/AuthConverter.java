package com.connecteamed.server.global.auth.converter;

import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.member.enums.SocialType;
import com.connecteamed.server.global.auth.dto.AuthReqDTO;
import com.connecteamed.server.global.auth.dto.AuthResDTO;

import java.util.UUID;

public class AuthConverter {



    // 1. [Request DTO -> Entity] 회원가입용
    public static Member toMember(AuthReqDTO.JoinDTO dto, String encodedPassword) {
        return Member.builder()
                .loginId(dto.loginId())
                .password(encodedPassword)
                .name(dto.name())
                .socialType(SocialType.LOCAL)
                .build();
    }

    // 2. [Entity -> Response DTO] 회원가입 결과 반환용
    public static AuthResDTO.JoinDTO toJoinResultDTO(Member member) {
        return AuthResDTO.JoinDTO.builder()
                .memberId(member.getPublicId())
                .name(member.getName())
                .build();
    }



    //로그인
    public static AuthResDTO.LoginDTO toLoginDTO(Member member, String accessToken, Long expiresIn) {
        return AuthResDTO.LoginDTO.builder()
                .memberId(member.getPublicId()) // UUID 매핑
                .accessToken(accessToken)
                .grantType("Bearer")
                .expiresIn(expiresIn)
                .build();
    }

}
