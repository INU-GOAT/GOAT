package com.capstone.goat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Sport sport;

    @OneToMany(mappedBy = "game",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Team> team1;

    @OneToMany(mappedBy = "game",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Team> team2;

    private String startTime;

    private Integer latitude;

    private Integer longitude;

    private String court;

    private String winTeam;

    @Builder
    public Game(Sport sport, String startTime, Integer latitude, Integer longitude, String court, String winTeam) {
        this.sport = sport;
        this.startTime = startTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.court = court;
        this.winTeam = winTeam;
    }
}
