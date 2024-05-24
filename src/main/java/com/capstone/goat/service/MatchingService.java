package com.capstone.goat.service;

import com.capstone.goat.domain.MatchStartTime;
import com.capstone.goat.domain.Matching;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.MatchingConditionDto;
import com.capstone.goat.dto.response.MatchingResponseDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.MatchingRepository;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MatchingService {

    private final MatchingRepository matchingRepository;
    private final MatchMakingService matchMakingService;
    private final UserRepository userRepository;

    private static MatchingResponseDto toDto (Matching matching) {

        log.info("[로그] 매칭 조건 검색 시작");

        List<String> matchStartTimeList = matching.getMatchStartTimes().stream()
                .map(MatchStartTime::getStartTime)
                .toList();

        return MatchingResponseDto.of(matching, matchStartTimeList);
    }

    public MatchingResponseDto getMatchingCondition(long userId) {

        log.info("[로그] 매칭 조건 검색 시작");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
        long groupId = Optional.ofNullable(user.getGroup())
                .orElseThrow(() -> new CustomException(CustomErrorCode.NO_JOINING_GROUP))
                .getId();

        return matchingRepository.findByGroupId(groupId)
                .map(MatchingService::toDto)
                .orElse(null);  // 매칭 중이 아니면 null 반환
    }

    // 일정 주기로 실행할 스케줄링 메서드
    @Scheduled(cron = "0 * * * * *")  // 매 분마다 실행
    @Transactional
    public void matchMakeOldMatching() {
        List<Matching> matchingList = matchingRepository.findOldMatchingList(LocalDateTime.now());

        for (Matching matching : matchingList) {
            // 매칭 조건 Dto 생성
            MatchingConditionDto matchingConditionDto = MatchingConditionDto.of(
                    matching.getSport().getName(),
                    matching.getLatitude(),
                    matching.getLongitude(),
                    matching.getMatchStartTimes().stream().map(MatchStartTime::getStartTime).toList(),
                    matching.getPreferCourt(),
                    matching.getIsClubMatching()
            );

            long minutesElapsed = ChronoUnit.MINUTES.between(matching.getMatchingStartTime(), LocalDateTime.now());
            int matchingRange = (int) (minutesElapsed / 30);
            matchingRange = Math.min(matchingRange, 10);    // 최대 범위 21km x 21km

            log.info("[로그] 재매칭 시작, 매칭 id: {}, 매칭 시작 시간: {}", matching.getId(), matching.getMatchingStartTime());
            matchMakingService.findMatching(
                    matchingConditionDto,
                    matching.getGroup().getId(),
                    matching.getRating(),
                    matchingRange
            );
        }
    }
}
