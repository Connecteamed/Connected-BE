package com.connecteamed.server.domain.invite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteCodeRes {

    private String inviteCode;
    private Instant expiredAt;

}
