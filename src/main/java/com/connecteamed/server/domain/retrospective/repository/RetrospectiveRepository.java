package com.connecteamed.server.domain.retrospective.repository;

import com.connecteamed.server.domain.retrospective.entity.AiRetrospective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetrospectiveRepository extends JpaRepository<AiRetrospective, Long> {

    @Query("SELECT ar FROM AiRetrospective ar " +
            "WHERE ar.deletedAt IS NULL " +
            "ORDER BY ar.createdAt DESC")
    List<AiRetrospective> findRecentRetrospectives();

    @Query("SELECT ar FROM AiRetrospective ar " +
            "JOIN ar.writer pm " +
            "JOIN pm.member m " +
            "WHERE ar.deletedAt IS NULL " +
            "AND m.loginId = :username " +
            "ORDER BY ar.createdAt DESC")
    List<AiRetrospective> findRecentRetrospectivesByUsername(@Param("username") String username);
}
