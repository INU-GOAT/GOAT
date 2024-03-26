package com.capstone.goat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    @NotBlank
    @Schema(description = "로그인 아이디",example = "test")
    private String login_id;
    @NotBlank
    @Schema(description = "비밀번호",example = "12345")
    private String password;
}
