package com.connecteamed.server.domain.member.service;

import com.connecteamed.server.domain.member.dto.MemberRes;
import com.connecteamed.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberRes.CheckIdResultDTO checkIdDuplication(String loginId) {
        // 아이디가 이미 존재한다면 isAvailable은 false
        boolean isAvailable = !memberRepository.existsByLoginId(loginId);

        return MemberRes.CheckIdResultDTO.builder()
                .isAvailable(isAvailable)
                .build();
    }
}
