package com.capstone.goat.repository;

import com.capstone.goat.domain.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClubRepository extends JpaRepository<Club,Long> {
    boolean existsByName(String name);
    @Query("SELECT c FROM Club c WHERE SIZE(c.members) < 20")
    List<Club> findClubsWithLessThan20Members();
}
