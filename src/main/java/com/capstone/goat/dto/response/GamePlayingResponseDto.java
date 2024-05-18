package com.capstone.goat.dto.response;

import com.capstone.goat.domain.Game;
import com.capstone.goat.domain.PreferCourt;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
public class GamePlayingResponseDto {

    private final long gameId;

    private final String sportName;

    private final LocalDateTime startTime;

    private final float latitude;

    private final float longitude;

    private final String court;

    private final List<String> preferCourts;

    private final List<UserInfoDto> team1;

    private final List<UserInfoDto> team2;


    @Builder(access = AccessLevel.PRIVATE)
    private GamePlayingResponseDto(long gameId, String sportName, LocalDateTime startTime, float latitude, float longitude, String court, List<String> preferCourts, List<UserInfoDto> team1, List<UserInfoDto> team2) {
        this.gameId = gameId;
        this.sportName = sportName;
        this.startTime = startTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.court = court;
        this.preferCourts = preferCourts;
        this.team1 = team1;
        this.team2 = team2;
    }

    public static GamePlayingResponseDto of(Game game, List<PreferCourt> preferCourtList, List<UserInfoDto> team1, List<UserInfoDto> team2) {
        List<String> preferCourts = preferCourtList.stream()
                .map(PreferCourt::getCourt)
                .filter(Objects::nonNull)
                .toList();

        return GamePlayingResponseDto.builder()
                .gameId(game.getId())
                .sportName(game.getSport().getName())
                .startTime(game.getStartTime())
                .latitude(game.getLatitude())
                .longitude(game.getLongitude())
                .court(game.getCourt())
                .preferCourts(preferCourts)
                .team1(team1)
                .team2(team2)
                .build();
    }


}
