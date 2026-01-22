package com.connecteamed.server.domain.project.repository;

import com.connecteamed.server.domain.project.entity.ProjectRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRoleRepository extends JpaRepository<ProjectRole, Long> {
    Optional<ProjectRole> findByRoleName(String roleName);
}