package com.capstone.goat.service;

import com.capstone.goat.domain.*;
import com.capstone.goat.dto.request.MatchingConditionDto;
import com.capstone.goat.repository.GameRepository;
import com.capstone.goat.repository.GroupRepository;
import com.capstone.goat.repository.MatchMakingRepository;
import com.capstone.goat.repository.MatchingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchMakingService {

    private final MatchingRepository matchingRepository;
    private final MatchMakingRepository matchMakingRepository;
    private final GroupRepository groupRepository;
    private final GameRepository gameRepository;

    @Transactional
    public void addMatchingAndMatchMaking(MatchingConditionDto matchingConditionDto, long groupId, int rating) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 그룹이 존재하지 않습니다."));

        // Matching Repository에 저장
        Matching matching = matchingConditionDto.toEntity(rating, group);
        // Dto의 List<String> matchStartTimes를 List<MatchStartTime>으로 변환
        List<MatchStartTime> matchStartTimeList = matchingConditionDto.getMatchStartTimes().stream().map(stringStartTime ->
            MatchStartTime.builder().startTime(stringStartTime).matching(matching).build()
        ).toList();
        matching.addMatchStartTimes(matchStartTimeList);
        matchingRepository.save(matching);

        // MatchMaking Repository에 저장
        matchingConditionDto.toMatchMakingList(rating, groupId)  // List<MatchMaking>
                .forEach(matchMakingRepository::save);
    }

    @Async
    @Transactional
    public void findMatching(MatchingConditionDto matchingConditionDto, long groupId, int rating) {

        for (MatchMaking matchMaking : matchingConditionDto.toMatchMakingList(rating, groupId)) {

            // 조건에 맞는 매칭 중인 유저 검색
            List<MatchMaking> matchMakingList = matchMakingRepository.findByMatching(matchMaking);

            // 검색한 리스트가 비어있으면 다음으로
            if (matchMakingList.size() <= 1) continue;

            float latitude = matchingConditionDto.getLatitude();
            float longitude = matchingConditionDto.getLongitude();
            int player = Sport.getSport(matchingConditionDto.getSport()).getPlayer();

            // 스포츠 인원에 맞는 팀 구성이 되는지 확인
            List<Long> team1 = findSumSubset(matchMakingList, player);
            if (team1.isEmpty()) continue;
            List<Long> team2 = findSumSubset(matchMakingList, player);

            log.info("[로그] : team1: " + team1 + " team2: " + team2);

            if (!team2.isEmpty()) {
                log.info("[로그] deleteByGroupId 시작");
                // matchMakingRepository 및 matchingRepository에서 제거
                for (long matchedGroupId : team1) {
                    matchMakingRepository.deleteByGroupIdAndLatitudeAndLongitude(matchedGroupId, latitude, longitude);
                    matchingRepository.deleteByGroupId(matchedGroupId);
                }
                for (long matchedGroupId : team2) {
                    matchMakingRepository.deleteByGroupIdAndLatitudeAndLongitude(matchedGroupId, latitude, longitude);
                    matchingRepository.deleteByGroupId(matchedGroupId);
                }

                // Game에 추가
                addGame(team1, team2, matchMaking);

                return;
            }
        }
    }

    // 그룹 인원 수의 합이 스포츠 한 팀의 수와 같은 집합 검색
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

    // 게임 생성
    private void addGame(List<Long> team1GroupId, List<Long> team2GroupId, MatchMaking matchMaking) {
        log.info("[로그] addGame() 시작");

        // 시작 시간 파싱
        LocalTime matchStartTimeParsed = LocalTime.parse(matchMaking.getMatchStartTime(), DateTimeFormatter.ofPattern("HHmm"));

        // 현재 날짜와 시작 시간을 합쳐서 LocalDateTime 객체 생성
        LocalDateTime matchStartDateTime = LocalDate.now().atTime(matchStartTimeParsed);

        Game newGame = Game.builder()
                .sport(matchMaking.getSport())
                .startTime(matchStartDateTime)
                .latitude(matchMaking.getLatitude())
                .longitude(matchMaking.getLongitude())
                .court(null)
                .winTeam(null)
                .build();
        Game game = gameRepository.save(newGame);

        for (long groupId: team1GroupId) {
            Optional.ofNullable(groupRepository.findUsersById(groupId))
                    .stream().flatMap(Collection::stream)
                    .forEach(user ->
                            game.addTeammateToTeam(
                                    Teammate.builder().teamNumber(1).game(game).user(user).build()
                            )
                    );
        }

        for (long groupId: team2GroupId) {
            Optional.ofNullable(groupRepository.findUsersById(groupId))
                    .stream().flatMap(Collection::stream)
                    .forEach(user ->
                            game.addTeammateToTeam(
                                    Teammate.builder().teamNumber(2).game(game).user(user).build()
                            )
                    );
        }

    }

    @Transactional
    public void deleteMatching(long groupId) {
        Matching foundMatching = matchingRepository.findByGroupId(groupId).orElseThrow(() -> new NoSuchElementException("해당 그룹 id에 해당하는 매칭이 없습니다. 매칭 중이 아닙니다"));

        matchMakingRepository.deleteByGroupIdAndLatitudeAndLongitude(groupId, foundMatching.getLatitude(), foundMatching.getLongitude());

        matchingRepository.deleteByGroupId(groupId);
    }

}
