package com.capstone.goat.service;

import com.capstone.goat.domain.Sport;
import com.capstone.goat.domain.User;
import com.capstone.goat.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingService {

    private final GroupRepository groupRepository;

    // 그룹원의 평균 rating을 계산
    public int getRatingMean(long groupId, String sport) {
        List<User> userList = groupRepository.findUsersById(groupId);

        return (int) userList.stream()
                .mapToInt(user -> user.getRatings().get(Sport.getSport(sport)).getRating()) // Integer 형식으로 매핑
                .average() // OptionalDouble 반환
                .orElseThrow(() -> new NoSuchElementException("그룹원의 rating 평균을 계산하는 중 오류가 생겼습니다. 그룹이 비어있습니다."));
    }

}
