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
    private final List<PreferCourt> preferCourts = new ArrayList<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<VotedCourt> votedCourts = new ArrayList<>();   // Court 투표로 변경

    @Builder
    public Game(Sport sport, LocalDateTime startTime) {
        this.sport = sport;
        this.startTime = startTime;
        this.latitude = null;
        this.longitude = null;
        this.court = null;
    }

    public void addPreferCourt(PreferCourt preferCourt) {
        this.preferCourts.add(preferCourt);
    }

    public void determineCourt(String court, float latitude, float longitude) {
        this.court = court;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
