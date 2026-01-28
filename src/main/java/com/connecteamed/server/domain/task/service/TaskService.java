package com.connecteamed.server.domain.task.service;

import com.connecteamed.server.domain.task.dto.*;

import java.util.List;

public interface TaskService {

    Long createTask(Long projectId, TaskCreateReq req);

    List<TaskSummaryRes> getProjectTasks(Long projectId);

    TaskDetailRes getTaskDetail(Long taskId);

    void updateTaskStatus(Long taskId, TaskStatusUpdateReq req);

    void updateTaskSchedule(Long taskId, TaskScheduleUpdateReq req);

    void updateTaskAssignees(Long taskId, TaskAssigneeUpdateReq req);

    void deleteTask(Long taskId);
}
