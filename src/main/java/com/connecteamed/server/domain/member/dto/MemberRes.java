package com.connecteamed.server.domain.member.dto;


import lombok.*;

public class MemberRes {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckIdResultDTO {
        private boolean isAvailable;
    }

}
