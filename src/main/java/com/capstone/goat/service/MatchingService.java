package com.capstone.goat.service;

import com.capstone.goat.domain.MatchStartTime;
import com.capstone.goat.domain.Matching;
import com.capstone.goat.dto.response.MatchingResponseDto;
import com.capstone.goat.repository.MatchingRepository;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final MatchingRepository matchingRepository;
    private final UserRepository userRepository;

    private static MatchingResponseDto toDto (Matching matching) {

        List<String> matchStartTimeList = matching.getMatchStartTimes().stream().map(MatchStartTime::getStartTime).toList();

        return MatchingResponseDto.of(matching, matchStartTimeList);
    }

    public MatchingResponseDto getMatchingCondition(long groupId) {

        // 매칭 중이 아니면 null 반환
        Optional<Matching> optionalMatching = matchingRepository.findByGroupId(groupId);
        if (optionalMatching.isEmpty())
            return null;

        Matching matching = optionalMatching.get();
        return toDto(matching);
    }
}
