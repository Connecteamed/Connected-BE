package com.connecteamed.server.domain.task.repository;

import com.connecteamed.server.domain.task.entity.Task;
import com.connecteamed.server.domain.task.entity.TaskAssignee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
//TODO: 정리하기
public interface TaskAssigneeRepository extends JpaRepository<TaskAssignee, Long> {

    List<TaskAssignee> findAllByTask(Task task);

    void deleteAllByTask(Task task);
  
    List<TaskAssignee> findAllByTaskId(Long taskId);

    List<TaskAssignee> findAllByTaskIdIn(List<Long> taskIds);
}
