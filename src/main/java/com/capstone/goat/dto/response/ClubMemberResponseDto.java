package com.capstone.goat.dto.response;

import com.capstone.goat.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.PostRemove;

@Getter
@NoArgsConstructor
public class ClubMemberResponseDto {
    @Schema(description ="클럽 회원의 아이디 값")
    private Long userId;
    @Schema(description = "클럽 회원의 닉네임")
    private String nickname;

    @Builder
    private ClubMemberResponseDto(Long userId, String nickname){
        this.userId = userId;
        this.nickname = nickname;
    }

    public static ClubMemberResponseDto of(User user){
        return ClubMemberResponseDto.builder().userId(user.getId()).nickname(user.getNickname()).build();
    }
}
