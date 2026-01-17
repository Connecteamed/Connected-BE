package com.connecteamed.server.domain.token.entity;

import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Instant expiryDate; // Instant 리팩토링 반영

    // 토큰 만료 여부 확인 로직
    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }


    public RefreshToken updateToken(String newToken, Instant newExpiryDate) {
        this.token = newToken;
        this.expiryDate = newExpiryDate;
        return this;
    }


}