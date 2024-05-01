package com.capstone.goat.dto.response;

import com.capstone.goat.domain.Matching;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MatchingResponseDto {

    private final String sport;

    private final float latitude;

    private final float longitude;

    private final LocalDateTime matchingStartTime;

    private final List<String> matchStartTimes;

    private final String preferCourt;

    private final int userCount;

    @Builder(access = AccessLevel.PRIVATE)
    private MatchingResponseDto(String sport, float latitude, float longitude, LocalDateTime matchingStartTime, List<String> matchStartTimes, String preferCourt, int userCount) {
        this.sport = sport;
        this.latitude = latitude;
        this.longitude = longitude;
        this.matchingStartTime = matchingStartTime;
        this.matchStartTimes = matchStartTimes;
        this.preferCourt = preferCourt;
        this.userCount = userCount;
    }

    public static MatchingResponseDto of(Matching matching, List<String> matchStartTimes) {

        return MatchingResponseDto.builder()
                .sport(matching.getSport().getName())
                .latitude(matching.getLatitude())
                .longitude(matching.getLongitude())
                .matchingStartTime(matching.getMatchingStartTime())
                .matchStartTimes(matchStartTimes)
                .preferCourt(matching.getPreferCourt())
                .userCount(matching.getUserCount())
                .build();
    }

}