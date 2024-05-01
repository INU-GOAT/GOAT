package com.capstone.goat.repository;


import com.capstone.goat.domain.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    Optional<Matching> findByGroupId(long groupId);

    void deleteByGroupId(long groupId);
}
