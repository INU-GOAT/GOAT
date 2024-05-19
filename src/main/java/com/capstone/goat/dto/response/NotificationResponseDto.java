package com.capstone.goat.dto.response;

import com.capstone.goat.domain.Notification;
import com.capstone.goat.domain.NotificationType;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
public class NotificationResponseDto {

    private final Long id;

    private final NotificationType type;

    private final String comment;

    @CreationTimestamp
    private final LocalDateTime sendTime; // 오래된 그룹 초대 알림이면 알림 조회 시 삭제 후 반환

    private final String senderNickname;

    @Builder
    private NotificationResponseDto(Long id, NotificationType type, String comment, LocalDateTime sendTime, String senderNickname) {
        this.id = id;
        this.type = type;
        this.comment = comment;
        this.sendTime = sendTime;
        this.senderNickname = senderNickname;
    }

    public static NotificationResponseDto of(Notification notification, String senderNickname) {

        return NotificationResponseDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .comment(notification.getComment())
                .sendTime(notification.getSendTime())
                .senderNickname(senderNickname)
                .build();
    }
}
