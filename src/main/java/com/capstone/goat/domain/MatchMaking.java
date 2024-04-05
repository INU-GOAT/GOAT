package com.capstone.goat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchMaking {

    private Sport sport;

    private Integer userCount;  // 유저 수

    private Integer rating;  // MatchMaking Rating

    private Float latitude;

    private Float longitude;

    private String preferCourt;

    private LocalDateTime matchingStartTime;

    private String matchStartTime;    // 당일 매칭만 잡는 거면 0시부터 24시를 30분 단위로 쪼개어서 표시 ex)0430 1500 등

    private Long groupId;

    @Builder
    public MatchMaking(Sport sport, int userCount, int rating, float latitude, float longitude, String preferCourt, LocalDateTime matchingStartTime, String matchStartTime, long groupId) {
        this.sport = sport;
        this.userCount = userCount;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.preferCourt = preferCourt;
        this.matchingStartTime = matchingStartTime;
        this.matchStartTime = matchStartTime;
        this.groupId = groupId;
    }
}
