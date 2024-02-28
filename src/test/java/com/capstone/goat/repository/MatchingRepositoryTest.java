package com.capstone.goat.repository;

import com.capstone.goat.domain.Matching;
import com.capstone.goat.domain.Sport;
import com.capstone.goat.dto.MatchingConditionDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MatchingRepositoryTest {

    @Autowired MatchingRepository matchingRepository;

    @Commit
    @Test
    @DisplayName("매칭 추가")
    void createMatching() {
        Matching matching = Matching.builder()
                .preferGender("X")
                .sport(Sport.SOCCER)
                .latitude(123123)
                .longitude(321321)
                .startTime(LocalDateTime.of(2024, 2, 29, 15, 00))
                .build();
        matchingRepository.save(matching);
    }

    @Test
    @DisplayName("매칭 조건으로 검색")
    void findByConditions() {

        List<LocalDateTime> startTimeList = new ArrayList<LocalDateTime>();
        startTimeList.add(LocalDateTime.of(2024, 2, 29, 4, 30));

        MatchingConditionDto matchingConditionDto = MatchingConditionDto.builder()
                .preferGender("X")
                .sport("축구")
                .latitude(321321)
                .longitude(123123)
                .startTimeList(startTimeList)
                .build();   // X:상관없음, M:동성(남자만), F:동성(여자만)

        Sport sport = Sport.getSport(matchingConditionDto.getSport());
        Integer latitude = matchingConditionDto.getLatitude();
        Integer longitude = matchingConditionDto.getLongitude();
        LocalDateTime startTime = matchingConditionDto.getStartTimeList().get(0);
        String preferGender = matchingConditionDto.getPreferGender();

        List<Matching> matchingList = matchingRepository.findByConditions(sport, latitude, longitude, startTime, preferGender);
        System.out.println("SOUT "+ matchingList.get(0));
    }
}