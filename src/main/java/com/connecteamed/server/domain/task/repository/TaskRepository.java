package com.connecteamed.server.domain.task.repository;

import com.connecteamed.server.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
