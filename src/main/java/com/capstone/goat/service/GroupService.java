package com.capstone.goat.service;

import com.capstone.goat.domain.Group;
import com.capstone.goat.domain.User;
import com.capstone.goat.repository.GroupRepository;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Transactional
    public Group addGroup(long userId) {  // 엔드포인트에서 파라미터로 받아온 엔티티는 더티 체킹이 불가능

        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("해당하는 유저가 존재하지 않습니다."));

        // 그룹장이 user인 그룹 생성
        Group group = Group.builder()
                .master(user)
                .build();

        // user에 group 지정; 더티 체킹으로 자동 저장
        user.joinGroup(group);

        return groupRepository.save(group);
    }
}
