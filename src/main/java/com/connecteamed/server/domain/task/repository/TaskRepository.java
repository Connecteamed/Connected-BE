package com.connecteamed.server.domain.task.repository;

import com.connecteamed.server.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import com.connecteamed.server.domain.task.enums.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndDeletedAtIsNull(Long id);

    // TODO: 업무목록 조회 & 완료된 업무 목록 조회 -> 하나로 통일 필요
    List<Task> findAllByProject_IdAndDeletedAtIsNullOrderByStartDateAsc(Long projectId);
  
    List<Task> findAllByProjectIdAndStatusAndDeletedAtIsNull(Long projectId, TaskStatus status);
}
