package com.connecteamed.server.domain.retrospective.service;


import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.member.repository.MemberRepository;
import com.connecteamed.server.domain.retrospective.code.RetrospectiveErrorCode;
import com.connecteamed.server.domain.retrospective.dto.RetrospectiveRes;
import com.connecteamed.server.domain.retrospective.entity.AiRetrospective;
import com.connecteamed.server.domain.retrospective.repository.RetrospectiveRepository;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import com.connecteamed.server.global.auth.exception.AuthException;
import com.connecteamed.server.global.auth.exception.code.AuthErrorCode;
import com.connecteamed.server.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrospectiveService {


    private final RetrospectiveRepository retrospectiveRepository;
    private final MemberRepository memberRepository;


    public RetrospectiveRes.RetrospectiveList getMyRetrospectives() {

        String loginId = SecurityUtil.getCurrentLoginId();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        List<AiRetrospective> retrospectives = retrospectiveRepository
                .findAllByWriterMemberAndDeletedAtIsNullOrderByCreatedAtDesc(member);

        List<RetrospectiveRes.RetrospectiveInfo> infoList = retrospectives.stream()
                .map(r -> RetrospectiveRes.RetrospectiveInfo.builder()
                        .id(r.getId())
                        .title(r.getTitle())
                        .createdAt(r.getCreatedAt())
                        .build())
                .toList();

        return RetrospectiveRes.RetrospectiveList.builder()
                .retrospectives(infoList)
                .build();
    }



    @Transactional
    public void deleteRetrospective(Long retrospectiveId) {

        String loginId = SecurityUtil.getCurrentLoginId();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new GeneralException(RetrospectiveErrorCode.RETROSPECTIVE_MEMBER_NOT_FOUND));

        AiRetrospective retrospective = retrospectiveRepository.findById(retrospectiveId)
                .orElseThrow(() -> new GeneralException(RetrospectiveErrorCode.RETROSPECTIVE_NOT_FOUND));

        if (retrospective.getDeletedAt() != null) {
            throw new GeneralException(RetrospectiveErrorCode.RETROSPECTIVE_ALREADY_DELETED);
        }

        if (!retrospective.getWriter().getMember().getId().equals(member.getId())) {
            throw new AuthException(RetrospectiveErrorCode.RETROSPECTIVE_NOT_WRITER);
        }

        retrospective.updateDeletedAt(Instant.now());
    }

}
