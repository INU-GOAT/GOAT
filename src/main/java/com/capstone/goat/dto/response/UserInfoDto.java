package com.capstone.goat.dto.response;

import com.capstone.goat.domain.Sport;
import com.capstone.goat.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfoDto {

    private final long userId;

    private final String nickname;

    private final int ratingScore;

    @Builder(access = AccessLevel.PRIVATE)
    private UserInfoDto(long userId, String nickname, int ratingScore, String comment) {
        this.userId = userId;
        this.nickname = nickname;
        this.ratingScore = ratingScore;
    }

    public static UserInfoDto of(User user, Sport sport) {
        return UserInfoDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .ratingScore(user.getRatings().get(sport).getRatingScore())
                .build();
    }
}