package com.capstone.goat.dto.response;

import com.capstone.goat.domain.Club;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClubListResponseDto {
    @Schema(description = "클럽의 데이터베이스 아이디 값")
    private Long clubId;
    @Schema(description = "클럽 이름")
    private String name;
    @Schema(description = "클럽의 스포츠 종목")
    private String sport;
    @Schema(description = "승수")
    private Long win;
    @Schema(description = "패수")
    private Long lose;
    @Schema(description = "인원수")
    private Integer memberNumber;

    @Builder
    private ClubListResponseDto(Long clubId, String name, String sport, Long win, Long lose, int memberNumber){
        this.clubId = clubId;
        this.name = name;
        this.sport = sport;
        this.win = win;
        this.lose = lose;
        this.memberNumber = memberNumber;
    }

    public static ClubListResponseDto of(Club club){
        return ClubListResponseDto.builder().clubId(club.getId()).name(club.getName()).sport(club.getSport()).win(club.getWin()).lose(club.getLose()).memberNumber(club.getMembers().size()).build();
    }

}
