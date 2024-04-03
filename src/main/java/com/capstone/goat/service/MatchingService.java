package com.capstone.goat.service;

import com.capstone.goat.domain.Game;
import com.capstone.goat.domain.Matching;
import com.capstone.goat.domain.Sport;
import com.capstone.goat.domain.Team;
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
public class MatchingService {

    private final MatchMakingRepository matchMakingRepository;
    private final GroupRepository groupRepository;
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public void addMatching(MatchingConditionDto matchingConditionDto) {

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
        }

    }

    @Async
    @Transactional
    public void findMatching(MatchingConditionDto matchingConditionDto) {

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

            log.info("[로그] : groupId: " + groupId);

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

            // 조건에 맞는 매칭 중인 유저 검색
            List<Matching> matchingList = matchMakingRepository.findByMatching(matching);

            // 검색한 리스트가 비어있으면 다음으로
            // TODO 1 대신 sport.getPlayer()로 바꿔도 될 듯
            if (matchingList.size() <= 1) continue;

            /*// 투 포인터 알고리즘
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
            }*/

            int player = sport.getPlayer();

            List<Integer> team1 = findSumSubset(matchingList, player);
            if (team1.isEmpty()) continue;
            List<Integer> team2 = findSumSubset(matchingList, player);

            log.info("[로그] : team1: " + team1);
            log.info("[로그] : team2: " + team2);

            if (!team2.isEmpty()) {
                // MatchingRepository에서 제거
                for (Integer matchedGroupId : team1) {
                    matchMakingRepository.deleteByGroupIdAndLatitudeAndLongitude(matchedGroupId, latitude, longitude);
                }
                for (Integer matchedGroupId : team2) {
                    matchMakingRepository.deleteByGroupIdAndLatitudeAndLongitude(matchedGroupId, latitude, longitude);
                }

                addGame(team1, team2, matching);

                return;
            }
        }
    }

    private List<Integer> findSumSubset(List<Matching> matchingList, int target) {
        int n = matchingList.size();
        boolean[][] dp = new boolean[n + 1][target + 1];

        // 모든 부분집합은 빈 집합을 포함하기 때문에 true로 초기화
        for (int i = 0; i <= n; i++) {
            dp[i][0] = true;
        }

        // 동적 프로그래밍을 사용하여 부분집합의 합을 계산
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= target; j++) {
                if (matchingList.get(i - 1).getUserCount() > j) {
                    dp[i][j] = dp[i - 1][j];
                } else {
                    dp[i][j] = dp[i - 1][j] || dp[i - 1][j - matchingList.get(i - 1).getUserCount()];
                }
            }
        }

        // 부분집합을 구성하는 요소를 찾아내기
        List<Integer> subset = new ArrayList<>();
        if (dp[n][target]) {
            int i = n, j = target;
            while (i > 0 && j > 0) {
                if (dp[i - 1][j]) {
                    i--;
                } else {
                    subset.add(matchingList.get(i - 1).getGroupId());
                    j -= matchingList.get(i - 1).getUserCount();
                    matchingList.remove(i - 1);
                    i--;
                }
            }
        }

        return subset;
    }

    private void addGame(List<Integer> team1GroupId, List<Integer> team2GroupId, Matching matching) {

        Game newGame = Game.builder()
                .sport(matching.getSport())
                .startTime(matching.getStartTime())
                .latitude(matching.getLatitude())
                .longitude(matching.getLongitude())
                .court(null)
                .winTeam(null)
                .build();
        Game game = gameRepository.save(newGame);

        for (Integer groupId: team1GroupId) {
            log.info("[로그] team1 groupId : " + groupId);
            Optional.ofNullable(groupRepository.findUsersById(groupId))
                    .stream().flatMap(Collection::stream)
                    .forEach(user ->
                            teamRepository.save(
                                    Team.builder().game(game).user(user).build()
                            )
                    );
        }

        for (Integer groupId: team2GroupId) {
            log.info("[로그] team2 groupId : " + groupId);
            Optional.ofNullable(groupRepository.findUsersById(groupId))
                    .stream().flatMap(Collection::stream)
                    .forEach(user ->
                            teamRepository.save(
                                    Team.builder().game(game).user(user).build()
                            )
                    );
        }


    }

    @Transactional
    public void deleteMatching(Integer groupId) {

    }

}
