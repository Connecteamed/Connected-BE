package com.connecteamed.server.global.auth;

import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import com.connecteamed.server.global.auth.exception.code.AuthErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Duration accessExpiration;
    private final Duration refreshExpiration;

    public JwtUtil(
            @Value("${jwt.token.secret-key}") String secret,
            @Value("${jwt.token.expiration.access}") Long accessExpiration,
            @Value("${jwt.token.expiration.refresh}") Long refreshExpiration
    ) {
        // [1] 문자열 키를 HMAC-SHA 알고리즘에 적합한 SecretKey 객체로 변환
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = Duration.ofMillis(accessExpiration);
        this.refreshExpiration = Duration.ofMillis(refreshExpiration);
    }

    // [2] AccessToken 생성 (이메일 대신 ID/Username 사용)
    public String createAccessToken(CustomUserDetails user) {
        return createToken(user, accessExpiration);
    }

    // RefreshToken 생성
    public String createRefreshToken(CustomUserDetails user) {
        return createToken(user, refreshExpiration);
    }

    // [3] 토큰에서 사용자 식별자(Subject) 가져오기
    public String getUserId(String token) {
        try {
            //유효하지 않을 시 getClaim은 jwtexception throw
            return getClaims(token).getPayload().getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    // 단순 토큰 유효 유무 확인 함수
    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException e) {
            System.out.println("유효하지 않은 토큰: " + e.getMessage());
            return false;
        }
    }

    // [5] 실제 토큰 생성 로직
    private String createToken(CustomUserDetails user, Duration expiration) {
        Instant now = Instant.now();

        // 권한 정보 추출 (예: ROLE_USER, ROLE_ADMIN)
        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(user.getUsername()) // 유저의 고유 식별자(ID)를 넣습니다.
                .claim("role", authorities)  // 커스텀 클레임으로 권한 추가
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(secretKey)
                .compact();
    }

    // [6] 토큰 파싱 및 검증
    private Jws<Claims> getClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .clockSkewSeconds(60) // 시간 오차 허용 (네트워크 지연 등 대비)
                .build()
                .parseSignedClaims(token);
    }

//토큰 내부에서 만료시간을 꺼내오는 로직
    public Instant getExpiryDate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration().toInstant();
    }

    //예외 처리를 위한 토큰 유효처리함수
    public void validateToken(String token) {
        try {
            getClaims(token); // 여기서 ExpiredJwtException 등이 발생
        } catch (ExpiredJwtException e) {
            throw new GeneralException(AuthErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new GeneralException(AuthErrorCode.INVALID_TOKEN);
        }
    }

//서비스 만료시간 계산 위해 호출
public Duration getRefreshExpiration() {
    return this.refreshExpiration;
}

//컨버터에서 expiresIn값 줄 때 사용
public Long getAccessTokenExpirationMillis() {
        return this.accessExpiration.toMillis();
    }

}
