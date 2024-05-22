package com.capstone.goat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Sport sport;

    private Integer ratingScore; // MMR 기본값

    private Integer win;    // 승 수

    private Integer lose;   // 패 수

    private Integer draw;   // 비김 수

    private Integer winStreak;  // 연승

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder(access = AccessLevel.PRIVATE)
    private Rating(Sport sport, int ratingScore, int win, int lose, int draw, int winStreak, User user) {
        this.sport = sport;
        this.ratingScore = ratingScore;
        this.win = win;
        this.lose = lose;
        this.draw = draw;
        this.winStreak = winStreak;
        this.user = user;
    }

    // ratingScore 말고 유저 생성 시 등록한 본인의 실력을 받으면 그에 따른 점수를 넣어도 될 듯
    public static Rating initRating(Sport sport, int ratingScore, User user) {
        return Rating.builder()
                .sport(sport)
                .ratingScore(ratingScore)
                .win(0)
                .lose(0)
                .winStreak(0)
                .user(user)
                .build();
    }

    public void updateRating(int result, int feedback) {

        // 승/패/무 횟수 추가
        if (result == 1) win++;
        else if (result == -1) lose++;
        else draw++;

        // 연승 수 조정
        winStreak = (winStreak * result <= 0)
                ? result
                : winStreak + result;

        System.out.println("[로그] ratingScore: " + ratingScore + " , result: " + result + " , winStreak: " + winStreak + " , feedback: " + feedback);

        // rating 점수 조정
        ratingScore += (result * 15) + (winStreak * 5) + (feedback * 10);

        if (ratingScore > 1000) ratingScore = 1000; // Max Value
        else if (ratingScore < 0) ratingScore = 0;  // Min Value
    }
}
