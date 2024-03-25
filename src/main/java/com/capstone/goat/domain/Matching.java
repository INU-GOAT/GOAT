package com.capstone.goat.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity // TODO 매칭을 배열로 하니까 Entity로 DB에 저장할 필요 없을 듯 @Component로 변경
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

    private String startTime;    // 당일 매칭만 잡는 거면 0시부터 24시를 30분 단위로 쪼개어서 표시 ex)0430 1500 등

    private String preferGender;

    private String preferCourt;

    private Integer userCount;  // 유저 수

    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;*/
    private Integer groupId;

    @Builder
    public Matching(Sport sport, Integer latitude, Integer longitude, LocalDateTime matchingStartTime, String startTime, String preferGender, String preferCourt, Integer userCount, Integer groupId) {
        this.sport = sport;
        this.latitude = latitude;
        this.longitude = longitude;
        this.matchingStartTime = matchingStartTime;
        this.startTime = startTime;
        this.preferGender = preferGender;
        this.preferCourt = preferCourt;
        this.userCount = userCount;
        this.groupId = groupId;
    }
}
