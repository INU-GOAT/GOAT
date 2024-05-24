package com.capstone.goat.dto.response;

import com.capstone.goat.domain.ClubGame;
import com.capstone.goat.domain.Game;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class GameFinishedResponseDto {

    private final long gameId;

    private final String sportName;

    private final LocalDateTime startTime;

    private final String parsedDate;

    private final String parsedTime;

    private final String court;

    private final ClubGame clubGame;

    private final Integer result;

    @Builder(access = AccessLevel.PRIVATE)
    private GameFinishedResponseDto(long gameId, String sportName, LocalDateTime startTime, String court, ClubGame clubGame, Integer result) {
        this.gameId = gameId;
        this.sportName = sportName;
        this.startTime = startTime;
        this.parsedDate = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.parsedTime = startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        this.court = court;
        this.clubGame = clubGame;
        this.result = result;
    }

    public static GameFinishedResponseDto of(Game game, Integer result) {

        return GameFinishedResponseDto.builder()
                .gameId(game.getId())
                .sportName(game.getSport().getName())
                .startTime(game.getStartTime())
                .court(game.getCourt())
                .clubGame(game.getClubGame())
                .result(result)
                .build();
    }
}
