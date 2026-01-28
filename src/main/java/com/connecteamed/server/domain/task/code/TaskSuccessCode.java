package com.connecteamed.server.domain.task.code;

import com.connecteamed.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TaskSuccessCode implements BaseSuccessCode {

    TASK_CREATE_SUCCESS(HttpStatus.OK, "TASK_CREATE_SUCCESS", "업무 생성에 성공하였습니다."),
    TASK_LIST_GET_SUCCESS(HttpStatus.OK, "TASK_LIST_GET_SUCCESS", "업무 목록 조회에 성공하였습니다."),
    TASK_DETAIL_GET_SUCCESS(HttpStatus.OK, "TASK_DETAIL_GET_SUCCESS", "업무 상세 조회에 성공하였습니다."),
    TASK_SCHEDULE_UPDATE_SUCCESS(HttpStatus.OK, "TASK_SCHEDULE_UPDATE_SUCCESS", "업무 일정 수정에 성공하였습니다."),
    TASK_ASSIGNEES_UPDATE_SUCCESS(HttpStatus.OK, "TASK_ASSIGNEES_UPDATE_SUCCESS", "업무 담당자 변경에 성공하였습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
