package com.connecteamed.server.domain.project.repository;

import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    @EntityGraph(attributePaths = {"member", "roles", "roles.role"})
    List<ProjectMember> findAllByProjectId(Long projectId);

    @EntityGraph(attributePaths = {"member", "roles", "roles.role"})
    Optional<ProjectMember> findByIdAndProjectId(Long id, Long projectId);

    @EntityGraph(attributePaths = {"member", "roles", "roles.role"})
    List<ProjectMember> findAllByIdIn(List<Long> ids);\
      
    @EntityGraph(attributePaths = {"project", "roles", "roles.role"})
    List<ProjectMember> findAllByMember(Member member);

}
