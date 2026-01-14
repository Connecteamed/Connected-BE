package com.connecteamed.server.domain.project.repository;

import com.connecteamed.server.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByPublicId(UUID publicId);
    Optional<Project> findByName(String name);
}
