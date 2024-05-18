package com.capstone.goat.service;

import com.capstone.goat.domain.MatchStartTime;
import com.capstone.goat.domain.Matching;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.response.MatchingResponseDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.GroupRepository;
import com.capstone.goat.repository.MatchingRepository;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MatchingService {

    private final MatchingRepository matchingRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

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
}
