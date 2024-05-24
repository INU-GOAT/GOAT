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
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating; // MatchMaking Rating

    @Enumerated(EnumType.STRING)
    private Sport sport;

    private Double latitude;

    private Double longitude;

    private String preferCourt;

    private Boolean isClubMatching;

    private LocalDateTime matchingStartTime;

    @OneToMany(mappedBy = "matching",fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<MatchStartTime> matchStartTimes = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;    // TODO OneToOne 관계니까 Long groupId로 변경해야 함

    @Builder
    public Matching(int rating, Sport sport, double latitude, double longitude, String preferCourt, Boolean isClubMatching, LocalDateTime matchingStartTime, Group group) {
        this.rating = rating;
        this.sport = sport;
        this.latitude = latitude;
        this.longitude = longitude;
        this.preferCourt = preferCourt;
        this.isClubMatching = isClubMatching;
        this.matchingStartTime = matchingStartTime;
        this.group = group;
    }

    public void addMatchStartTimes(List<MatchStartTime> matchStartTimeList) {
        this.matchStartTimes.addAll(matchStartTimeList);
    }
}
