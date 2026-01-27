package com.connecteamed.server.domain.task.controller;

import com.connecteamed.server.domain.task.code.TaskSuccessCode;
import com.connecteamed.server.domain.task.dto.*;
import com.connecteamed.server.domain.task.service.TaskService;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.apiPayload.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Project-Task", description = "프로젝트 업무 관련 API")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "업무 추가", description = "업무 추가 API입니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "업무 생성 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "업무 생성 성공 예시",
                    value = """
                    {
                        "status": "success",
                        "data": {
                            "taskId": 17
                        },
                        "message": "업무 생성에 성공하였습니다.",
                        "code": null
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createTask(
            @PathVariable Long projectId,
            @RequestBody @Valid TaskCreateReq req
    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(TaskSuccessCode.TASK_CREATE_SUCCESS, Map.of("taskId", taskService.createTask(projectId, req))));
    }

    @Operation(summary = "업무 목록 조회(전체)", description = "업무 목록 조회(전체) API입니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "업무 목록 조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "업무 목록 조회 성공 예시",
                    value = """
                    {
                        "status": "success",
                        "data": [
                            {
                            "taskId": 1,
                            "name": "string",
                            "content": "string",
                            "status": "TODO",
                            "startDate": "2026-01-22T08:39:00.260Z",
                            "dueDate": "2026-01-22T08:39:00.260Z",
                            "assignees": [
                                {
                                "projectMemberId": 13,
                                "memberId": 1,
                                "memberName": "테스트유저"
                                },
                                {
                                "projectMemberId": 14,
                                "memberId": 2,
                                "memberName": "string"
                                },
                                {
                                "projectMemberId": 15,
                                "memberId": 3,
                                "memberName": "string2"
                                }
                            ]
                            },
                        ],
                        "message": "업무 목록 조회에 성공하였습니다.",
                        "code": null
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<ApiResponse<List<TaskSummaryRes>>> getProjectTasks(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(TaskSuccessCode.TASK_LIST_GET_SUCCESS, taskService.getProjectTasks(projectId)));
    }

    @Operation(summary = "업무 상세 조회", description = "업무 상세 조회 API입니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "업무 상세 조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "업무 상세 조회 성공 예시",
                    value = """
                    {
                        "status": "success",
                        "data": {
                            "taskId": 11,
                            "projectId": 1,
                            "name": "string",
                            "content": "string",
                            "status": "TODO",
                            "startDate": "2026-01-22T08:39:00.260Z",
                            "dueDate": "2026-01-22T08:39:00.260Z",
                            "assignees": [
                            {
                                "projectMemberId": 15,
                                "memberId": 3,
                                "memberName": "string2"
                            },
                            {
                                "projectMemberId": 13,
                                "memberId": 1,
                                "memberName": "테스트유저"
                            },
                            {
                                "projectMemberId": 14,
                                "memberId": 2,
                                "memberName": "string"
                            }
                            ],
                            "createdAt": "2026-01-22T08:39:12.213578Z",
                            "updatedAt": "2026-01-22T08:39:12.213578Z"
                        },
                        "message": "업무 상세 조회에 성공하였습니다.",
                        "code": null
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/tasks/{taskId}/detail")
    public ResponseEntity<ApiResponse<TaskDetailRes>> getTaskDetail(
            @PathVariable Long taskId
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(TaskSuccessCode.TASK_DETAIL_GET_SUCCESS, taskService.getTaskDetail(taskId)));
    }

    //TODO: updateTaskStatus 중복-> TaskController
    // @Operation(summary = "업무 상태 변경", description = "업무 상태 변경 API입니다.")
    // @PatchMapping("/tasks/{taskId}/status")
    // public ResponseEntity<ApiResponse<Void>> updateTaskStatus(
    //         @PathVariable Long taskId,
    //         @RequestBody @Valid TaskStatusUpdateReq req
    // ) {
    //     taskService.updateTaskStatus(taskId, req);
    //     return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode._OK, null));
    // }

    @Operation(summary = "업무 일정 수정(시작/마감)", description = "업무 일정 수정(시작/마감) API입니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "업무 일정 수정 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "업무 일정 수정 성공 예시",
                    value = """
                    {
                        "status": "success",
                        "data": null,
                        "message": "업무 일정 수정에 성공하였습니다.",
                        "code": null
                    }
                    """
                )
            )
        )
    })
    @PatchMapping("/tasks/{taskId}/schedule")
    public ResponseEntity<ApiResponse<Void>> updateTaskSchedule(
            @PathVariable Long taskId,
            @RequestBody @Valid TaskScheduleUpdateReq req
    ) {
        taskService.updateTaskSchedule(taskId, req);
        return ResponseEntity.ok(ApiResponse.onSuccess(TaskSuccessCode.TASK_SCHEDULE_UPDATE_SUCCESS, null));
    }

    @Operation(summary = "담당자 변경(전체 교체)", description = "담당자 변경(전체 교체) API입니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "업무 담당자 변경 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "업무 담당자 변경 성공 예시",
                    value = """
                    {
                        "status": "success",
                        "data": null,
                        "message": "업무 담당자 변경에 성공하였습니다.",
                        "code": null
                    }
                    """
                )
            )
        )
    })
    @PatchMapping("/tasks/{taskId}/assignees")
    public ResponseEntity<ApiResponse<Void>> updateTaskAssignees(
            @PathVariable Long taskId,
            @RequestBody @Valid TaskAssigneeUpdateReq req
    ) {
        taskService.updateTaskAssignees(taskId, req);
        return ResponseEntity.ok(ApiResponse.onSuccess(TaskSuccessCode.TASK_ASSIGNEES_UPDATE_SUCCESS, null));
    }

    // TODO: 업무 삭제 CompletedTaskController 중복 해결 필요
    // @Operation(summary = "업무 삭제(soft delete)", description = "업무 삭제(soft delete) API입니다.")
    // @DeleteMapping("/tasks/{taskId}")
    // public ResponseEntity<ApiResponse<Void>> deleteTask(
    //         @PathVariable Long taskId
    // ) {
    //     taskService.deleteTask(taskId);
    //     return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode._OK, null));
    // }
}
