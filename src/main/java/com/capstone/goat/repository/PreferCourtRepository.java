package com.capstone.goat.repository;

import com.capstone.goat.domain.PreferCourt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreferCourtRepository extends JpaRepository<PreferCourt, Long> {
    PreferCourt findFirstByCourtAndGameId(String court, Long gameId);
    List<PreferCourt> findAllByGameId(Long gameId);
}
