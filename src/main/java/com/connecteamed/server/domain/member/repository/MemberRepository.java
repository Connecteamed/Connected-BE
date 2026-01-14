package com.connecteamed.server.domain.member.repository;

import com.connecteamed.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member,Long> {

    //로그인시
    Optional<Member> findByLoginId(String loginId);

    // 2. 회원가입 시 중복체크
    boolean existsByLoginId(String loginId);


    //유저조회시
    Optional<Member> findByPublicId(UUID publicId);
}
