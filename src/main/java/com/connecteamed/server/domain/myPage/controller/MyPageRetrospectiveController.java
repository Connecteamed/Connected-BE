package com.connecteamed.server.domain.myPage.controller;


import com.connecteamed.server.domain.myPage.code.MyPageSuccessCode;
import com.connecteamed.server.domain.myPage.dto.MyPageRetrospectiveRes;
import com.connecteamed.server.domain.myPage.service.MyPageRetrospectiveService;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MyPage", description = "마이페이지 관련 API")
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageRetrospectiveController {
    private final MyPageRetrospectiveService myPageRetrospectiveService;

    @Operation(summary = "내가 작성한 회고 목록 조회", description = "사용자가 작성한 모든 회고 목록을 최신순으로 조회합니다.")
    @GetMapping("/retrospectives")
    public ApiResponse<MyPageRetrospectiveRes.RetrospectiveList> getMyRetrospectives() {
        return ApiResponse.onSuccess(MyPageSuccessCode.OK,myPageRetrospectiveService.getMyRetrospectives());
    }

    @Operation(summary = "작성한 회고 삭제", description = "작성한 회고를 삭제 처리합니다. 본인이 작성한 회고만 삭제 가능합니다.")
    @DeleteMapping("/retrospectives/{retrospectiveId}")
    public ApiResponse<Void> deleteRetrospective(@PathVariable Long retrospectiveId) {
        myPageRetrospectiveService.deleteRetrospective(retrospectiveId);

        return ApiResponse.onSuccess(MyPageSuccessCode.RETROSPECTIVE_DELETED, null);
    }
}
