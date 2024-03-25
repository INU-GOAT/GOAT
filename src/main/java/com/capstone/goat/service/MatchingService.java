package com.capstone.goat.service;

import com.capstone.goat.domain.Matching;
import com.capstone.goat.domain.Sport;
import com.capstone.goat.dto.MatchingConditionDto;
import com.capstone.goat.repository.MatchMakingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final MatchMakingRepository matchMakingRepository;

    @Transactional
    public List<Integer> findMatching(MatchingConditionDto matchingConditionDto) {

        Sport sport = Sport.getSport(matchingConditionDto.getSport());
        Integer latitude = matchingConditionDto.getLatitude();
        Integer longitude = matchingConditionDto.getLongitude();
        LocalDateTime matchingStartTime = matchingConditionDto.getMatchingStartTime();
        List<String> startTimeList = matchingConditionDto.getStartTimeList();
        String preferGender = matchingConditionDto.getPreferGender();   // 프론트에서 O/X로만 보내면 O일 경우 user 성별 확인 후 남자는 M, 여자는 F
        String preferCourt = matchingConditionDto.getPreferCourt();
        Integer userCount = matchingConditionDto.getUserCount();
        Integer groupId = matchingConditionDto.getGroupId();

        for (String startTime : startTimeList) {

            Matching matching = Matching.builder()
                    .sport(sport)
                    .latitude(latitude)
                    .longitude(longitude)
                    .matchingStartTime(matchingStartTime)
                    .startTime(startTime)
                    .preferGender(preferGender)
                    .preferCourt(preferCourt)
                    .userCount(userCount)
                    .groupId(groupId).build();

            // 매칭 대기열에 추가
            matchMakingRepository.save(matching);

            // 조건에 맞는 매칭 중인 유저 검색
            List<Matching> matchingList = matchMakingRepository.findByMatching(matching);

            // 검색한 리스트가 비어있으면 다음으로
            if (matchingList.size() <= 1) continue;

            // 투 포인터 알고리즘
            int start = 0;
            int end = 0;
            int sum = 0;
            int player = sport.getPlayer();
            List<Integer> team = new ArrayList<>();

            while (start < matchingList.size()) {
                if (sum > player || end == matchingList.size()) {
                    sum -= matchingList.get(start).getUserCount();
                    start++;
                } else {
                    sum += matchingList.get(end).getUserCount();
                    end++;
                }
                if (sum == player) {
                    if (team.isEmpty()) {
                        for (int i = start; i < end; i++)  team.add(matchingList.get(i).getGroupId());
                        start = end;
                        sum = 0;
                    }
                    else {
                        for (int i = start; i < end; i++) team.add(matchingList.get(i).getGroupId());
                        break;
                    }
                }
            }

            // 매칭 성공했으면
            if (start != end) {
                // MatchingRepository에서 제거
                for (Integer matchedGroupId : team) {
                    matchMakingRepository.deleteByGroupIdAndLatitudeAndLongitude(matchedGroupId, latitude, longitude);
                }

                return team;
            }
        }

        return null;
    }

    @Transactional
    public void deleteMatching(Integer groupId) {

    }

}
