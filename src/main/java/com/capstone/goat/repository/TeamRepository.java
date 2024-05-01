package com.capstone.goat.repository;

import com.capstone.goat.domain.Teammate;
import com.capstone.goat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Teammate,Long> {
    Optional<Teammate> findFirstByUserOrderByIdDesc(User user);
    List<Teammate> findAllByUserOrderByIdDesc(User user);
}
