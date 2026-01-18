package com.connecteamed.server.global.auth.service;

import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.member.repository.MemberRepository;
import com.connecteamed.server.global.auth.converter.AuthConverter;
import com.connecteamed.server.global.auth.dto.AuthReqDTO;
import com.connecteamed.server.global.auth.dto.AuthResDTO;
import com.connecteamed.server.global.auth.exception.AuthException;
import com.connecteamed.server.global.auth.exception.code.AuthErrorCode;
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
    ) {
        //중복 에러 처리
        if (memberRepository.existsByLoginId(dto.loginId())) {
            throw new AuthException(AuthErrorCode.DUPLICATE_LOGIN_ID);
        }

        String encodedPassword = passwordEncoder.encode(dto.password());

        Member newMember = AuthConverter.toMember(dto, encodedPassword);

        // DB 저장
        Member savedMember = memberRepository.save(newMember);
        memberRepository.flush();

        return AuthConverter.toJoinResultDTO(savedMember);



    }
}
