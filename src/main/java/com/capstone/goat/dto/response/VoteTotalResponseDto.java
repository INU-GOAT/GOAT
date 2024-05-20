package com.capstone.goat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "투표된 경기장 정보들 응답 Dto")
public class VoteTotalResponseDto {
    @Schema(description = "현재까지 투표된 경기장들의 정보")
    private List<VotedCourtResponseDto> votedCourts;
    @Schema(description = "남은 투표 인원 수")
    private Integer notVotedCount;

    @Builder
    private VoteTotalResponseDto(List<VotedCourtResponseDto> votedCourts, int notVotedCount){
        this.votedCourts = votedCourts;
        this.notVotedCount = notVotedCount;
    }

    public static VoteTotalResponseDto of(List<VotedCourtResponseDto> votedCourts, int notVotedCount){
        return VoteTotalResponseDto.builder().votedCourts(votedCourts).notVotedCount(notVotedCount).build();
    }
}
