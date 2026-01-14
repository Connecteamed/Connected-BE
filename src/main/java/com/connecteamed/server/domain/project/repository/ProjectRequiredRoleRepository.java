package com.connecteamed.server.domain.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.connecteamed.server.domain.project.entity.ProjectRequiredRole;

public interface ProjectRequiredRoleRepository extends JpaRepository<ProjectRequiredRole, Long> {

    // ProjectRequiredRole -> ProjectRole 조인해서 필요한 필드만 뽑기 (N+1 방지)
    @Query("""
        select prr.projectRole.id, prr.projectRole.roleName
        from ProjectRequiredRole prr
        where prr.project.id = :projectId
    """)
    List<Object[]> findRequiredRoles(@Param("projectId") Long projectId);
}
