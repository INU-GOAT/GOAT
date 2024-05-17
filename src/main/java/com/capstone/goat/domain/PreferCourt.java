package com.capstone.goat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PreferCourt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String court;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Builder
    private PreferCourt(String court, Game game) {
        this.court = court;
        this.game = game;
    }
}
