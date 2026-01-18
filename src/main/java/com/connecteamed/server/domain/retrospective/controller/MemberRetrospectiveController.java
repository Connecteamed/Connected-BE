package com.connecteamed.server.domain.retrospective.controller;


import com.connecteamed.server.domain.project.service.ProjectService;
import com.connecteamed.server.domain.retrospective.code.RetrospectiveSuccessCode;
import com.connecteamed.server.domain.retrospective.dto.RetrospectiveRes;
import com.connecteamed.server.domain.retrospective.service.RetrospectiveService;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MyPage", description = "마이페이지 관련 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberRetrospectiveController {
    private final ProjectService projectService;
    private final RetrospectiveService retrospectiveService;

    @Operation(summary = "내가 작성한 회고 목록 조회", description = "사용자가 작성한 모든 회고 목록을 최신순으로 조회합니다.")
    @GetMapping("/me/retrospectives")
    public ApiResponse<RetrospectiveRes.RetrospectiveList> getMyRetrospectives() {
        return ApiResponse.onSuccess(RetrospectiveSuccessCode.OK,retrospectiveService.getMyRetrospectives());
    }

    @Operation(summary = "작성한 회고 삭제", description = "작성한 회고를 삭제 처리합니다. 본인이 작성한 회고만 삭제 가능합니다.")
    @DeleteMapping("/me/retrospectives/{retrospectiveId}")
    public ApiResponse<Void> deleteRetrospective(@PathVariable Long retrospectiveId) {
        retrospectiveService.deleteRetrospective(retrospectiveId);

        return ApiResponse.onSuccess(RetrospectiveSuccessCode.DELETED, null);
    }
}
