package com.capstone.goat.service;

import com.capstone.goat.domain.MatchStartTime;
import com.capstone.goat.domain.Matching;
import com.capstone.goat.dto.response.MatchingResponseDto;
import com.capstone.goat.repository.MatchingRepository;
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

    private static MatchingResponseDto toDto (Matching matching) {

        log.info("[로그] 매칭 조건 검색 시작");

        List<String> matchStartTimeList = matching.getMatchStartTimes().stream().map(MatchStartTime::getStartTime).toList();

        return MatchingResponseDto.of(matching, matchStartTimeList);
    }

    public MatchingResponseDto getMatchingCondition(long groupId) {

        log.info("[로그] 매칭 조건 검색 시작");

        // 매칭 중이 아니면 null 반환
        Optional<Matching> optionalMatching = matchingRepository.findByGroupId(groupId);
        if (optionalMatching.isEmpty())
            return null;

        Matching matching = optionalMatching.get();
        return toDto(matching);
    }
}
