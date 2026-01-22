package com.connecteamed.server.domain.retrospective.repository;

import com.connecteamed.server.domain.retrospective.entity.AiRetrospective;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiRetrospectiveRepository extends JpaRepository<AiRetrospective, Long> {
    Optional<AiRetrospective> findByIdAndProjectId(Long id, Long projectId);
    List<AiRetrospective> findAllByProjectIdOrderByCreatedAtDesc (Long projectId);
}
