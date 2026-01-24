package com.connecteamed.server.domain.invite.entity;

import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Entity
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@Getter
@Table(name= "invite_code")
public class InviteCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "expired_at", nullable = false)
    private Instant expiredAt;

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiredAt);
    }




}
