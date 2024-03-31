package com.capstone.goat.dto.response;

import com.capstone.goat.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private Long id;

    private String nickname;

    private Integer age;

    private String gender;

    private String prefer_sport;

    private Integer soccer_tier;
    private Integer badminton_tier;
    private Integer basketball_tier;
    @Builder
    private UserResponseDto (User user){
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.age = user.getAge();
        this.gender = user.getGender();
        this.prefer_sport = user.getPrefer_sport();
        this.soccer_tier = user.getSoccer_tier();
        this.badminton_tier = user.getBadminton_tier();
        this.basketball_tier = user.getBasketball_tier();
    }

    public static UserResponseDto of(User user){
        return  UserResponseDto.builder().user(user).build();
    }

}
