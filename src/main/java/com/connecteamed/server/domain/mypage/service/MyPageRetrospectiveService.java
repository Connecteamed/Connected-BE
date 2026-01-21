package com.connecteamed.server.domain.mypage.service;


import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.member.repository.MemberRepository;
import com.connecteamed.server.domain.mypage.code.MyPageErrorCode;
import com.connecteamed.server.domain.mypage.dto.MyPageRetrospectiveRes;
import com.connecteamed.server.domain.retrospective.entity.AiRetrospective;
import com.connecteamed.server.domain.retrospective.repository.RetrospectiveRepository;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import com.connecteamed.server.global.auth.exception.AuthException;
import com.connecteamed.server.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageRetrospectiveService {


    private final RetrospectiveRepository retrospectiveRepository;
    private final MemberRepository memberRepository;


    public MyPageRetrospectiveRes.RetrospectiveList getMyRetrospectives() {

        String loginId = SecurityUtil.getCurrentLoginId();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new GeneralException(MyPageErrorCode.MEMBER_NOT_FOUND));

        List<AiRetrospective> retrospectives = retrospectiveRepository
                .findAllByWriterMemberAndDeletedAtIsNullOrderByCreatedAtDesc(member);

        List<MyPageRetrospectiveRes.RetrospectiveInfo> infoList = retrospectives.stream()
                .map(r -> MyPageRetrospectiveRes.RetrospectiveInfo.builder()
                        .id(r.getId())
                        .title(r.getTitle())
                        .createdAt(r.getCreatedAt())
                        .build())
                .toList();

        return MyPageRetrospectiveRes.RetrospectiveList.builder()
                .retrospectives(infoList)
                .build();
    }



    @Transactional
    public void deleteRetrospective(Long retrospectiveId) {

        String loginId = SecurityUtil.getCurrentLoginId();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new GeneralException(MyPageErrorCode.RETROSPECTIVE_MEMBER_NOT_FOUND));

        AiRetrospective retrospective = retrospectiveRepository.findById(retrospectiveId)
                .orElseThrow(() -> new GeneralException(MyPageErrorCode.RETROSPECTIVE_NOT_FOUND));

        if (retrospective.getDeletedAt() != null) {
            throw new GeneralException(MyPageErrorCode.RETROSPECTIVE_ALREADY_DELETED);
        }

        if (!retrospective.getWriter().getMember().getId().equals(member.getId())) {
            throw new AuthException(MyPageErrorCode.RETROSPECTIVE_NOT_WRITER);
        }

        retrospective.updateDeletedAt(Instant.now());
    }

}
