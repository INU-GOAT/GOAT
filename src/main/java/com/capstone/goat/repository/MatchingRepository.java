package com.capstone.goat.repository;


import com.capstone.goat.domain.Matching;
import com.capstone.goat.domain.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchingRepository extends JpaRepository<Matching, Integer> {

    @Query("SELECT m FROM Matching m WHERE m.sport = :sport AND m.startTime = :startTime AND m.preferGender = :preferGender " +
            "AND ((m.latitude - :latitude) BETWEEN -500000 AND 500000) AND ((m.longitude - :longitude) BETWEEN -500000 AND 500000)")
    List<Matching> findByConditions(Sport sport, Integer latitude, Integer longitude, LocalDateTime startTime, String preferGender);
}
