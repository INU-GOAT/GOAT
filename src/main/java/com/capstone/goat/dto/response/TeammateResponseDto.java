package com.capstone.goat.dto.response;

import com.capstone.goat.domain.Teammate;
import com.capstone.goat.domain.User;
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

    public static TeammateResponseDto of(Teammate teammate, User user) {

        return TeammateResponseDto.builder()
                .teamNumber(teammate.getTeamNumber())
                .comment(teammate.getComment())
                .userId(user.getId())
                .userNickname(user.getNickname())
                .build();
    }
}
