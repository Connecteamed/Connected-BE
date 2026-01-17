package com.connecteamed.server.domain.project.repository;

import com.connecteamed.server.domain.project.entity.ProjectRequiredRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRequiredRoleRepository extends JpaRepository<ProjectRequiredRole, Long> {
    List<ProjectRequiredRole> findByProjectId(Long projectId);
}

