package com.capstone.goat.repository;

import com.capstone.goat.domain.Group;
import com.capstone.goat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group,Long> {

    // TODO N+1 문제 해결해야 할 듯
    @Query("select g.members from Group g where g.id = :groupId")
    List<User> findUsersById(long groupId);
}
