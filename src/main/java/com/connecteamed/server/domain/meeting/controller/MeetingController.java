package com.connecteamed.server.domain.meeting.controller;

import com.connecteamed.server.domain.meeting.dto.*;
import com.connecteamed.server.domain.meeting.service.MeetingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Meeting API", description = "회의록 생성, 수정, 조회 및 삭제 API")
@RequestMapping("/api")
public class MeetingController {

    private final MeetingService meetingService;

    // 회의록 생성(작성)
    @PostMapping("/projects/{projectId}/meetings")
    public ResponseEntity<MeetingCreateRes> createMeeting(
            @PathVariable Long projectId,
            @RequestBody MeetingCreateReq request) {
        return ResponseEntity.ok(meetingService.createMeeting(projectId, request));
    }

    // 회의록 수정
    @PostMapping("/meetings/{meetingId}")
    public ResponseEntity<MeetingDetailRes> updateMeeting(
            @PathVariable Long meetingId,
            @RequestBody MeetingUpdateReq request){
        return ResponseEntity.ok(meetingService.updateMeeting(meetingId, request));
    }

    // 회의록 목록 조회
    @GetMapping("/projects/{projectId}/meetings")
    public ResponseEntity<MeetingListRes> getMeetings(@PathVariable Long projectId) {
        return ResponseEntity.ok(meetingService.getMeetings(projectId));
    }

    // 회의록 상세 조회
    @GetMapping("/meetings/{meetingId}")
    public ResponseEntity<MeetingDetailRes> getMeeting(@PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingService.getMeeting(meetingId));
    }
}
