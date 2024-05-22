package com.capstone.goat.dto.response;

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

    public static UserInfoDto of(Long userId, String userNickname, int ratingScore) {
        return UserInfoDto.builder()
                .userId(userId)
                .nickname(userNickname)
                .ratingScore(ratingScore)
                .build();
    }
}