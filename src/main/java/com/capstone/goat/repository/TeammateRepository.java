package com.capstone.goat.repository;

import com.capstone.goat.domain.Teammate;
import com.capstone.goat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeammateRepository extends JpaRepository<Teammate,Long> {
    Optional<Teammate> findFirstByUserOrderByIdDesc(User user);
    List<Teammate> findAllByUserOrderByIdDesc(User user);
    List<Teammate> findAllByGameIdAndTeamNumber(Long gameId, Integer teamNumber);
    @Query("select t.user from Teammate t where t.game.id = :gameId")
    List<User> findUsersByGameId(Long gameId);
    Optional<Teammate> findByUserIdAndGameId(Long userId, Long gameId);
    List<Teammate> findByGameId(Long gameId);
}
