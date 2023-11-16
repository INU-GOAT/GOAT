package com.capstone.goat.dto;

import com.capstone.goat.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

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

    @NotBlank
    private int age;

    private int soccer_career;
    private int basketball_career;
    private int badminton_career;
    private String soccer_position;
    private String basketball_position;

    public User toEntity(){
        return User.builder()
                .name(name)
                .phone(phone)
                .login_id(login_id)
                .password(password)
                .age(age)
                .build();
    }

}
