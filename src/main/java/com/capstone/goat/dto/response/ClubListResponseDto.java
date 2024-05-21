package com.capstone.goat.dto.response;

import com.capstone.goat.domain.Club;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClubListResponseDto {
    @Schema(description = "클럽 이름")
    private String name;
    @Schema(description = "클럽의 스포츠 종목")
    private String sport;
    @Schema(description = "승수")
    private Long win;
    @Schema(description = "패수")
    private Long lose;
    @Schema(description = "인원수")
    private Long memberNumber;

    @Builder
    private ClubListResponseDto(String name, String sport, long win, long lose, long memberNumber){
        this.name = name;
        this.sport = sport;
        this.win = win;
        this.lose = lose;
        this.memberNumber = memberNumber;
    }

    public static ClubListResponseDto of(Club club){
        return ClubListResponseDto.builder().name(club.getName()).sport(club.getSport()).win(club.getWin()).lose(club.getLose()).memberNumber(club.getMembers().size()).build();
    }

}
