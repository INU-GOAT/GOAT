package com.capstone.goat.dto.response;

import com.capstone.goat.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private Long id;

    private String name;

    private String phone;

    private Integer age;

    private Boolean isMan;

    private Long manner_point;
    private Integer manner_count;

    private String prefer_sport;

    private Integer soccer_tier;
    private Integer badminton_tier;
    private Integer basketball_tier;
    @Builder
    private UserResponseDto (User user){
        this.id = user.getId();
        this.age = user.getAge();
        this.isMan = user.getIsMan();
        this.prefer_sport = user.getPrefer_sport();
        this.soccer_tier = user.getSoccer_tier();
        this.badminton_tier = user.getBadminton_tier();
        this.basketball_tier = user.getBasketball_tier();
    }

    public static UserResponseDto of(User user){
        return  UserResponseDto.builder().user(user).build();
    }

}
