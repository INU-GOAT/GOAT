package com.capstone.goat.service;

import com.capstone.goat.domain.Notification;
import com.capstone.goat.domain.NotificationType;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.response.NotificationResponseDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.EmitterRepository;
import com.capstone.goat.repository.NotificationRepository;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
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

        User receiver = getUserByNickname(receiverNickname);

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
                .content(message)
                .build();

        notify(sender != null ? sender.getNickname() : null, receiver.getId(), notification);

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

    private User getUserByNickname(String userNickname) {
        return userRepository.findByNickname(userNickname)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    // 일반 Notification 서비스
    /*-------------------------------------------------------------------------------------------------*/
    // SSE Notification 서비스

    // 기본 타임아웃 설정 => 5분
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 5;

    private final EmitterRepository emitterRepository;

    /**
     * 클라이언트가 구독을 위해 호출하는 메서드.
     *
     * @param userId - 구독하는 클라이언트의 사용자 아이디.
     * @return SseEmitter - 서버에서 보낸 이벤트 Emitter
     */
    public SseEmitter connect(Long userId) {
        SseEmitter emitter = createEmitter(userId);

        sendToClient(userId, "[SSE] EventStream Created. [userId=" + userId + "]");
        return emitter;
    }

    public void disconnect(Long userId) {
        SseEmitter emitter = emitterRepository.get(userId);
        emitter.complete();
        emitterRepository.deleteById(userId);
    }

    /**
     * 서버의 이벤트를 클라이언트에게 보내는 메서드
     * 다른 서비스 로직에서 이 메서드를 사용해 데이터를 Object event에 넣고 전송하면 된다.
     *
     * @param senderNickname - 메세지를 전송할 사용자의 닉네임.
     * @param receiverId - 메세지를 받을 사용자의 아이디.
     * @param notification  - 전송할 이벤트 객체.
     */
    public void notify(String senderNickname, Long receiverId, Notification notification) {

        NotificationResponseDto event = NotificationResponseDto.of(notification, senderNickname);
        sendToClient(receiverId, event);
    }

    /**
     * 클라이언트에게 데이터를 전송
     *
     * @param receiverId   - 데이터를 받을 사용자의 아이디.
     * @param data - 전송할 데이터.
     */
    private void sendToClient(Long receiverId, Object data) {
        SseEmitter emitter = emitterRepository.get(receiverId);
        if (emitter != null) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .id(String.valueOf(receiverId))
                                .name("[SSE] connect")
                                .data(data)
                );
            } catch (IOException exception) {
                emitterRepository.deleteById(receiverId);
                emitter.completeWithError(exception);
            }
        }
    }

    /**
     * 사용자 아이디를 기반으로 이벤트 Emitter를 생성
     *
     * @param id - 사용자 아이디.
     * @return SseEmitter - 생성된 이벤트 Emitter.
     */
    private SseEmitter createEmitter(Long id) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(id, emitter);

        // Emitter가 완료될 때(모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다.
        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        // Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        return emitter;
    }
}
