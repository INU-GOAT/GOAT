package com.capstone.goat.repository;

import com.capstone.goat.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n.sendTime FROM Notification n WHERE n.sender.id = :userId and n.type = :type")
    List<LocalDateTime> findSendTimeBySenderIdAndType(Long userId, Integer type);

    @Query("SELECT n.sendTime FROM Notification n WHERE n.receiver.id = :userId and n.type = :type")
    List<LocalDateTime> findSendTimeByReceiverIdAndType(Long userId, Integer type);
}