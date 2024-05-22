package com.capstone.goat.dto.response;

import com.capstone.goat.domain.Teammate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TeammateResponseDto {

    private final Integer teamNumber;

    private final String comment;

    private final Long userId;

    private final String userNickname;

    @Builder(access = AccessLevel.PRIVATE)
    private TeammateResponseDto(Integer teamNumber, String comment, Long userId, String userNickname) {
        this.teamNumber = teamNumber;
        this.comment = comment;
        this.userId = userId;
        this.userNickname = userNickname;
    }

    public static TeammateResponseDto of(Teammate teammate, Long userId, String userNickname) {

        return TeammateResponseDto.builder()
                .teamNumber(teammate.getTeamNumber())
                .comment(teammate.getComment())
                .userId(userId)
                .userNickname(userNickname)
                .build();
    }
}
