package com.connecteamed.server.domain.token.entity;

import com.connecteamed.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class BlacklistedToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String token;

    @Column(nullable = false, updatable = false)
    private Instant expiryDate;

    public BlacklistedToken(String token, Instant expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }

}
