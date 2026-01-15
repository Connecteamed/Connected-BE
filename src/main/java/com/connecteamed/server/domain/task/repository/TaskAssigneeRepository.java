package com.connecteamed.server.domain.task.repository;

import com.connecteamed.server.domain.task.entity.TaskAssignee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskAssigneeRepository extends JpaRepository<TaskAssignee,Long> {
    List<TaskAssignee> findAllByTaskId(Long taskId);
}
