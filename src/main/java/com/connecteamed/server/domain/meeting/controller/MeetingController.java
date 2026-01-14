package com.connecteamed.server.domain.meeting.controller;

import com.connecteamed.server.domain.meeting.dto.MeetingCreateRes;
import com.connecteamed.server.domain.meeting.dto.MeetingDetailRes;
import com.connecteamed.server.domain.meeting.dto.MeetingListRes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MeetingController {

    // 회의록 생성(작성)
    @PostMapping("/projects/{projectId}/meetings")
    public ResponseEntity<MeetingCreateRes> createMeeting(...) {...}

    // 회의록 수정
    @PostMapping("/meetings/{meetingId}")
    public ResponseEntity<MeetingDetailRes> updateMeeting(...) {...}

    // 회의록 목록 조회
    @GetMapping("/projects/{projectId}/meetings")
    public ResponseEntity<MeetingListRes> getMeetings(...) {...}

    // 회의록 상세 조회
    @GetMapping("/meetings/{meetingId}")
    public ResponseEntity<MeetingDetailRes> getDetailMeetings(...) {...}
}
