package com.connecteamed.server.domain.project.entity;

import com.connecteamed.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "project_member_role",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_member_role",
                        columnNames = {"project_member_id", "role_id"}
                )
        }
)
public class ProjectMemberRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_member_id", nullable = false)
    private ProjectMember projectMember;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private ProjectRole role;
}
