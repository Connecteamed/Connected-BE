package com.connecteamed.server.domain.member.entity;


import com.connecteamed.server.domain.member.enums.SocialType;
import com.connecteamed.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;


@Entity
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@Getter
@Table(name= "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="public_id", nullable = false,unique = true, columnDefinition = "UUID")
    private UUID publicId;

    @Column(name="name",nullable = false)
    private String name;

    @Column(name="login_id",unique = true)
    private String loginId; //소셜 로그인 시 NULL 가능

    @Column(name="password")
    private String password; //소셜 로그인 시 NULL 가능

    @Enumerated(EnumType.STRING)
    @Column(name="social_type",nullable = false)
    private SocialType socialType;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    //객체 생성시에 public_id 자동 생성해주는 빌더
    @PrePersist
    public void prePersist(){
        if(this.publicId == null){
            this.publicId = UUID.randomUUID();
        }
    }





}
