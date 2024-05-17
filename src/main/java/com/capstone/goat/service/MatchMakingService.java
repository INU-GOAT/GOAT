package com.capstone.goat.service;

import com.capstone.goat.domain.*;
import com.capstone.goat.dto.request.MatchingConditionDto;
import com.capstone.goat.repository.*;
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
    private final TeammateRepository teammateRepository;

    @Transactional
    public void addMatchingAndMatchMaking(MatchingConditionDto matchingConditionDto, long groupId, int rating) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 그룹이 존재하지 않습니다."));
        int userCount = group.getMembers().size();

        log.info("[로그] 매칭 DB 추가, userCount = " + userCount);

        // Matching Repository에 저장
        Matching matching = matchingConditionDto.toEntity(rating, group);
        // Dto의 List<String> matchStartTimes를 List<MatchStartTime>으로 변환
        List<MatchStartTime> matchStartTimeList = matchingConditionDto.getMatchStartTimes().stream().map(stringStartTime ->
            MatchStartTime.builder().startTime(stringStartTime).matching(matching).build()
        ).toList();
        matching.addMatchStartTimes(matchStartTimeList);
        matchingRepository.save(matching);

        // MatchMaking Repository에 저장
        matchingConditionDto.toMatchMakingList(userCount, rating, groupId)  // List<MatchMaking>
                .forEach(matchMakingRepository::save);

        // 그룹원 모두 매칭 중으로 상태 변경
        group.getMembers().forEach(user -> user.changeStatus(Status.MATCHING));
    }

    @Async
    @Transactional
    public void findMatching(MatchingConditionDto matchingConditionDto, long groupId, int rating) {

        for (MatchMaking matchMaking : matchingConditionDto.toMatchMakingList(0, rating, groupId)) {

            log.info("[로그] 매치메이킹 시작, groupId = " + groupId);

            // 조건에 맞는 매칭 중인 유저 검색
            List<MatchMaking> matchMakingList = matchMakingRepository.findByMatching(matchMaking);

            log.info("[로그] 조건에 맞는 매칭 중인 유저, matchMakingList = " + matchMakingList);

            // 검색한 리스트가 비어있으면 다음으로
            if (matchMakingList.size() <= 1) continue;

            float latitude = matchingConditionDto.getLatitude();
            float longitude = matchingConditionDto.getLongitude();
            int player = Sport.getSport(matchingConditionDto.getSport()).getPlayer();

            // 스포츠 인원에 맞는 팀 구성이 되는지 확인
            List<MatchMaking> team1 = findSumSubset(matchMakingList, player);
            if (team1.isEmpty()) continue;
            List<MatchMaking> team2 = findSumSubset(matchMakingList, player);

            if (!team2.isEmpty()) {
                List<Long> team1GroupId = team1.stream().map(MatchMaking::getGroupId).toList();
                List<Long> team2GroupId = team2.stream().map(MatchMaking::getGroupId).toList();
                List<String> preferCourtList = new ArrayList<>();
                preferCourtList.addAll(team1.stream().map(MatchMaking::getPreferCourt).toList());
                preferCourtList.addAll(team2.stream().map(MatchMaking::getPreferCourt).toList());

                log.info("[로그] : team1GroupId: " + team1GroupId + " team2GroupId: " + team2GroupId);

                // Matching과 MatchMaking에서 매칭된 그룹 제거
                deleteMatchedGroup(team1GroupId, team2GroupId, latitude, longitude);

                // Game에 추가
                Long gameId = addGame(team1GroupId, team2GroupId, matchMaking, preferCourtList);

                // 매칭된 모든 유저를 게임 중으로 상태 변경
                changeUserStateToGaming(gameId);

                // 매칭된 그룹 모두 해체
                disbandGroup(team1GroupId, team2GroupId);

                return;
            }
        }
    }

    // 그룹 인원 수의 합이 스포츠 한 팀의 수와 같은 집합 검색
    private List<MatchMaking> findSumSubset(List<MatchMaking> matchMakingList, int target) {

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
        List<MatchMaking> subset = new ArrayList<>();
        if (dp[n][target]) {
            int i = n, j = target;
            while (i > 0 && j > 0) {
                if (dp[i - 1][j]) {
                    i--;
                } else {
                    subset.add(matchMakingList.get(i - 1));
                    j -= matchMakingList.get(i - 1).getUserCount();
                    matchMakingList.remove(i - 1);
                    i--;
                }
            }
        }

        return subset;
    }

    // Matching과 MatchMaking에서 매칭된 그룹 제거
    private void deleteMatchedGroup(List<Long> team1, List<Long> team2, float latitude, float longitude) {

        log.info("[로그] deleteByGroupId 시작");

        for (long groupId : team1) {
            matchMakingRepository.deleteByGroupIdAndLatitudeAndLongitude(groupId, latitude, longitude);
            matchingRepository.deleteByGroupId(groupId);
        }
        for (long groupId : team2) {
            matchMakingRepository.deleteByGroupIdAndLatitudeAndLongitude(groupId, latitude, longitude);
            matchingRepository.deleteByGroupId(groupId);
        }
    }

    // 게임 생성
    private Long addGame(List<Long> team1, List<Long> team2, MatchMaking matchMaking, List<String> preferCourtList) {

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

        for (long groupId: team1) {
            Optional.ofNullable(groupRepository.findUsersById(groupId))
                    .stream().flatMap(Collection::stream)
                    .forEach(user ->
                            teammateRepository.save(
                                    Teammate.builder().teamNumber(1).game(game).user(user).build()
                            )
                    );
        }

        for (long groupId: team2) {
            Optional.ofNullable(groupRepository.findUsersById(groupId))
                    .stream().flatMap(Collection::stream)
                    .forEach(user ->
                            teammateRepository.save(
                                    Teammate.builder().teamNumber(2).game(game).user(user).build()
                            )
                    );
        }

        for (String preferCourtString : preferCourtList) {
            PreferCourt preferCourt = PreferCourt.builder()
                    .court(preferCourtString)
                    .game(game)
                    .build();
            game.addPreferCourts(preferCourt);
        }

        return game.getId();
    }

    private void changeUserStateToGaming(Long gameId) {

        // 매칭된 모든 유저를 게임 중으로 상태 변경
        teammateRepository.findUsersByGameId(gameId).forEach(user -> {
            user.changeStatus(Status.GAMING);
        });
    }

    // 매칭된 그룹 모두 삭제
    private void disbandGroup(List<Long> team1, List<Long> team2) {

        for (long groupId: team1) {
            groupRepository.findById(groupId)
                    .ifPresent(Group::kickAllMembers);
        }

        for (Long groupId : team2) {
            groupRepository.findById(groupId)
                    .ifPresent(Group::kickAllMembers);
        }
    }

    @Transactional
    public void deleteMatching(long groupId) {

        Matching foundMatching = matchingRepository.findByGroupId(groupId).orElseThrow(() -> new NoSuchElementException("해당 그룹 id에 해당하는 매칭이 없습니다. 매칭 중이 아닙니다"));

        matchMakingRepository.deleteByGroupIdAndLatitudeAndLongitude(groupId, foundMatching.getLatitude(), foundMatching.getLongitude());

        matchingRepository.deleteByGroupId(groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 그룹이 존재하지 않습니다."));

        // 그룹원 모두 대기 중으로 상태 변경
        group.getMembers().forEach(user -> user.changeStatus(Status.WAITING));
    }

}
