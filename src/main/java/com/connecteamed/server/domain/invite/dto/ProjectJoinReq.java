package com.connecteamed.server.domain.invite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectJoinReq {
    @NotBlank(message = "초대 코드는 필수 입력 항목입니다.")
    private String inviteCode;
}
