package com.connecteamed.server.domain.invite.service;

import com.connecteamed.server.domain.invite.dto.InviteCodeRes;
import com.connecteamed.server.domain.invite.entity.InviteCode;
import com.connecteamed.server.domain.invite.repository.InviteCodeRepository;
import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.member.repository.MemberRepository;
import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.repository.ProjectMemberRepository;
import com.connecteamed.server.domain.project.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

    @Mock
    private InviteCodeRepository inviteCodeRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;
    @Mock private MemberRepository memberRepository;

    @InjectMocks
    private InviteService inviteService;

    @Test
    @DisplayName("초대 코드 발급 - 기존 유효 코드가 1시간 이상 남았으면 재사용하는지 확인")
    void getOrGenerateInviteCode_ReuseExisting() {
        // given
        Long projectId = 1L;
        Project project = Project.builder().id(projectId).build();
        String loginId = "testUser";
        Member member = Member.builder().id(10L).loginId(loginId).build();
        InviteCode existingCode = InviteCode.builder()
                .code("OLDCODE1")
                .expiredAt(Instant.now().plus(Duration.ofHours(5))) // 5시간 남음
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(memberRepository.findByLoginId(loginId)).thenReturn(Optional.of(member));
        when(projectMemberRepository.existsByProjectIdAndMemberId(projectId, member.getId())).thenReturn(true);
        when(inviteCodeRepository.findTopByProjectIdAndExpiredAtAfterOrderByCreatedAtDesc(
                eq(projectId),
                any(Instant.class)
        )).thenReturn(Optional.of(existingCode));

        // when
        InviteCodeRes result = inviteService.getOrGenerateInviteCode(projectId, loginId);

        // then
        assertThat(result.getInviteCode()).isEqualTo("OLDCODE1");
        verify(inviteCodeRepository, times(0)).save(any()); // 저장하지 않음
    }


    @Test
    @DisplayName("초대 코드 발급 - 기존 코드가 만료 1시간 이내라면 새로 생성하는지 확인")
    void getOrGenerateInviteCode_GenerateNewWhenExpiringSoon() {
        // given
        Long projectId = 1L;
        Project project = Project.builder().id(projectId).build();
        String loginId = "testUser";
        Member member = Member.builder().id(10L).loginId(loginId).build();

        // 1. 30분 뒤에 만료되는 기존 코드 설정 (재사용 기준인 1시간 미만)
        InviteCode expiringSoonCode = InviteCode.builder()
                .code("EXPIRING_SOON")
                .expiredAt(Instant.now().plus(Duration.ofMinutes(30)))
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(memberRepository.findByLoginId(loginId)).thenReturn(Optional.of(member));
        when(projectMemberRepository.existsByProjectIdAndMemberId(projectId, member.getId())).thenReturn(true);

        // 2. 레포지토리에서 임박한 코드가 조회됨
        when(inviteCodeRepository.findTopByProjectIdAndExpiredAtAfterOrderByCreatedAtDesc(anyLong(), any(Instant.class)))
                .thenReturn(Optional.of(expiringSoonCode));

        // 3. 새 코드 생성 시 중복 체크는 한 번에 통과한다고 가정
        when(inviteCodeRepository.existsByCode(anyString())).thenReturn(false);

        // when
        InviteCodeRes result = inviteService.getOrGenerateInviteCode(projectId, loginId);

        // then
        // 1. 반환된 코드가 기존의 "EXPIRING_SOON"이 아니어야 함
        assertThat(result.getInviteCode()).isNotEqualTo("EXPIRING_SOON");
        assertThat(result.getInviteCode()).hasSize(8); // 8자리 UUID substring 확인

        // 2. 새 코드가 DB에 저장되었는지 확인 (save 호출 횟수 1회)
        verify(inviteCodeRepository, times(1)).save(any(InviteCode.class));

    }





}
