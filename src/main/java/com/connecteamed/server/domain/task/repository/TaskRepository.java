package com.connecteamed.server.domain.task.repository;

import com.connecteamed.server.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByPublicIdAndDeletedAtIsNull(UUID publicId);

    List<Task> findAllByProject_IdAndDeletedAtIsNullOrderByStartDateAsc(Long projectId);
}
