package com.connecteamed.server.domain.task.service;

import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.domain.project.repository.ProjectMemberRepository;
import com.connecteamed.server.domain.project.repository.ProjectRepository;
import com.connecteamed.server.domain.task.dto.*;
import com.connecteamed.server.domain.task.entity.Task;
import com.connecteamed.server.domain.task.entity.TaskAssignee;
import com.connecteamed.server.domain.task.exception.TaskErrorCode;
import com.connecteamed.server.domain.task.exception.TaskException;
import com.connecteamed.server.domain.task.repository.TaskAssigneeRepository;
import com.connecteamed.server.domain.task.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskAssigneeRepository taskAssigneeRepository;

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Override
    public UUID createTask(Long projectId, TaskCreateReq req) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new TaskException(TaskErrorCode.PROJECT_NOT_FOUND));

        if (req.startDate().isAfter(req.dueDate())) {
            throw new TaskException(TaskErrorCode.INVALID_SCHEDULE);
        }

        Task task = Task.builder()
                .project(project)
                .name(req.name())
                .content(req.content())
                .startDate(req.startDate())
                .dueDate(req.dueDate())
                .build();

        Task saved = taskRepository.save(task);

        List<Long> assigneeIds = req.assigneeProjectMemberIds() == null ? List.of() : req.assigneeProjectMemberIds();
        attachAssignees(saved, projectId, assigneeIds);

        return saved.getPublicId();
    }

    @Override
    public List<TaskSummaryRes> getProjectTasks(Long projectId) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new TaskException(TaskErrorCode.PROJECT_NOT_FOUND));

        List<Task> tasks = taskRepository.findAllByProject_IdAndDeletedAtIsNullOrderByStartDateAsc(projectId);

        List<TaskSummaryRes> result = new ArrayList<>();
        for (Task task : tasks) {
            List<TaskAssigneeRes> assignees = toAssigneeRes(taskAssigneeRepository.findAllByTask(task));
            result.add(new TaskSummaryRes(
                    task.getPublicId(),
                    task.getName(),
                    task.getContent(),
                    task.getStatus(),
                    task.getStartDate(),
                    task.getDueDate(),
                    assignees
            ));
        }
        return result;
    }

    @Override
    public TaskDetailRes getTaskDetail(UUID taskId) {
        Task task = taskRepository.findByPublicIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new TaskException(TaskErrorCode.TASK_NOT_FOUND));

        List<TaskAssigneeRes> assignees = toAssigneeRes(taskAssigneeRepository.findAllByTask(task));

        return new TaskDetailRes(
                task.getPublicId(),
                task.getProject().getId(),
                task.getName(),
                task.getContent(),
                task.getStatus(),
                task.getStartDate(),
                task.getDueDate(),
                assignees,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    @Override
    public void updateTaskStatus(UUID taskId, TaskStatusUpdateReq req) {
        Task task = taskRepository.findByPublicIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new TaskException(TaskErrorCode.TASK_NOT_FOUND));

        task.changeStatus(req.status());
    }

    @Override
    public void updateTaskSchedule(UUID taskId, TaskScheduleUpdateReq req) {
        Task task = taskRepository.findByPublicIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new TaskException(TaskErrorCode.TASK_NOT_FOUND));

        if (req.startDate().isAfter(req.dueDate())) {
            throw new TaskException(TaskErrorCode.INVALID_SCHEDULE);
        }

        task.changeSchedule(req.startDate(), req.dueDate());
    }

    @Override
    public void updateTaskAssignees(UUID taskId, TaskAssigneeUpdateReq req) {
        Task task = taskRepository.findByPublicIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new TaskException(TaskErrorCode.TASK_NOT_FOUND));

        Long projectId = task.getProject().getId();

        taskAssigneeRepository.deleteAllByTask(task);

        List<Long> assigneeIds = req.assigneeProjectMemberIds() == null ? List.of() : req.assigneeProjectMemberIds();
        attachAssignees(task, projectId, assigneeIds);
    }

    @Override
    public void deleteTask(UUID taskId) {
        Task task = taskRepository.findByPublicIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new TaskException(TaskErrorCode.TASK_NOT_FOUND));

        task.softDelete();
    }

    private void attachAssignees(Task task, Long projectId, List<Long> projectMemberIds) {
        if (projectMemberIds == null || projectMemberIds.isEmpty()) {
            return;
        }

        List<Long> distinctIds = projectMemberIds.stream().distinct().toList();

        List<ProjectMember> members = projectMemberRepository.findAllByIdIn(distinctIds);
        if (members.size() != distinctIds.size()) {
            throw new TaskException(TaskErrorCode.INVALID_ASSIGNEE);
        }

        for (ProjectMember pm : members) {
            if (pm.getProject() == null || !Objects.equals(pm.getProject().getId(), projectId)) {
                throw new TaskException(TaskErrorCode.ASSIGNEE_NOT_IN_PROJECT);
            }
        }

        List<TaskAssignee> entities = members.stream()
                .map(pm -> TaskAssignee.builder()
                        .task(task)
                        .projectMember(pm)
                        .build()
                )
                .collect(Collectors.toList());

        taskAssigneeRepository.saveAll(entities);
    }

    private List<TaskAssigneeRes> toAssigneeRes(List<TaskAssignee> assignees) {
        List<TaskAssigneeRes> result = new ArrayList<>();
        for (TaskAssignee ta : assignees) {
            ProjectMember pm = ta.getProjectMember();

            Long projectMemberId = pm.getId();
            Long memberId = pm.getMember() != null ? pm.getMember().getId() : null;
            UUID memberPublicId = pm.getMember() != null ? pm.getMember().getPublicId() : null;
            String memberName = pm.getMember() != null ? pm.getMember().getName() : null;

            result.add(new TaskAssigneeRes(projectMemberId, memberId, memberPublicId, memberName));
        }
        return result;
    }
}
