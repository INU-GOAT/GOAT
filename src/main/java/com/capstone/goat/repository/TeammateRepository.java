package com.capstone.goat.repository;

import com.capstone.goat.domain.Teammate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeammateRepository extends JpaRepository<Teammate,Long> {
    Optional<Teammate> findFirstByUserIdOrderByIdDesc(Long userId);
    List<Teammate> findAllByUserIdOrderByIdDesc(Long userId);
    List<Teammate> findAllByGameIdAndTeamNumber(Long gameId, Integer teamNumber);
    @Query("select t.userId from Teammate t where t.game.id = :gameId")
    List<Long> findUserIdsByGameId(Long gameId);
    Optional<Teammate> findByUserIdAndGameId(Long userId, Long gameId);
    List<Teammate> findByGameId(Long gameId);
}
