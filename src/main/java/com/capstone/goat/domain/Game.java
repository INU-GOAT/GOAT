package com.capstone.goat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private LocalDateTime startTime;

    private Float latitude;

    private Float longitude;

    private String court;

    private Integer winTeam;    // null이면 게임 중 / 1이면 1팀 / 2면 2팀

    @OneToMany(mappedBy = "game",fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Teammate> team1 = new ArrayList<>();

    @OneToMany(mappedBy = "game",fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Teammate> team2 = new ArrayList<>();

    @Builder
    public Game(Sport sport, LocalDateTime startTime, float latitude, float longitude, String court, Integer winTeam) {
        this.sport = sport;
        this.startTime = startTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.court = court;
        this.winTeam = winTeam;
    }

    public void addTeammateToTeam(Teammate teammate) {
        if (teammate.getTeamNumber().equals(1))
            team1.add(teammate);
        else if (teammate.getTeamNumber().equals(2))
            team2.add(teammate);
        else
            throw new IllegalArgumentException("teammate 객체 값이 잘못되어있습니다.");
    }

    public void determineCourt(String court) {
        this.court = court;
    }

    public void determineWinTeam(int winTeam) {
        this.winTeam = winTeam;
    }
}
