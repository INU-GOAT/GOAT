package com.capstone.goat.repository;

import com.capstone.goat.domain.Club;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club,Long> {
    boolean existsByName(String name);
}
