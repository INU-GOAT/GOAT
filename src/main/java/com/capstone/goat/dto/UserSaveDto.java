package com.capstone.goat.dto;

import com.capstone.goat.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class UserSaveDto {
    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    @NotBlank
    private String login_id;

    @NotBlank
    private String password;

    @NotNull
    private Integer age;

    @NotNull
    private Boolean isMan;

    @NotBlank
    private String prefer_sport;

    private Integer soccer_tier;
    private Integer basketball_tier;
    private Integer badminton_tier;
    /*@Builder
    public UserSaveDto(String name, String phone, String login_id, String password , int age,
                       String prefer_sport){
        this.name
    }*/

}
