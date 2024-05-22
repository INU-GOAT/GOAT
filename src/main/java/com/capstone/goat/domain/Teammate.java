package com.capstone.goat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Teammate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer teamNumber;

    private Integer result; // 패: -1, 무: 0, 승: 1

    @Column(columnDefinition = "text")
    private String comment;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Builder
    private Teammate(Integer teamNumber, Integer result, String comment, Long userId, Game game) {
        this.teamNumber = teamNumber;
        this.result = result;
        this.comment = comment;
        this.userId = userId;
        this.game = game;
    }

    public void updateGameReport(Integer result, String comment) {
        this.result = result;
        this.comment = comment;
    }
}
