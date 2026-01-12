package com.connecteamed.server.domain.task.entity;


import com.connecteamed.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@Getter
@Table(name="task_note")
public class TaskNote extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @OneToOne(fetch =FetchType.LAZY)
    @JoinColumn(name="task_assignee_id",nullable = false,unique = true)
    private TaskAssignee taskAssignee;

    @Column(name="content",nullable = false,columnDefinition = "TEXT")
    private String content;
}
