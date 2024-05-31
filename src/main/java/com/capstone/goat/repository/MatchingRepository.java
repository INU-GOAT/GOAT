package com.capstone.goat.repository;


import com.capstone.goat.domain.Matching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    Optional<Matching> findByGroupId(long groupId);

    void deleteByGroupId(long groupId);

    @Query(value = "select * from matching m where timestampdiff(minute, m.matching_start_time, :now) % 1 = 0 and timestampdiff(minute, m.matching_start_time, :now) != 0", nativeQuery = true)
    List<Matching> findOldMatchingList(LocalDateTime now);
}
