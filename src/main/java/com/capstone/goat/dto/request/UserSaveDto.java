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

    @NotNull
    @Schema(description = "나이")
    private Integer age;

    @NotNull
    @Schema(description = "성별")
    private String gender;

    @NotNull
    @Schema(description = "닉네임")
    private String nickname;

    @NotBlank
    @Schema(description = "선호 스포츠", example = "soccer")
    private String prefer_sport;

    @Schema(description = "축구 실력")
    private Integer soccer_tier;
    @Schema(description = "농구 실력")
    private Integer basketball_tier;
    @Schema(description = "배드민턴 실력")
    private Integer badminton_tier;
    @Schema(description = "테니스 실력")
    private Integer tableTennis_tier;

}
