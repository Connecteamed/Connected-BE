package com.connecteamed.server.domain.MyPage.service;


import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.member.repository.MemberRepository;
import com.connecteamed.server.domain.myPage.code.MyPageErrorCode;
import com.connecteamed.server.domain.myPage.service.MyPageRetrospectiveService;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.domain.myPage.dto.MyPageRetrospectiveRes;
import com.connecteamed.server.domain.retrospective.entity.AiRetrospective;
import com.connecteamed.server.domain.retrospective.repository.RetrospectiveRepository;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import com.connecteamed.server.global.util.SecurityUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class MyPageRetrospectiveServiceTest {
    @InjectMocks
    private MyPageRetrospectiveService myPageRetrospectiveService;

    @Mock
    private RetrospectiveRepository retrospectiveRepository;

    @Mock
    private MemberRepository memberRepository;

    private static MockedStatic<SecurityUtil> mockedSecurityUtil;

    @BeforeAll
    static void setup() {
        mockedSecurityUtil = mockStatic(SecurityUtil.class);
    }

    @AfterAll
    static void tearDown() {
        mockedSecurityUtil.close();
    }

    @Test
    @DisplayName("내가 작성한 회고 목록 조회 성공")
    void getMyRetrospectives_Success() {
        String loginId = "test_user";
        Member member = Member.builder().id(1L).loginId(loginId).build();

        AiRetrospective retro1 = AiRetrospective.builder().id(55L).title("회고 1").build();
        AiRetrospective retro2 = AiRetrospective.builder().id(54L).title("회고 2").build();

        given(SecurityUtil.getCurrentLoginId()).willReturn(loginId);
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(member));
        given(retrospectiveRepository.findAllByWriterMemberAndDeletedAtIsNullOrderByCreatedAtDesc(member))
                .willReturn(List.of(retro1, retro2));

        MyPageRetrospectiveRes.RetrospectiveList result = myPageRetrospectiveService.getMyRetrospectives();

        assertThat(result.getRetrospectives()).hasSize(2);
        assertThat(result.getRetrospectives().get(0).getTitle()).isEqualTo("회고 1");
        assertThat(result.getRetrospectives().get(1).getId()).isEqualTo(54L);
    }

    @Test
    @DisplayName("작성한 회고 삭제 성공")
    void deleteRetrospective_Success() {
        String loginId = "writer_user";
        Long retroId = 55L;

        Member member = Member.builder().id(1L).loginId(loginId).build();
        ProjectMember writer = ProjectMember.builder().member(member).build();

        AiRetrospective retrospective = AiRetrospective.builder()
                .id(retroId)
                .writer(writer)
                .deletedAt(null)
                .build();

        given(SecurityUtil.getCurrentLoginId()).willReturn(loginId);
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(member));
        given(retrospectiveRepository.findById(retroId)).willReturn(Optional.of(retrospective));

        myPageRetrospectiveService.deleteRetrospective(retroId);

        assertThat(retrospective.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("작성한 회고 삭제 요청 실패 - 작성자가 아닌 경우")
    void deleteRetrospective_NotWriter_Fail() {
        String loginId = "other_user";
        Long retroId = 55L;

        Member writerMember = Member.builder().id(1L).loginId("actual_writer").build();
        Member otherMember = Member.builder().id(2L).loginId(loginId).build();

        ProjectMember writer = ProjectMember.builder().member(writerMember).build();
        AiRetrospective retrospective = AiRetrospective.builder()
                .id(retroId)
                .writer(writer)
                .build();

        given(SecurityUtil.getCurrentLoginId()).willReturn(loginId);
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(otherMember));
        given(retrospectiveRepository.findById(retroId)).willReturn(Optional.of(retrospective));

        GeneralException exception = assertThrows(GeneralException.class, () ->
                myPageRetrospectiveService.deleteRetrospective(retroId)
        );

        assertEquals(MyPageErrorCode.RETROSPECTIVE_NOT_WRITER, exception.getCode());
        assertThat(retrospective.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("작성한 회고 삭제 요청 실패 - 이미 삭제된 회고인 경우")
    void deleteRetrospective_AlreadyDeleted_Fail() {
        String loginId = "writer_user";
        Long retroId = 55L;

        Member member = Member.builder().id(1L).loginId(loginId).build();
        ProjectMember writer = ProjectMember.builder().member(member).build();

        AiRetrospective retrospective = AiRetrospective.builder()
                .id(retroId)
                .writer(writer)
                .deletedAt(Instant.now())
                .build();

        given(SecurityUtil.getCurrentLoginId()).willReturn(loginId);
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(member));
        given(retrospectiveRepository.findById(retroId)).willReturn(Optional.of(retrospective));

        GeneralException exception = assertThrows(GeneralException.class, () ->
                myPageRetrospectiveService.deleteRetrospective(retroId)
        );

        assertEquals(MyPageErrorCode.RETROSPECTIVE_ALREADY_DELETED, exception.getCode());
    }

}
