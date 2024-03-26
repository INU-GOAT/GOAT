package com.capstone.goat.dto.request;

import com.capstone.goat.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSaveDto {
    @NotBlank
    @Schema(description = "이름",example = "김승섭")
    private String name;

    @NotBlank
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @NotBlank
    @Schema(description = "로그인 아이디",example = "test")
    private String login_id;

    @NotBlank
    @Schema(description = "비밀번호",example = "12345")
    private String password;

    @NotNull
    @Schema(description = "나이")
    private Integer age;

    @NotNull
    @Schema(description = "성별(남자인가?")
    private Boolean isMan;

    @NotBlank
    @Schema(description = "선호 스포츠", example = "soccer")
    private String prefer_sport;

    private Integer soccer_tier;
    private Integer basketball_tier;
    private Integer badminton_tier;

}
