package com.connecteamed.server.domain.retrospective.repository;

import com.connecteamed.server.domain.retrospective.entity.RetrospectiveTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetrospectiveTaskRepository extends JpaRepository<RetrospectiveTask, Long> {
}
