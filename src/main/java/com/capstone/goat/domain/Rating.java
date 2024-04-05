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

    private Integer rating; // MMR

    private Integer win;    // 승 수

    private Integer lose;   // 패 수

    private Integer winStreak;  // 연승

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Rating(Sport sport, int rating, int win, int lose, int winStreak, User user) {
        this.sport = sport;
        this.rating = rating;
        this.win = win;
        this.lose = lose;
        this.winStreak = winStreak;
        this.user = user;
    }
}
