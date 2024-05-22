package com.capstone.goat.service;

import com.capstone.goat.domain.*;
import com.capstone.goat.dto.request.MatchingConditionDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
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
    private final UserRepository userRepository;
    private final GroupService groupService;
    private final GroupRepository groupRepository;
    private final GameRepository gameRepository;
    private final TeammateRepository teammateRepository;
    private final NotificationService notificationService;
    private final VotedCourtRepository votedCourtRepository;

    @Transactional
    public long addMatchingAndMatchMaking(MatchingConditionDto matchingConditionDto, long userId, int rating) {

        // 사용자에게 그룹이 없을 경우 생성
        Group group = groupService.getGroup(userId);

        // 그룹장이 아닌 경우 매칭 시작 불가능
        if (!Objects.equals(group.getMasterId(), userId)) {
            throw new CustomException(CustomErrorCode.MATCHING_ACCESS_DENIED);
        }

        // 그룹원 모두 매칭 중으로 상태 변경, 매칭 대기 상태가 아니면 예외 발생
        group.getMembers().forEach(member -> {
            if (Status.WAITING == member.getStatus()) {
                member.changeStatus(Status.MATCHING);
            } else {
                throw new CustomException(CustomErrorCode.NOT_WAITING_STATE);
            }
        });

        int userCount = group.getMembers().size();
        log.info("[로그] 매칭 DB 추가, userCount = " + userCount);

        // Matching Repository에 저장
        Matching matching = matchingConditionDto.toEntity(rating, group);
        List<MatchStartTime> matchStartTimeList = matchingConditionDto.getMatchStartTimes().stream()
                .map(stringStartTime ->
                        MatchStartTime.builder()
                                .startTime(stringStartTime)
                                .matching(matching)
                                .build()
                )
                .toList();  // Dto의 List<String> matchStartTimes를 List<MatchStartTime>으로 변환
        matching.addMatchStartTimes(matchStartTimeList);
        matchingRepository.save(matching);

        // MatchMaking Repository에 저장
        matchingConditionDto.toMatchMakingList(userCount, rating, group.getId())  // List<MatchMaking>
                .forEach(matchMakingRepository::save);

        return group.getId();
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
                List<PreferCourt> preferCourtList = getPerferCourtList(team1, team2);

                log.info("[로그] : team1GroupId: " + team1GroupId + " team2GroupId: " + team2GroupId);

                // Matching과 MatchMaking에서 매칭된 그룹 제거
                deleteMatchedGroup(team1GroupId, team2GroupId, latitude, longitude);

                // Game에 추가
                Long gameId = addGame(team1GroupId, team2GroupId, matchMaking, preferCourtList);

                // 매칭된 모든 유저를 게임 중으로 상태 변경 및 매칭 완료 알림 전송
                initiateUserGaming(gameId);

                // 매칭된 그룹 모두 해체
                disbandGroupAll(team1GroupId, team2GroupId);

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
                if (!dp[i - 1][j]) {
                    subset.add(matchMakingList.get(i - 1));
                    j -= matchMakingList.get(i - 1).getUserCount();
                    matchMakingList.remove(i - 1);
                }
                i--;
            }
        }

        return subset;
    }

    // MatchMaking 리스트를 중복이 제거된 PreferCourt 리스트로 변환
    private List<PreferCourt> getPerferCourtList(List<MatchMaking> team1, List<MatchMaking> team2) {

        Set<PreferCourt> preferCourtSet = new HashSet<>();

        preferCourtSet.addAll(
            team1.stream().map(matchMaking ->
                    PreferCourt.builder()
                            .court(matchMaking.getPreferCourt())
                            .latitude(matchMaking.getLatitude())
                            .longitude(matchMaking.getLongitude())
                            .build()
            ).toList()
        );
        preferCourtSet.addAll(
                team2.stream().map(matchMaking ->
                        PreferCourt.builder()
                                .court(matchMaking.getPreferCourt())
                                .latitude(matchMaking.getLatitude())
                                .longitude(matchMaking.getLongitude())
                                .build()
                ).toList()
        );

        return new ArrayList<>(preferCourtSet);
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
    private Long addGame(List<Long> team1, List<Long> team2, MatchMaking matchMaking, List<PreferCourt> preferCourtList) {

        log.info("[로그] addGame() 시작");

        // 시작 시간 파싱
        LocalTime matchStartTimeParsed = LocalTime.parse(matchMaking.getMatchStartTime(), DateTimeFormatter.ofPattern("HHmm"));

        // 현재 날짜와 시작 시간을 합쳐서 LocalDateTime 객체 생성
        LocalDateTime matchStartDateTime = LocalDate.now().atTime(matchStartTimeParsed);

        Game newGame = Game.builder()
                .sport(matchMaking.getSport())
                .startTime(matchStartDateTime)
                .build();
        Game game = gameRepository.save(newGame);

        for (long groupId: team1) {
            Optional.ofNullable(groupRepository.findUsersById(groupId))
                    .stream().flatMap(Collection::stream)
                    .forEach(user ->
                            teammateRepository.save(
                                    Teammate.builder().teamNumber(1).game(game).userId(user.getId()).build()
                            )
                    );
        }

        for (long groupId: team2) {
            Optional.ofNullable(groupRepository.findUsersById(groupId))
                    .stream().flatMap(Collection::stream)
                    .forEach(user ->
                            teammateRepository.save(
                                    Teammate.builder().teamNumber(2).game(game).userId(user.getId()).build()
                            )
                    );
        }

        preferCourtList.forEach(preferCourt -> {
            preferCourt.determineGame(game);
            game.addPreferCourt(preferCourt);
            votedCourtRepository.save(VotedCourt.builder().court(preferCourt.getCourt()).game(game).build());
        });

        return game.getId();
    }

    private void initiateUserGaming(Long gameId) {

        // 매칭된 모든 유저를 게임 중으로 상태 변경 및 매칭 완료 알림 전송
        teammateRepository.findUserIdsByGameId(gameId).forEach(userId -> {
            User user = getUser(userId);
            user.changeStatus(Status.GAMING);
            notificationService.sendNotification(null, user.getNickname(), NotificationType.MATCHING);
        });
    }

    // 매칭된 그룹 모두 삭제
    private void disbandGroupAll(List<Long> team1, List<Long> team2) {

        for (long groupId: team1) {
            groupRepository.findById(groupId)
                    .ifPresent(groupService::disbandGroup);
        }

        for (Long groupId : team2) {
            groupRepository.findById(groupId)
                    .ifPresent(groupService::disbandGroup);
        }
    }

    @Transactional
    public void deleteMatching(long userId) {

        User user = getUser(userId);
        Group group = Optional.ofNullable(user.getGroup())
                .orElseThrow(() -> new CustomException(CustomErrorCode.NO_JOINING_GROUP));

        // 그룹장이 아닌 경우 매칭 종료 불가능
        if (!Objects.equals(group.getMasterId(), user.getId()))
            throw new CustomException(CustomErrorCode.MATCHING_ACCESS_DENIED);

        // 매칭 삭제
        long groupId = group.getId();
        Matching foundMatching = matchingRepository.findByGroupId(groupId).
                orElseThrow(() -> new CustomException(CustomErrorCode.NO_MATCHING));
        matchMakingRepository.deleteByGroupIdAndLatitudeAndLongitude(groupId, foundMatching.getLatitude(), foundMatching.getLongitude());
        matchingRepository.deleteByGroupId(groupId);

        // 그룹원 모두 대기 중으로 상태 변경
        group.getMembers().forEach(member -> member.changeStatus(Status.WAITING));

        // 그룹 인원이 1명인 경우 그룹 삭제
        if(group.getMembers().size() == 1){
            groupService.disbandGroup(group);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

}
