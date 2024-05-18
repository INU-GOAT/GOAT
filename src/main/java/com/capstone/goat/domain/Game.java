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

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PreferCourt> preferCourts = new ArrayList<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VotedWinTeam> votedWinTeams = new ArrayList<>();   // Court 투표로 변경

    @Builder
    public Game(Sport sport, LocalDateTime startTime, float latitude, float longitude, String court) {
        this.sport = sport;
        this.startTime = startTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.court = court;
    }

    public void addPreferCourts(PreferCourt preferCourt) {
        this.preferCourts.add(preferCourt);
    }

    public void determineCourt(String court) {
        this.court = court;
    }

    public void voteWinTeam(VotedWinTeam votedWinTeam) {
        this.votedWinTeams.add(votedWinTeam);
    }
}
