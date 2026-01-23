package com.connecteamed.server.domain.invite.repository;

import com.connecteamed.server.domain.invite.entity.InviteCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {

    //해당 프로젝트의 유효한 초대코드가 있는지 확인
    Optional<InviteCode> findTopByProjectIdAndExpiredAtAfterOrderByCreatedAtDesc(Long projectId, Instant now);

    //입력받은 초대 코드 유효한지 확인
    Optional<InviteCode> findByCodeAndExpiredAtAfter(String code, Instant now);

    boolean existsByCode(String code);
}
