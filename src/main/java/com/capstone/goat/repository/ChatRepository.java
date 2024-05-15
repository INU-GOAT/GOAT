package com.capstone.goat.repository;

import com.capstone.goat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Long> {
    List<Chat> findAllByGameId(Long gameId);
}
