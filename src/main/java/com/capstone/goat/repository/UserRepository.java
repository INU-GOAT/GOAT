package com.capstone.goat.repository;

import com.capstone.goat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByNickname(String nickname);
    Optional<User> findByNickname(String nickname);
    @Query("select u.nickname from User u where u.id = :id")
    Optional<String> findNicknameById(Long id);
    int countByGroupId(Long groupId);
}
