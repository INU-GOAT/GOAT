package com.capstone.goat.service;

import com.capstone.goat.domain.Game;
import com.capstone.goat.domain.MatchMaking;
import com.capstone.goat.domain.Sport;
import com.capstone.goat.domain.Teammate;
import com.capstone.goat.dto.request.MatchingConditionDto;
import com.capstone.goat.repository.GameRepository;
import com.capstone.goat.repository.GroupRepository;
import com.capstone.goat.repository.MatchMakingRepository;
import com.capstone.goat.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchMakingService {

    private final MatchMakingRepository matchMakingRepository;
    private final GroupRepository groupRepository;
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public void addMatchingAndMatchMaking(MatchingConditionDto matchingConditionDto, int rating) {

        Sport sport = Sport.getSport(matchingConditionDto.getSport());
        float latitude = matchingConditionDto.getLatitude();
        float longitude = matchingConditionDto.getLongitude();
        LocalDateTime matchingStartTime = matchingConditionDto.getMatchingStartTime();
        List<String> matchStartTimeList = matchingConditionDto.getMatchStartTimes();
        String preferCourt = matchingConditionDto.getPreferCourt();
        int userCount = matchingConditionDto.getUserCount();
        long groupId = matchingConditionDto.getGroupId();

        for (String matchStartTime : matchStartTimeList) {

            MatchMaking matchMaking = com.capstone.goat.domain.MatchMaking.builder()
                    .sport(sport)
                    .latitude(latitude)
                    .longitude(longitude)
                    .matchingStartTime(matchingStartTime)
                    .matchStartTime(matchStartTime)
                    .preferCourt(preferCourt)
                    .userCount(userCount)
                    .groupId(groupId).build();

            // 매칭 대기열에 추가
            matchMakingRepository.save(matchMaking);
        }

    }

    @Async
    @Transactional
    public void findMatching(MatchingConditionDto matchingConditionDto, int rating) {

        Sport sport = Sport.getSport(matchingConditionDto.getSport());
        float latitude = matchingConditionDto.getLatitude();
        float longitude = matchingConditionDto.getLongitude();
        LocalDateTime matchingStartTime = matchingConditionDto.getMatchingStartTime();
        List<String> matchStartTimeList = matchingConditionDto.getMatchStartTimes();
        String preferCourt = matchingConditionDto.getPreferCourt();
        int userCount = matchingConditionDto.getUserCount();
        long groupId = matchingConditionDto.getGroupId();

        for (String matchStartTime : matchStartTimeList) {

            log.info("[로그] : groupId: " + groupId);

            MatchMaking matchMaking = com.capstone.goat.domain.MatchMaking.builder()
                    .sport(sport)
                    .latitude(latitude)
                    .longitude(longitude)
                    .matchingStartTime(matchingStartTime)
                    .matchStartTime(matchStartTime)
                    .preferCourt(preferCourt)
                    .userCount(userCount)
                    .groupId(groupId).build();

            // 조건에 맞는 매칭 중인 유저 검색
            List<MatchMaking> matchMakingList = matchMakingRepository.findByMatching(matchMaking);

            // 검색한 리스트가 비어있으면 다음으로
            if (matchMakingList.size() <= 1) continue;

            int player = sport.getPlayer();

            List<Long> team1 = findSumSubset(matchMakingList, player);
            if (team1.isEmpty()) continue;
            List<Long> team2 = findSumSubset(matchMakingList, player);

            log.info("[로그] : team1: " + team1);
            log.info("[로그] : team2: " + team2);

            if (!team2.isEmpty()) {
                // MatchingRepository에서 제거
                for (long matchedGroupId : team1) {
                    matchMakingRepository.deleteByGroupIdAndLatitudeAndLongitude(matchedGroupId, latitude, longitude);
                }
                for (long matchedGroupId : team2) {
                    matchMakingRepository.deleteByGroupIdAndLatitudeAndLongitude(matchedGroupId, latitude, longitude);
                }

                addGame(team1, team2, matchMaking);

                return;
            }
        }
    }

    private List<Long> findSumSubset(List<MatchMaking> matchMakingList, int target) {
        int n = matchMakingList.size();
        boolean[][] dp = new boolean[n + 1][target + 1];

        // 모든 부분집합은 빈 집합을 포함하기 때문에 true로 초기화
        for (int i = 0; i <= n; i++) {
            dp[i][0] = true;
        }

        // 동적 프로그래밍을 사용하여 부분집합의 합을 계산
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= target; j++) {
                if (matchMakingList.get(i - 1).getUserCount() > j) {
                    dp[i][j] = dp[i - 1][j];
                } else {
                    dp[i][j] = dp[i - 1][j] || dp[i - 1][j - matchMakingList.get(i - 1).getUserCount()];
                }
            }
        }

        // 부분집합을 구성하는 요소를 찾아내기
        List<Long> subset = new ArrayList<>();
        if (dp[n][target]) {
            int i = n, j = target;
            while (i > 0 && j > 0) {
                if (dp[i - 1][j]) {
                    i--;
                } else {
                    subset.add(matchMakingList.get(i - 1).getGroupId());
                    j -= matchMakingList.get(i - 1).getUserCount();
                    matchMakingList.remove(i - 1);
                    i--;
                }
            }
        }

        return subset;
    }

    private void addGame(List<Long> team1GroupId, List<Long> team2GroupId, MatchMaking matchMaking) {

        Game newGame = Game.builder()
                .sport(matchMaking.getSport())
                .startTime(matchMaking.getMatchStartTime())
                .latitude(matchMaking.getLatitude())
                .longitude(matchMaking.getLongitude())
                .court(null)
                .winTeam(0)
                .build();
        Game game = gameRepository.save(newGame);

        for (long groupId: team1GroupId) {
            log.info("[로그] team1 groupId : " + groupId);
            Optional.ofNullable(groupRepository.findUsersById(groupId))
                    .stream().flatMap(Collection::stream)
                    .forEach(user ->
                            teamRepository.save(
                                    Teammate.builder().teamNumber(1).game(game).user(user).build()
                            )
                    );
        }

        for (long groupId: team2GroupId) {
            log.info("[로그] team2 groupId : " + groupId);
            Optional.ofNullable(groupRepository.findUsersById(groupId))
                    .stream().flatMap(Collection::stream)
                    .forEach(user ->
                            teamRepository.save(
                                    Teammate.builder().teamNumber(2).game(game).user(user).build()
                            )
                    );
        }

    }

    @Transactional
    public void deleteMatching(Integer groupId) {

    }

}
