package com.connecteamed.server.domain.task.service;

import com.connecteamed.server.domain.task.dto.TaskListRes;
import com.connecteamed.server.domain.task.entity.Task;
import com.connecteamed.server.domain.task.enums.TaskStatus;
import com.connecteamed.server.domain.task.repository.TaskAssigneeRepository;
import com.connecteamed.server.domain.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompletedTaskService {

    private final TaskRepository taskRepository;
    private final TaskAssigneeRepository taskAssigneeRepository;

    @Value("${app.test.user-id:1}")
    private Long testUserId;

    // 완료된 업무 목록 조회
    public TaskListRes getCompletedTasks(Long projectId) {
        List<Task> completedTasks = taskRepository.findAllByProjectIdAndStatusAndDeletedAtIsNull(
                projectId,
                TaskStatus.DONE
        );

        List<TaskListRes.TaskSummary> summaries = completedTasks.stream()
                .map(task -> {
                    List<String> assigneeNames = taskAssigneeRepository.findAllByTaskId(task.getId())
                            .stream()
                            .map(assignee -> assignee.getProjectMember().getMember().getName())
                            .toList();

                    return new TaskListRes.TaskSummary(
                            task.getId(),
                            task.getName(),
                            task.getContent(),
                            task.getStartDate().toString(),
                            task.getDueDate().toString(),
                            task.getStatus().name(),
                            assigneeNames
                    );
                }).toList();
        return new TaskListRes(summaries);
    }

    private Long getCurrentUserId() {
        return testUserId;
    }
}
