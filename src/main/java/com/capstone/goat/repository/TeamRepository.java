package com.capstone.goat.repository;

import com.capstone.goat.domain.Teammate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Teammate,Long> {
}
