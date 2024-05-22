package com.capstone.goat.dto.response;

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

    private final LocalDateTime startDate;

    private final String parsedDate;

    private final String parsedTime;

    private final String court;

    private final Integer result;

    @Builder(access = AccessLevel.PRIVATE)
    private GameFinishedResponseDto(long gameId, String sportName, LocalDateTime startDate, String court, Integer result) {
        this.gameId = gameId;
        this.sportName = sportName;
        this.startDate = startDate;
        this.parsedDate = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.parsedTime = startDate.format(DateTimeFormatter.ofPattern("HH:mm"));
        this.court = court;
        this.result = result;
    }

    public static GameFinishedResponseDto of(Game game, Integer result) {

        return GameFinishedResponseDto.builder()
                .gameId(game.getId())
                .sportName(game.getSport().getName())
                .startDate(game.getStartTime())
                .court(game.getCourt())
                .result(result)
                .build();
    }
}
