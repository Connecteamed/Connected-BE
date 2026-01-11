package com.connecteamed.server.domain.meeting.entity;

import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "meeting_attendee",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_meeting_attendee_meeting_member",
                        columnNames = {"meeting_id", "attendee_id"} // 조합 unique 반영
                )
        }
)
public class MeetingAttendee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendee_id", nullable = false)
    private ProjectMember projectMember;
}
