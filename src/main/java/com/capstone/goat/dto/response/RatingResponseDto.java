package com.capstone.goat.dto.response;

import com.capstone.goat.domain.Rating;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RatingResponseDto {

    private final String sportName;

    private final int ratingScore;

    private final int win;

    private final int lose;

    private final int winStreak;

    @Builder(access = AccessLevel.PRIVATE)
    private RatingResponseDto(String sportName, int ratingScore, int win, int lose, int winStreak) {
        this.sportName = sportName;
        this.ratingScore = ratingScore;
        this.win = win;
        this.lose = lose;
        this.winStreak = winStreak;
    }

    public static RatingResponseDto from(Rating rating) {
        return RatingResponseDto.builder()
                .sportName(rating.getSport().getName())
                .ratingScore(rating.getRatingScore())
                .win(rating.getWin())
                .lose(rating.getLose())
                .winStreak(rating.getWinStreak())
                .build();
    }
}
