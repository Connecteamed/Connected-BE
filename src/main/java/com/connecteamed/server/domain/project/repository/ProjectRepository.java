package com.connecteamed.server.domain.project.repository;

import com.connecteamed.server.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
