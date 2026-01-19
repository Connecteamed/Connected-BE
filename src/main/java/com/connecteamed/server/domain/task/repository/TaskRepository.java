package com.connecteamed.server.domain.task.repository;

import com.connecteamed.server.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import com.connecteamed.server.domain.task.enums.TaskStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
//TODO: 정리하기
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByPublicIdAndDeletedAtIsNull(UUID publicId);

    List<Task> findAllByProject_IdAndDeletedAtIsNullOrderByStartDateAsc(Long projectId);
  
    List<Task> findAllByProjectIdAndStatusAndDeletedAtIsNull(Long projectId, TaskStatus status);
}
