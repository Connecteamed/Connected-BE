package com.connecteamed.server.domain.project.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.connecteamed.server.domain.project.entity.ProjectMember;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    Optional<ProjectMember> findByIdAndProjectId(Long id, Long projectId);
}
