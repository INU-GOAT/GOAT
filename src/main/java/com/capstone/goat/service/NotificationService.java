package com.capstone.goat.service;

import com.capstone.goat.domain.Notification;
import com.capstone.goat.domain.NotificationType;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.response.NotificationResponseDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.NotificationRepository;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public long sendNotification(Long senderId, String receiverNickname, NotificationType type) {

        User sender = Optional.ofNullable(senderId)
                .map(this::getUser)
                .orElse(null);

        User receiver = userRepository.findByNickname(receiverNickname)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        String message;
        if (NotificationType.MATCHING == type) {
            message = type.getMessage();
        }  else {
            message = Optional.ofNullable(sender)
                    .map(User::getNickname)
                    .orElse(null)
                    + type.getMessage();
        }

        Notification notification = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .type(type)
                .comment(message)
                .build();

        return notificationRepository.save(notification).getId();
        }

    @Transactional
    public void deleteNotification(long userId, long notificationId) {

        notificationRepository.findById(notificationId).ifPresent(notification -> {
            // 해당 유저에게 온 알림이 아니면 예외
            if (notification.getReceiver().getId() != userId) {
                throw new CustomException(CustomErrorCode.NO_AUTHORITY);
            }
            notificationRepository.deleteById(notificationId);
        });
    }

    @Transactional
    public List<NotificationResponseDto> getNotificationList(long userId) {

        List<Notification> notificationList = notificationRepository.findAllByReceiverId(userId);

        return notificationList.stream()
                .filter(notification -> {   // 그룹 초대 알림의 경우 발송된지 30초가 지났으면 삭제 후 리스트에 추가하지 않음
                    long seconds = Duration.between(notification.getSendTime(), LocalDateTime.now()).getSeconds();
                    if (seconds > 30) {
                        notificationRepository.delete(notification);
                    }
                    return NotificationType.GROUP_INVITE != notification.getType() || seconds <= 30;
                }).map(notification -> {
                    String senderNickname = Optional.ofNullable(notification.getSender())
                            .map(User::getNickname)
                            .orElse(null);
                    return NotificationResponseDto.of(notification, senderNickname);
                }).toList();
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }
}
