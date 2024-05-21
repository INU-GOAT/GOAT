package com.capstone.goat.dto.response;

import com.capstone.goat.domain.Club;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ClubResponseDto {

    @Schema(description = "클럽 이름")
    private String name;

    @Schema(description = "클럽장")
    private String clubMaster;

    @Schema(description = "클럽의 스포츠 종목")
    private String sport;
    @Schema(description = "승수")
    private Long win;
    @Schema(description = "패수")
    private Long lose;

    @Schema(description = "인원수")
    private Long memberNumber;

    @Schema(description = "클럽 회원의 데이터베이스아이디 값 명단")
    private List<ClubMemberResponseDto> members;

    @Builder
    private ClubResponseDto(String name, String clubMaster,String sport, Long memberNumber, List<ClubMemberResponseDto> members,long win, long lose){
        this.name = name;
        this.clubMaster = clubMaster;
        this.sport =sport;
        this.memberNumber = memberNumber;
        this.members = members;
        this.win = win;
        this.lose = lose;
    }

    public static ClubResponseDto of(Club club,String clubMaster, List<ClubMemberResponseDto> members){
        return ClubResponseDto.builder()
                .name(club.getName())
                .clubMaster(clubMaster)
                .sport(club.getSport())
                .memberNumber((long) club.getMembers().size())
                .win(club.getWin())
                .lose(club.getLose())
                .members(members)
                .build();
    }

}
