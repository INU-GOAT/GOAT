package com.capstone.goat.dto.response;

import com.capstone.goat.domain.Status;
import com.capstone.goat.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "유저 정보 응답 Dto")
@NoArgsConstructor
public class UserResponseDto {
    @Schema(description = "유저의 데이터베이스 아이디값")
    private Long id;
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "나이")
    private Integer age;
    @Schema(description = "성별")
    private String gender;
    @Schema(description = "선호스포츠")
    private String prefer_sport;
    @Schema(description = "가입된 클럽")
    private String club;
    @Schema(description = "가입된 클럽의 데이터베이스 아이디값")
    private Long clubId;
    @Schema(description = "축구 실력")
    private Integer soccer_tier;
    @Schema(description = "배드민턴 실력")
    private Integer badminton_tier;
    @Schema(description = "농구실력")
    private Integer basketball_tier;
    @Schema(description = "탁구실력")
    private Integer tableTennis_tier;
    @Schema(description = "유저상태")
    private Status status;
    @Schema(description = "경기장 투표 여부")
    private Boolean isVoted;
    @Builder
    private UserResponseDto (User user, String club,long clubId){
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.age = user.getAge();
        this.gender = user.getGender();
        this.club = club;
        this.clubId = clubId;
        this.prefer_sport = user.getPrefer_sport();
        this.soccer_tier = user.getSoccer_tier();
        this.badminton_tier = user.getBadminton_tier();
        this.basketball_tier = user.getBasketball_tier();
        this.tableTennis_tier = user.getTableTennis_tier();
        this.status = user.getStatus();
        this.isVoted = user.getIsVoted();
    }

    public static UserResponseDto of(User user,String club, long clubId){
        return  UserResponseDto.builder().user(user).club(club).clubId(clubId).build();
    }

}
