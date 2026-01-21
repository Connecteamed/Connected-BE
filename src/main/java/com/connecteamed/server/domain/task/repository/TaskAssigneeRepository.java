package com.connecteamed.server.domain.task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import com.connecteamed.server.domain.task.entity.Task;
import com.connecteamed.server.domain.task.entity.TaskAssignee;
//TODO: 정리하기
public interface TaskAssigneeRepository extends JpaRepository<TaskAssignee, Long> {

    List<TaskAssignee> findAllByTask(Task task);

    @Modifying
    @Query("DELETE FROM TaskAssignee ta WHERE ta.task = :task")
    void deleteAllByTask(@Param("task") Task task);
  
    List<TaskAssignee> findAllByTaskId(Long taskId);

    List<TaskAssignee> findAllByTaskIdIn(List<Long> taskIds);

    @Query("SELECT ta FROM TaskAssignee ta " +
        "JOIN FETCH ta.projectMember pm " +
        "JOIN FETCH pm.member " +
        "WHERE ta.task IN :tasks")
    List<TaskAssignee> findAllByTaskInWithDetails(@Param("tasks") List<Task> tasks);
}
