package com.connecteamed.server.domain.myPage.controller;

import com.connecteamed.server.domain.myPage.code.MyPageSuccessCode;
import com.connecteamed.server.domain.myPage.service.MyPageProjectService;
import com.connecteamed.server.domain.myPage.dto.MyPageProjectListRes;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MyPage", description = "마이페이지 관련 API")
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageProjectController {

    private final MyPageProjectService myPageProjectService;

    @Operation(summary = "내가 완료한 프로젝트 목록 조회", description = "현재 로그인한 사용자가 참여한 프로젝트 중 완료(COMPLETED) 상태인 목록을 조회합니다.")
    @GetMapping("/projects/completed")
    public ApiResponse<MyPageProjectListRes.CompletedProjectList> getCompletedProjects() {
        return ApiResponse.onSuccess(MyPageSuccessCode.OK,myPageProjectService.getMyCompletedProjects());
    }

    @Operation(summary = "완료한 프로젝트 삭제", description = "특정 프로젝트 이력을 삭제합니다. 프로젝트 소유자(Owner)만 삭제 가능하게 구현했습니다.")
    @DeleteMapping("/projects/{projectId}")
    public ApiResponse<Void> deleteProject(@PathVariable Long projectId) {
        myPageProjectService.deleteCompletedProject(projectId);
        return ApiResponse.onSuccess(MyPageSuccessCode.OK, null);
    }

}