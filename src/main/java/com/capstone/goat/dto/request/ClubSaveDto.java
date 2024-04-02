package com.capstone.goat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClubSaveDto {

    @Schema(description = "클럽명",example = "인천의태양")
    private String name;
}
