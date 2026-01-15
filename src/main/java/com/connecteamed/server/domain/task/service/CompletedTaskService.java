package com.connecteamed.server.domain.task.service;

import com.connecteamed.server.domain.task.dto.CompletedTaskDetailRes;
import com.connecteamed.server.domain.task.dto.CompletedTaskListRes;
import com.connecteamed.server.domain.task.dto.CompletedTaskUpdateReq;
import com.connecteamed.server.domain.task.entity.Task;
import com.connecteamed.server.domain.task.entity.TaskAssignee;
import com.connecteamed.server.domain.task.entity.TaskNote;
import com.connecteamed.server.domain.task.enums.TaskStatus;
import com.connecteamed.server.domain.task.repository.TaskAssigneeRepository;
import com.connecteamed.server.domain.task.repository.TaskNoteRepository;
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
    private final TaskNoteRepository taskNoteRepository;

    @Value("${app.test.user-id:1}")
    private Long testUserId;

    // 완료한 업무 목록 조회
    public CompletedTaskListRes getCompletedTasks(Long projectId) {
        List<Task> completedTasks = taskRepository.findAllByProjectIdAndStatusAndDeletedAtIsNull(
                projectId,
                TaskStatus.DONE
        );

        List<CompletedTaskListRes.TaskSummary> summaries = completedTasks.stream()
                .map(task -> {
                    List<String> assigneeNames = taskAssigneeRepository.findAllByTaskId(task.getId())
                            .stream()
                            .map(assignee -> assignee.getProjectMember().getMember().getName())
                            .toList();

                    return new CompletedTaskListRes.TaskSummary(
                            task.getId(),
                            task.getName(),
                            task.getContent(),
                            task.getStartDate().toString(),
                            task.getDueDate().toString(),
                            task.getStatus().name(),
                            assigneeNames
                    );
                }).toList();
        return new CompletedTaskListRes(summaries);
    }

    // 완료한 업무 상태 변경
    @Transactional
    public void updateCompletedTaskStatus(Long taskId, TaskStatus taskStatus) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.updateStatus(taskStatus);
    }

    // 완료한 업무 상세 조회
    public CompletedTaskDetailRes getCompletedTaskDetail(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();

        List<String> assigneeNames = getAssigneeNames(taskId);

        String myNote = taskNoteRepository.findByTaskIdAndTaskAssignee_ProjectMember_Id(taskId, testUserId)
                .map(TaskNote::getContent)
                .orElse("");

        return new CompletedTaskDetailRes(
                task.getId(),
                task.getName(),
                task.getContent(),
                task.getStartDate().toString(),
                task.getDueDate().toString(),
                task.getStatus().name(),
                assigneeNames,
                myNote
        );
    }

    // 완료한 업무 상세 수정
    @Transactional
    public void updateCompletedTask(Long taskId, CompletedTaskUpdateReq req) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.updateInfo(req.name(), req.content());

        TaskNote note = taskNoteRepository.findByTaskIdAndTaskAssignee_ProjectMember_Id(taskId, testUserId)
                .orElseGet(() -> createNewNote(task));

        note.updateContent(req.noteContent());
    }

    // 완료한 업무 삭제
    @Transactional
    public void deleteCompletedTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.softDelete();
    }

    private List<String> getAssigneeNames(Long taskId) {
        return taskAssigneeRepository.findAllByTaskId(taskId).stream()
                .map(a -> a.getProjectMember().getMember().getName())
                .toList();
    }

    private TaskNote createNewNote(Task task) {
        TaskAssignee assignee = taskAssigneeRepository.findAllByTaskId(task.getId()).get(0);
        return taskNoteRepository.save(TaskNote.builder()
                .task(task)
                .taskAssignee(assignee)
                .content("")
                .build());
    }

    private Long getCurrentUserId() {
        return testUserId;
    }
}
