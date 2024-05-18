package com.capstone.goat.repository;

import com.capstone.goat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByNickname(String nickname);
}
