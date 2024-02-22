package com.capstone.goat.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    private String latitude;

    private String longitude;

    @CreationTimestamp
    private LocalDateTime matchingStartTime;

    private LocalDateTime startTime;

    // boolean isSameSex 가 낫지 않나
    private String preferGender;

    private String preferCourt;

    // 그룹 매칭 하려면 group 테이블 생성하고 group id 가져와야 하지 않나?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Matching(Integer id, Sport sport, String latitude, String longitude, LocalDateTime matchingStartTime, LocalDateTime startTime, String preferGender, String preferCourt, User user) {
        this.id = id;
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
