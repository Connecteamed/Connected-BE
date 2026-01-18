package com.connecteamed.server.domain.task.repository;

import com.connecteamed.server.domain.task.entity.TaskNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskNoteRepository extends JpaRepository<TaskNote, Long> {
    Optional<TaskNote> findByTaskIdAndTaskAssignee_ProjectMember_Id(Long taskId, Long projectMemberId);
}
