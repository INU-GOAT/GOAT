package com.capstone.goat.dto.response;

import com.capstone.goat.domain.VotedCourt;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "투표된 경기장 정보들 응답 Dto")
public class VotedCourtResponseDto {
    @Schema(description = "경기장")
    private String court;
    @Schema(description = "투표 수")
    private Integer count;

    @Builder
    private VotedCourtResponseDto(String court, int count){
        this.court = court;
        this.count = count;
    }
    public static VotedCourtResponseDto of(VotedCourt votedCourt){
        return VotedCourtResponseDto.builder().court(votedCourt.getCourt()).count(votedCourt.getCount()).build();
    }
}
