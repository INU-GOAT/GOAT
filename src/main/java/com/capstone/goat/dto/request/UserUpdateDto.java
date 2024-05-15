package com.capstone.goat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    @NotNull
    @Schema(description = "닉네임")
    private String nickname;
    @NotNull
    @Schema(description = "나이")
    private Integer age;

    @NotNull
    @Schema(description = "성별")
    private String gender;

    @NotBlank
    @Schema(description = "선호 스포츠", example = "soccer")
    private String prefer_sport;
}
