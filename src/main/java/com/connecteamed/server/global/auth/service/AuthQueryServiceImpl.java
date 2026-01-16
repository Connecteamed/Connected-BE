package com.connecteamed.server.global.auth.service;

import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.member.exception.MemberException;
import com.connecteamed.server.domain.member.exception.code.MemberErrorCode;
import com.connecteamed.server.domain.member.repository.MemberRepository;
import com.connecteamed.server.domain.token.entity.RefreshToken;
import com.connecteamed.server.domain.token.repository.RefreshTokenRepository;
import com.connecteamed.server.global.auth.CustomUserDetails;
import com.connecteamed.server.global.auth.JwtUtil;
import com.connecteamed.server.global.auth.converter.AuthConverter;
import com.connecteamed.server.global.auth.dto.AuthReqDTO;
import com.connecteamed.server.global.auth.dto.AuthResDTO;
import com.connecteamed.server.global.auth.exception.AuthException;
import com.connecteamed.server.global.auth.exception.code.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 로그인 및 조회는 ReadOnly로 성능 최적화
public class AuthQueryServiceImpl implements AuthQueryService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResDTO.LoginDTO login(AuthReqDTO.LoginDTO dto) {
        // 1. 아이디(Email)로 사용자 존재 여부 확인
        Member member = memberRepository.findByLoginId(dto.loginId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        // 2. 비밀번호 일치 여부 검증
        // DB의 암호화된 비번과 사용자가 입력한 평문 비번을 비교합니다.
        if (!passwordEncoder.matches(dto.password(), member.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD);
        }

        // 3. JWT 토큰 발급을 위한 Security 전용 객체 생성
        CustomUserDetails userDetails = new CustomUserDetails(member);

        // 4. 액세스 토큰 생성
        String accessToken = jwtUtil.createAccessToken(userDetails);
        String refreshToken = jwtUtil.createRefreshToken(userDetails);

        Instant expiryDate = Instant.now().plus(jwtUtil.getRefreshExpiration());

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByMember(member)
        .map(existingToken -> existingToken.updateToken(refreshToken, expiryDate))
                .orElseGet(() -> RefreshToken.builder()
                        .token(refreshToken)
                        .member(member)
                        .expiryDate(expiryDate)
                        .build());

        refreshTokenRepository.save(refreshTokenEntity);

        // 5. 만료 시간 설정 (초 단위로 변환: 예: 4시간 = 14400초)
        Long expiresIn = jwtUtil.getAccessTokenExpirationMillis() / 1000;

        // 6. Converter를 통해 Entity + Token -> ResDTO 변환 후 반환
        return AuthConverter.toLoginDTO(member, accessToken,refreshToken,expiresIn);
    }


    //refreshToken 재발급 로직
    @Override
    @Transactional
    public AuthResDTO.RefreshResultDTO reissue(AuthReqDTO.ReissueDTO dto) {
        //DB에서 해당 리프레시 토큰 검색
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(dto.refreshToken())
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN));

        //토큰 만료 여부 확인
        if (refreshTokenEntity.isExpired()) {
            refreshTokenRepository.delete(refreshTokenEntity); // 만료된 토큰은 삭제
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        //연관된 회원 정보 가져오기
        Member member = refreshTokenEntity.getMember();
        CustomUserDetails userDetails = new CustomUserDetails(member);

        //새로운 Access Token 및 Refresh Token 발급
        String newAccessToken = jwtUtil.createAccessToken(userDetails);
        String newRefreshToken = jwtUtil.createRefreshToken(userDetails);
        Instant newExpiryDate = Instant.now().plus(jwtUtil.getRefreshExpiration());

        // 5. DB의 기존 토큰 정보 갱신
        refreshTokenEntity.updateToken(newRefreshToken, newExpiryDate);

        // 6. 컨버터를 통해 응답 DTO로 변환하여 반환
        return AuthConverter.toRefreshResultDTO(newAccessToken, newRefreshToken);
    }


}
