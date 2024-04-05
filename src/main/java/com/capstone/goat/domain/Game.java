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
    private Long id;

    @Enumerated(EnumType.STRING)
    private Sport sport;

    private String startTime;

    private Float latitude;

    private Float longitude;

    private String court;

    private Integer winTeam;    // 0이면 게임 중 / 1이면 1팀 / 2면 2팀

    @OneToMany(mappedBy = "game",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Teammate> team1;

    @OneToMany(mappedBy = "game",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Teammate> team2;

    @Builder
    public Game(Sport sport, String startTime, float latitude, float longitude, String court, int winTeam) {
        this.sport = sport;
        this.startTime = startTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.court = court;
        this.winTeam = winTeam;
    }
}
