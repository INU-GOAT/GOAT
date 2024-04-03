package com.capstone.goat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClubUpdateDte {
    @Schema(description = "클럽이름")
    private String name;

    @Schema(description = "클럽 스포츠 종목")
    private String sport;
}
