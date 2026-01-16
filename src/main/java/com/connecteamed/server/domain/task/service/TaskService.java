package com.connecteamed.server.domain.task.service;

import com.connecteamed.server.domain.task.dto.*;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    UUID createTask(Long projectId, TaskCreateReq req);

    List<TaskSummaryRes> getProjectTasks(Long projectId);

    TaskDetailRes getTaskDetail(UUID taskId);

    void updateTaskStatus(UUID taskId, TaskStatusUpdateReq req);

    void updateTaskSchedule(UUID taskId, TaskScheduleUpdateReq req);

    void updateTaskAssignees(UUID taskId, TaskAssigneeUpdateReq req);

    void deleteTask(UUID taskId);
}
