package com.capstone.goat.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Sport sport;

    private Integer latitude;

    private Integer longitude;

    @CreationTimestamp
    private LocalDateTime matchingStartTime;

    private LocalDateTime startTime;    // 당일 매칭만 잡는 거면 0시부터 24시를 30분 단위로 쪼개어서 표시 ex)0430 1500 등

    private String preferGender;

    private String preferCourt;

    // 그룹 매칭 하려면 group 테이블 생성하고 group id 가져와야 하지 않나?
    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Matching(Sport sport, Integer latitude, Integer longitude, LocalDateTime matchingStartTime, LocalDateTime startTime, String preferGender, String preferCourt, User user) {
        this.sport = sport;
        this.latitude = latitude;
        this.longitude = longitude;
        this.matchingStartTime = matchingStartTime;
        this.startTime = startTime;
        this.preferGender = preferGender;
        this.preferCourt = preferCourt;
        this.user = user;
    }
}
