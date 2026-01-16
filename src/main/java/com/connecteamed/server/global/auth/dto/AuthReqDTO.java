package com.connecteamed.server.global.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public class AuthReqDTO {

    //회원가입
    @Builder
    public record JoinDTO(
            @NotBlank(message ="이름은 회원가입에 필수 입력 값입니다.")
            String name,
            @NotBlank(message = "ID는 회원가입에 필수 입력 값입니다.")
            String loginId,
            @NotBlank(message = "비밀번호는 회원가입에 필수 입력 값입니다.")
            String password
    ){}

    // 로그인
    @Builder
    public record LoginDTO(
            @NotBlank(message = "ID는 로그인에 필수 입력 값입니다.")
            String loginId,
            @NotBlank(message = "비밀번호는 로그인에 필수 입력 값입니다.")
            String password
    ){}

    //토큰 재발급
    @Builder
    public record ReissueDTO(
            @NotBlank(message = "refreshToken을 받지 못해 재발급이 불가능합니다.")
            String refreshToken
    ){}
}
