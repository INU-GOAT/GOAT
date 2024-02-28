package com.capstone.goat.service;

import com.capstone.goat.domain.Matching;
import com.capstone.goat.domain.Sport;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.MatchingConditionDto;
import com.capstone.goat.repository.MatchingRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final MatchingRepository matchingRepository;

    @Transactional
    public void findMatching(User user, MatchingConditionDto matchingConditionDto) {

        Sport sport = Sport.getSport(matchingConditionDto.getSport());
        Integer latitude = matchingConditionDto.getLatitude();
        Integer longitude = matchingConditionDto.getLongitude();
        List<LocalDateTime> startTimeList = matchingConditionDto.getStartTimeList();
        String preferGender = matchingConditionDto.getPreferGender();   // 프론트에서 O/X로만 보내면 O일 경우 user 성별 확인 후 남자는 M, 여자는 F
        //String preferCourt = matchingConditionDto.getPreferCourt();

        for (LocalDateTime startTime : startTimeList) {

            List<Matching> matchingList = matchingRepository.findByConditions(sport, latitude, longitude, startTime, preferGender);

        }

    }

}
