package com.capstone.goat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PreferCourt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String court;

    private float latitude;

    private float longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Builder
    private PreferCourt(String court, float latitude, float longitude, Game game) {
        this.court = court;
        this.latitude = latitude;
        this.longitude = longitude;
        this.game = game;
    }

    public void determineGame(Game game) {
        this.game = game;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PreferCourt that = (PreferCourt) obj;
        return Objects.equals(court, that.court);
    }

    @Override
    public int hashCode() {
        return Objects.hash(court);
    }
}
