package com.capstone.goat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    private Integer userCount;  // 유저 수

    private Integer rating; // MatchMaking Rating

    @Enumerated(EnumType.STRING)
    private Sport sport;

    private Float latitude;

    private Float longitude;

    private String preferCourt;

    @CreationTimestamp
    private LocalDateTime matchingStartTime;

    @OneToMany(mappedBy = "matching",fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<MatchStartTime> matchStartTimes = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;    // TODO OneToOne 관계니까 Long groupId로 변경해야 함

    @Builder
    public Matching(int userCount, int rating, Sport sport, float latitude, float longitude, String preferCourt, LocalDateTime matchingStartTime, Group group) {
        this.userCount = userCount;
        this.rating = rating;
        this.sport = sport;
        this.latitude = latitude;
        this.longitude = longitude;
        this.preferCourt = preferCourt;
        this.matchingStartTime = matchingStartTime;
        this.group = group;
    }

    public void addMatchStartTimes(List<MatchStartTime> matchStartTimeList) {
        this.matchStartTimes.addAll(matchStartTimeList);
    }
}
