package com.capstone.goat.repository;

import com.capstone.goat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByNickname(String nickname);
    Optional<User> findByNickname(String nickname);
}
