package com.connecteamed.server.domain.token.repository;

import com.connecteamed.server.domain.token.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    Boolean existsByToken(String token);
}
