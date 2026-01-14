package com.connecteamed.server.global.auth.service;

import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.member.repository.MemberRepository;
import com.connecteamed.server.global.auth.converter.AuthConverter;
import com.connecteamed.server.global.auth.dto.AuthReqDTO;
import com.connecteamed.server.global.auth.dto.AuthResDTO;
import com.connecteamed.server.global.auth.exception.AuthException;
import com.connecteamed.server.global.auth.exception.code1.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Override
    public AuthResDTO.JoinDTO signup(
            AuthReqDTO.JoinDTO dto
    ){try {
        //중복 에러 처리
        if (memberRepository.existsByLoginId(dto.loginId())) {
            throw new AuthException(AuthErrorCode.DUPLICATE_LOGIN_ID);
        }

        String encodedPassword = passwordEncoder.encode(dto.password());

        // DTO -> Entity 변환
        Member newMember = AuthConverter.toMember(dto, encodedPassword);

        // DB 저장
        Member savedMember = memberRepository.save(newMember);
        // [중요] DB에 즉시 반영해서 에러가 여기서 터지게 만듭니다.
        memberRepository.flush();

        // Entity -> Response DTO 변환 및 반환
        return AuthConverter.toJoinResultDTO(savedMember);
    }
        catch (Exception e) {
            // 이 코드가 에러의 '진짜 이유'를 콘솔에 빨간 글씨로 뿌려줍니다.
            e.printStackTrace();
            throw e;
        }

    }
}
