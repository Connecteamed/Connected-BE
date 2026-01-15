package com.connecteamed.server.global.auth;


import com.connecteamed.server.domain.member.exception.MemberException;
import com.connecteamed.server.domain.member.exception.code.MemberErrorCode;
import com.connecteamed.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String loginId) throws MemberException {
        return memberRepository.findByLoginId(loginId)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));
    }
}
