package com.capstone.goat.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameCourtDto {

    @NotBlank(message = "court는 공백으로 입력할 수 없습니다.")
    private String court;
}
