package com.capstone.goat.repository;

import com.capstone.goat.domain.Notification;
import com.capstone.goat.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n.sendTime FROM Notification n WHERE n.sender.id = :userId and n.type = :type")
    List<LocalDateTime> findSendTimeBySenderIdAndType(Long userId, NotificationType type);
    @Query("SELECT n.sendTime FROM Notification n WHERE n.receiver.id = :userId and n.type = :type")
    List<LocalDateTime> findSendTimeByReceiverIdAndType(Long userId, NotificationType type);
    List<Notification> findAllByReceiverIdOrderByIdDesc(Long receiverId);

}