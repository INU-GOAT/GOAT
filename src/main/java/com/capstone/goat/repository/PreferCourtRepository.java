package com.capstone.goat.repository;

import com.capstone.goat.domain.PreferCourt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferCourtRepository extends JpaRepository<PreferCourt, Long> {
    PreferCourt findByCourtAndGameId(String court, Long gameId);
}
