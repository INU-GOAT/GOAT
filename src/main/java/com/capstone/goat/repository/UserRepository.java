package com.capstone.goat.repository;

import com.capstone.goat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByLoginId(String login_id);

    boolean existsByLoginId(String login_id);

}
