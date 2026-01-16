package com.connecteamed.server.domain.task.controller;

import com.connecteamed.server.domain.task.dto.*;
import com.connecteamed.server.domain.task.service.TaskService;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.apiPayload.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    // 업무 추가
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createTask(
            @PathVariable Long projectId,
            @RequestBody @Valid TaskCreateReq req
    ) {
        UUID taskId = taskService.createTask(projectId, req);
        return ResponseEntity.ok(
                ApiResponse.onSuccess(GeneralSuccessCode._OK, Map.of("taskId", taskId))
        );
    }

    // 업무 목록 조회(전체)
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<ApiResponse<List<TaskSummaryRes>>> getProjectTasks(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(GeneralSuccessCode._OK, taskService.getProjectTasks(projectId))
        );
    }

    // 업무 상세 조회
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse<TaskDetailRes>> getTaskDetail(
            @PathVariable UUID taskId
    ) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(GeneralSuccessCode._OK, taskService.getTaskDetail(taskId))
        );
    }

    // 업무 상태 변경(드롭다운)
    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<ApiResponse<Void>> updateTaskStatus(
            @PathVariable UUID taskId,
            @RequestBody @Valid TaskStatusUpdateReq req
    ) {
        taskService.updateTaskStatus(taskId, req);
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode._OK, null));
    }

    // 업무 일정 수정(시작/마감)
    @PatchMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse<Void>> updateTaskSchedule(
            @PathVariable UUID taskId,
            @RequestBody @Valid TaskScheduleUpdateReq req
    ) {
        taskService.updateTaskSchedule(taskId, req);
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode._OK, null));
    }

    // 담당자 변경(전체 교체)
    @PatchMapping("/tasks/{taskId}/assignees")
    public ResponseEntity<ApiResponse<Void>> updateTaskAssignees(
            @PathVariable UUID taskId,
            @RequestBody @Valid TaskAssigneeUpdateReq req
    ) {
        taskService.updateTaskAssignees(taskId, req);
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode._OK, null));
    }

    // 업무 삭제(soft delete)
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable UUID taskId
    ) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode._OK, null));
    }
}
