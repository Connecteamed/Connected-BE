package com.connecteamed.server.domain.task.repository;

import com.connecteamed.server.domain.task.entity.Task;
import com.connecteamed.server.domain.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findAllByProjectIdAndStatusAndDeletedAtIsNull(Long projectId, TaskStatus status);
}
