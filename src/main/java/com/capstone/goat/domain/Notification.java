package com.capstone.goat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private NotificationType type;

    private String content;

    @CreationTimestamp
    private LocalDateTime sendTime; // 오래된 그룹 초대 알림이면 알림 조회 시 삭제 후 반환

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Builder
    private Notification(NotificationType type, String content, User receiver, User sender) {
        this.type = type;
        this.content = content;
        this.sendTime = LocalDateTime.now();
        this.receiver = receiver;
        this.sender = sender;
    }
}
