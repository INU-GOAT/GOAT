package com.capstone.goat.repository;

import com.capstone.goat.domain.Game;
import com.capstone.goat.domain.VotedCourt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VotedCourtRepository extends JpaRepository<VotedCourt,Long> {
    List<VotedCourt> findAllByGameId(Long gameId);
    Optional<VotedCourt> findByCourt(String court);
    boolean existsByCourt(String court);
}
